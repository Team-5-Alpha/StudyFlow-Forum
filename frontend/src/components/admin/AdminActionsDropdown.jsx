import { useState } from "react";

import "../../styles/components/common/FormControls.css";
import "../../styles/components/admin/AdminActionsDropdown.css";

export default function AdminActionsDropdown({
  profile,
  onBlock,
  onUnblock,
  onPromote
}) {
  const [open, setOpen] = useState(false);

  return (
    <div className="admin-actions-container">
      <button className="red-btn" onClick={() => setOpen(!open)}>
        Admin Actions
      </button>

      {open && (
        <div className="admin-dropdown">

          {!profile.blocked ? (
            <button className="red-btn" onClick={onBlock}>
              Block User
            </button>
          ) : (
            <button className="red-btn" onClick={onUnblock}>
              Unblock User
            </button>
          )}

          {profile.role !== "ADMIN" && (
            <button className="red-btn" onClick={onPromote}>
              Promote to Admin
            </button>
          )}

        </div>
      )}
    </div>
  );
}
