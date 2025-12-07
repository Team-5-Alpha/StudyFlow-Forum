import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import '../../styles/components/layout/Navbar.css';
import "../../styles/components/common/FormControls.css";

import logo from "../../assets/images/logo-s.png";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate("/login");
  }

  return (
    <div className="navbar-container">
      <div className="navbar">
        <div className="navbar-left">
            <img
              src={logo}
              alt="StudyFlow Logo"
              className="navbar-logo"
              onClick={() => navigate("/")}
            />
        </div>
        <div className="navbar-right">
            {user?.role === "ADMIN" && (
              <button className="primary-btn" onClick={() => navigate("/admin/users")}>
                Search
              </button>
            )}
            {!user && (
              <>
                <button className="primary-btn" onClick={() => navigate("/login")}>Login</button>
                <button className="primary-btn" onClick={() => navigate("/register")}>Register</button>
              </>
            )}
            {user && (
              <>
                <button className="primary-btn" onClick={() => navigate(`/users/${user.id}`)}>
                  Profile
                </button>
                <button className="primary-btn" onClick={handleLogout}>
                  Logout
                </button>
              </>
            )}
        </div>
      </div>
    </div>
  );
}
