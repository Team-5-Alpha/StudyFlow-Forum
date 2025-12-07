import { useNavigate } from "react-router-dom";
import AdminActionsDropdown from "./AdminActionsDropdown";
import { useUserAdminActions } from "../../hooks/user/useUserAdminActions";

export default function AdminUsersList({ users, actingUser, onUpdate }) {
  const navigate = useNavigate();

  if (!users || users.length === 0) {
    return <p>No users found.</p>;
  }

  function renderAvatar(u) {
    if (u.profilePhotoUrl) {
      return <img className="admin-user-avatar-img" src={u.profilePhotoUrl} alt="Profile" />;
    }
    const initial = u.username ? u.username.charAt(0).toUpperCase() : "?";
    return <div className="admin-user-avatar-fallback">{initial}</div>;
  }

  return (
    <div className="admin-users-list">
      {users.map((u) => (
        <div
          key={u.id}
          className="card admin-user-row clickable"
          onClick={() => navigate(`/users/${u.id}`)}
        >
          <div className="admin-user-avatar">{renderAvatar(u)}</div>

          <div className="admin-user-main">
            <div className="admin-user-name">@{u.username}</div>
            <div className="admin-user-names">{u.firstName} {u.lastName}</div>
            <div className="admin-user-email">{u.email}</div>
          </div>
          <div className="admin-user-meta">
            <span className={`admin-badge ${u.role === "ADMIN" ? "admin-badge-admin" : ""}`}>
              {u.role}
            </span>
            <span className={`admin-badge ${u.blocked ? "admin-badge-blocked" : "admin-badge-active"}`}>
              {u.blocked ? "Blocked" : "Active"}
            </span>

            {actingUser?.role === "ADMIN" && actingUser.id !== u.id && (
              <AdminQuickActions user={u} onUpdate={onUpdate} />
            )}
          </div>
        </div>
      ))}
    </div>
  );
}

function AdminQuickActions({ user, onUpdate }) {
  const setListProfile = (updater) => {
    if (!onUpdate) return;
    onUpdate(user.id, updater);
  };

  const adminActions = useUserAdminActions(user.id, setListProfile);

  return (
    <div onClick={(e) => e.stopPropagation()}>
      <AdminActionsDropdown
        profile={user}
        onBlock={adminActions.block}
        onUnblock={adminActions.unblock}
        onPromote={adminActions.promote}
      />
    </div>
  );
}
