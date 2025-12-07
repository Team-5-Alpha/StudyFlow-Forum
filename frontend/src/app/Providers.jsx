import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "../context/AuthContext";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function Providers({ children }) {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastContainer position="top-right" autoClose={2000} />
        {children}
      </AuthProvider>
    </BrowserRouter>
  );
}