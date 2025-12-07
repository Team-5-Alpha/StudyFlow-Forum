import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useUserEdit } from "../../hooks/user/useUserEdit";

import Input from "../../components/common/Input";
import Spinner from "../../components/common/Spinner";
import FieldLengthHint from "../../components/common/FieldLengthHint";

export default function UserEditPage() {
  const { user: actingUser, setUser } = useAuth();
  const { userId } = useParams();
  const navigate = useNavigate();

  const isAdmin = actingUser.role === "ADMIN";

  const { form, setForm, loading, saving, error, save } =
    useUserEdit(userId, isAdmin);

  async function handleSubmit(e) {
    e.preventDefault();
    const ok = await save();

    if (ok) {
      if (Number(userId) === actingUser.id) {
        setUser((prev) => ({ ...prev }));
      }
      navigate(`/users/${userId}`);
    }
  }

  if (loading) return <Spinner />;

  return (
    <div>
      <div>
        <h2>Edit Profile</h2>

        {error && <div className="error-banner">{error}</div>}

        <form onSubmit={handleSubmit}>
          <Input
            label="First Name"
            value={form.firstName}
            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
          />
          <FieldLengthHint value={form.firstName} min={4} max={32} />

          <Input
            label="Last Name"
            value={form.lastName}
            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
          />
          <FieldLengthHint value={form.lastName} min={4} max={32} />

          <Input
            label="Email"
            type="email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
          <FieldLengthHint value={form.email} min={6} max={128} />

          {isAdmin && (
            <>
              <Input
                label="Phone Number"
                value={form.phoneNumber}
                onChange={(e) =>
                  setForm({ ...form, phoneNumber: e.target.value })
                }
              />
            </>
          )}

          <Input
            label="New Password (optional)"
            type="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
          <FieldLengthHint value={form.password} min={6} max={128} />

          <Input
            label="Profile Photo URL"
            value={form.profilePhotoURL}
            onChange={(e) =>
              setForm({ ...form, profilePhotoURL: e.target.value })
            }
          />
          <FieldLengthHint value={form.profilePhotoURL} min={0} max={255} />

          <button className="primary-btn" type="submit" disabled={saving}>
            {saving ? "Saving..." : "Save Changes"}
          </button>
        </form>
      </div>
    </div>
  );
}
