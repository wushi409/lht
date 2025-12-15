import { getToken } from './auth';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const buildHeaders = (isJson: boolean) => {
  const token = getToken();
  const headers: Record<string, string> = {};
  if (isJson) headers['Content-Type'] = 'application/json';
  if (token) headers.Authorization = `Bearer ${token}`;
  return headers;
};

async function handleResponse<T>(res: Response): Promise<T> {
  const json = await res.json();
  if (!res.ok || json?.success === false) {
    const message = json?.message || `请求失败 (${res.status})`;
    throw new Error(message);
  }
  return json.data as T;
}

export async function apiGet<T>(path: string, options: RequestInit = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'GET',
    headers: { ...buildHeaders(false), ...(options.headers || {}) },
    ...options,
  });
  return handleResponse<T>(res);
}

export async function apiPost<T>(path: string, body?: any) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: buildHeaders(!(body instanceof FormData)),
    body: body instanceof FormData ? body : body ? JSON.stringify(body) : undefined,
  });
  return handleResponse<T>(res);
}

export async function apiPut<T>(path: string, body?: any) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'PUT',
    headers: buildHeaders(!(body instanceof FormData)),
    body: body instanceof FormData ? body : body ? JSON.stringify(body) : undefined,
  });
  return handleResponse<T>(res);
}

export async function apiDelete<T>(path: string) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'DELETE',
    headers: buildHeaders(false),
  });
  return handleResponse<T>(res);
}
