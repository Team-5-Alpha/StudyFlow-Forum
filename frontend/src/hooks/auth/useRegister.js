import { useState } from "react";
import { register as registerRequest } from "../../services/authService";
import { useAuth } from "../../context/AuthContext";

export function useRegister() {
  const { setUser } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function submit(form) {
    setLoading(true);
    setError("");

    try {
      const res = await registerRequest(form);
      const userData = res.data;

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
      setError(err.message || "Registration failed.");
      return false;
    } finally {
      setLoading(false);
    }
  }

  return { submit, loading, error };
}
