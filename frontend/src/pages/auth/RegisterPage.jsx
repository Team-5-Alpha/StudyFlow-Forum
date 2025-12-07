import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { useRegister } from "../../hooks/auth/useRegister";
import Input from "../../components/common/Input";
import FieldLengthHint from "../../components/common/FieldLengthHint";
import "../../styles/components/common/FormControls.css";

export default function RegisterPage() {
  const [form, setForm] = useState({
    username: "",
    firstName: "",
    lastName: "",
    email: "",
    password: ""
  });

  const { submit, loading, error } = useRegister();
  const navigate = useNavigate();

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const ok = await submit(form);
  if (ok) navigate("/");
}

  return (
    <div>
      <div>
        <h2>Register</h2>

        {error && <div className="error-banner">{error}</div>}

        <form onSubmit={handleSubmit}>
          <Input label="Username" name="username" value={form.username} onChange={change} />
          <FieldLengthHint value={form.username} min={4} max={32} />

          <Input label="First Name" name="firstName" value={form.firstName} onChange={change} />
          <FieldLengthHint value={form.firstName} min={4} max={32} />

          <Input label="Last Name" name="lastName" value={form.lastName} onChange={change} />
          <FieldLengthHint value={form.lastName} min={4} max={32} />

          <Input label="Email" name="email" type="email" value={form.email} onChange={change} />
          <FieldLengthHint value={form.email} min={6} max={128} />

          <Input label="Password" name="password" type="password" value={form.password} onChange={change} />
          <FieldLengthHint value={form.password} min={6} max={128} />

          <button className="primary-btn" type="submit" disabled={loading}>
            {loading ? "Registering..." : "Register"}
          </button>
        </form>
      </div>
    </div>
  );
}
