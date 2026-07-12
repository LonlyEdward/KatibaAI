import { useState } from "react";
import { AuthContext } from "./AuthContext";

export function AuthProvider({ children }) {
  //   const [user, setUser] = useState(null);

  const [user, setUser] = useState({
    id: "1",
    username: "Nelly",
    email: "nelly@example.com",
  });

  const login = (userData) => {
    setUser(userData);
  };

  const logout = () => {
    setUser(null);
  };

  const value = {
    user,

    login,

    logout,

    isAuthenticated: user !== null,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
