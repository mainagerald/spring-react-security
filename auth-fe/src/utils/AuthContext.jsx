import React, { createContext, useState, useContext, ReactNode, useEffect } from 'react';
import axiosInstance from '../utils/AxiosInstance';
import { environment } from '../service/environment';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      validateToken(token);
    }
  }, []);

  const validateToken = async (token) => {
    try {
      const response = await axiosInstance.post(`${environment.apiUrl}/auth/validate-token`, {accessToken:token},{
        headers: { Authorization: `Bearer ${token}` }
      });
      console.log("Validate token response:", response);
      if (response.data === true) {
        const decodedToken = jwtDecode(token);
        setUser({
          id: decodedToken.userId,
          email: decodedToken.sub,
          publicId: decodedToken.publicId,
          authorities: decodedToken.authorities
        });
        setIsAuthenticated(true);
      }
    } catch (error) {
      logout();
    }
  };

  const login = async (email, password) => {
    try {
      const response = await axiosInstance.post(`${environment.apiUrl}/auth/signin`, { email, password });
      const { accessToken, refreshToken } = response.data;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);

      const decodedToken = jwtDecode(accessToken);

      setUser({
        id: decodedToken.userId,
        email: decodedToken.sub,
        publicId: decodedToken.publicId,
        authorities: decodedToken.authorities
      });

      setIsAuthenticated(true);
    } catch (error) {
      setIsAuthenticated(false);
      setUser(null);
      throw error;
    }
  };

  const oauthLogin = (accessToken, refreshToken) => {
    const decodedToken = jwtDecode(accessToken);

    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);

    setUser({
      id: decodedToken.userId,
      email: decodedToken.sub,
      publicId: decodedToken.publicId,
      authorities: decodedToken.authorities
    });

    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
    setIsAuthenticated(false);
  };

  const signup = async (email, password) => {
    try {
      const response = await axiosInstance.post(`${environment.apiUrl}/auth/signup`, { email, password });
      const { accessToken, refreshToken } = response.data;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);

      const decodedToken = jwtDecode(accessToken);

      setUser({
        id: decodedToken.userId,
        email: decodedToken.sub,
        publicId: decodedToken.publicId,
        authorities: decodedToken.authorities
      });

      setIsAuthenticated(true);
    } catch (error) {
      setIsAuthenticated(false);
      setUser(null);
      throw error;
    }
  };

  const contextValue = {
    user,
    login,
    logout,
    signup,
    oauthLogin,
    isAuthenticated
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
