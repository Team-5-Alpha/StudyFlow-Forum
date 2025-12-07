import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import Spinner from "../common/Spinner";

export default function AdminRoute({ children }) {
  const { user, authLoading } = useAuth();

  if (authLoading) return <Spinner />;
  if (!user) return <Navigate to="/login" replace />;
  if (user.role !== "ADMIN") return <Navigate to="/" replace />;

  return children;
}