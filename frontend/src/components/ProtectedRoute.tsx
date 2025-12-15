import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../lib/auth';

export const ProtectedRoute: React.FC<{ children: React.ReactNode; roles?: string[] }> = ({
  children,
  roles,
}) => {
  const { auth } = useAuth();
  const location = useLocation();

  if (!auth.token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && auth.role && !roles.includes(auth.role)) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};
