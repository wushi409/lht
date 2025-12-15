import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';

type AuthState = {
  token: string | null;
  role: string | null;
};

type AuthContextValue = {
  auth: AuthState;
  setAuth: (next: AuthState) => void;
  logout: () => void;
};

const STORAGE_KEY = 'jobfair_auth';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const getInitialAuth = (): AuthState => {
  if (typeof window === 'undefined') return { token: null, role: null };
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return { token: null, role: null };
    const parsed = JSON.parse(raw) as AuthState;
    return parsed;
  } catch (e) {
    console.warn('Failed to parse auth storage', e);
    return { token: null, role: null };
  }
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [auth, setAuthState] = useState<AuthState>(getInitialAuth);

  useEffect(() => {
    if (auth.token) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(auth));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [auth]);

  const value = useMemo<AuthContextValue>(
    () => ({
      auth,
      setAuth: (next) => setAuthState(next),
      logout: () => setAuthState({ token: null, role: null }),
    }),
    [auth],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};

export const getToken = () => getInitialAuth().token;
export const getRole = () => getInitialAuth().role;
