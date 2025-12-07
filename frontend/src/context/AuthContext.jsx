import { createContext, useContext, useEffect, useState } from "react";
import { getCurrentUser, logout as apiLogout } from "../services/authService";
import { useLocation } from "react-router-dom";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
  if (location.pathname === "/login" || location.pathname === "/register") {
    setAuthLoading(false);
    return;
  }

  async function init() {
    try {
      const res = await getCurrentUser();
      if (res.meta?.success) {
        setUser({
          id: res.data.id,
          username: res.data.username,
          role: res.data.role,
        });
      } else {
        setUser(null);
      }
    } catch {
      setUser(null);
    } finally {
      setAuthLoading(false);
    }
  }

  init();
}, [location.pathname]);

  async function logout() {
    await apiLogout();
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, setUser, logout, authLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
