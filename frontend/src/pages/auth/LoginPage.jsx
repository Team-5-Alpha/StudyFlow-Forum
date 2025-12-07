import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { useLogin } from "../../hooks/auth/useLogin";
import Input from "../../components/common/Input";

import "../../styles/components/common/FormControls.css";
import "../../styles/pages/auth/LoginPage.css";

export default function LoginPage() {
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");

  const { submit, loading, error } = useLogin();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    const ok = await submit(identifier, password);
    if (ok) navigate("/");
  }

  return (
    <div className="login-page">
      <div>
        <h2>Login</h2>

        {error && <div className="error-banner">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          <Input
            label="Username or Email"
            value={identifier}
            onChange={(e) => setIdentifier(e.target.value)}
          />

          <Input
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button className="primary-btn">
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>
      </div>
    </div>
  );
}
