import { createContext, useContext, useEffect, useState, useCallback } from 'react';
import axiosClient from '../api/axiosClient';
import apolloClient from '../api/apolloClient';
import {
  setTokens,
  clearTokens,
  getAccessToken,
  getRefreshToken,
  subscribe,
} from '../api/tokenStore';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(!!getAccessToken());
  const [user, setUser] = useState(null);

  useEffect(() => {
    const unsubscribe = subscribe(() => {
      setIsAuthenticated(!!getAccessToken());
    });
    return unsubscribe;
  }, []);

  const register = useCallback(async (username, email, password) => {
    const res = await axiosClient.post('/api/auth/register', {
      username,
      email,
      password,
    });
    if (!res.data.success) {
      throw new Error(res.data.message || 'Registration failed');
    }
    setTokens(res.data.token, res.data.refreshToken);
    await apolloClient.clearStore();
    setUser({
      username: res.data.username,
      email: res.data.email,
      role: res.data.role,
    });
  }, []);

  const login = useCallback(async (email, password) => {
    const res = await axiosClient.post('/api/auth/login', { email, password });
    if (!res.data.success) {
      throw new Error(res.data.message || 'Login failed');
    }
    setTokens(res.data.token, res.data.refreshToken);
    setUser({
      username: res.data.username,
      email: res.data.email,
      role: res.data.role,
    });
  }, []);

  const logout = useCallback(async () => {
    const refreshToken = getRefreshToken();
    try {
      if (refreshToken) {
        await axiosClient.post('/api/auth/logout', { refreshToken });
      }
    } catch {
    } finally {
      clearTokens();
      await apolloClient.clearStore();
      setUser(null);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, register, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
