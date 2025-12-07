import { useEffect, useState, useCallback } from "react";
import Spinner from "../common/Spinner";
import UserSummary from "./UserSummary";
import api from "../../api/axios";
import { normalizeUserSummary } from "../../utils/followUtils";

import "../../styles/components/users/UserConnectionsModal.css";

export default function UserConnectionsModal({
  userId,
  mode,
  isOpen,
  onClose,
}) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  const isFollowers = mode === "followers";
  const title = isFollowers ? "Followers" : "Following";

  const load = useCallback(async () => {
    try {
      setLoading(true);

      const url = isFollowers
        ? `/api/private/users/${userId}/followers`
        : `/api/private/users/${userId}/following`;

      const res = await api.get(url);
      setItems((res.data || []).map(normalizeUserSummary));
    } finally {
      setLoading(false);
    }
  }, [isFollowers, userId]);

  useEffect(() => {
    if (isOpen && userId) load();
  }, [userId, mode, isOpen, load]);

  if (!isOpen) return null;

  return (
    <div className="uc-modal-backdrop" onClick={onClose}>
      <div
        className="uc-modal"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="uc-modal-header">
          <h2>{title}</h2>
          <button className="close-btn" onClick={onClose}>
            ✕
          </button>
        </div>

        <div className="uc-modal-body">
          {loading ? (
            <Spinner />
          ) : items.length === 0 ? (
            <p className="other-info">
              {isFollowers ? "No followers yet." : "This user follows no one."}
            </p>
          ) : (
            items.map((user) => (
              <UserSummary
                key={user.id}
                user={user}
                compact={true}
                showFollowButton={true}
                onChange={load}
                onClickUser={onClose}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
}
