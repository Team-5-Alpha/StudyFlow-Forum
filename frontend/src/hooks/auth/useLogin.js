import { useState } from "react";
import { login as loginRequest } from "../../services/authService";
import { useAuth } from "../../context/AuthContext";

export function useLogin() {
  const { setUser } = useAuth();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function submit(identifier, password) {
    setLoading(true);
    setError("");

    try {
      const response = await loginRequest(identifier, password);
      const userData = response.data;

      if (!userData) {
        setError("Invalid server response.");
        return false;
      }

      setUser({
        id: userData.id,
        username: userData.username,
        role: userData.role
      });

      return true;
    } catch (err) {
      setError(err.message || "Login failed.");
      return false;
    } finally {
      setLoading(false);
    }
  }

  return { submit, loading, error };
}
