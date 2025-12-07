import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import FollowButton from "../common/FollowButton";
import { useState, useEffect, useMemo } from "react";
import {
  applyFollowState,
  normalizeUserSummary,
  resolveIsFollowed,
} from "../../utils/followUtils";

import "../../styles/components/users/UserSummary.css";
import "../../styles/components/common/FormControls.css";

export default function UserSummary({
  user,
  onChange,
  onClickUser,
  compact = false,
  showFollowButton = true,
  onError,
  profileUpdater,
}) {
  const { user: actingUser } = useAuth();
  const navigate = useNavigate();

  const normalizedUser = useMemo(
    () => normalizeUserSummary(user),
    [user]
  );

  const isSelf = actingUser?.id === normalizedUser.id;

  const [isFollowed, setIsFollowed] = useState(
    resolveIsFollowed(normalizedUser)
  );

  useEffect(() => {
    setIsFollowed(resolveIsFollowed(normalizedUser));
  }, [
    normalizedUser,
    normalizedUser.followedByMe,
    normalizedUser.isFollowedByMe,
    normalizedUser.followedByCurrentUser,
    normalizedUser.isFollowedByCurrentUser,
    normalizedUser.id,
  ]);

  function handleCardClick() {
    navigate(`/users/${normalizedUser.id}`);
    onClickUser?.();
  }

  function updateFollowState(newState) {
    if (user) {
      Object.assign(user, applyFollowState(user, newState));
    }
    setIsFollowed(newState);
    onChange?.(newState);
  }

  return (
    <div
      className={`follow-card clickable ${compact ? "compact" : ""}`}
      onClick={handleCardClick}
    >
      <div className="image-container">
        {normalizedUser.profilePhotoUrl ? (
          <img className="user-image" src={normalizedUser.profilePhotoUrl} alt="Profile" />
        ) : (
          <div className="user-image user-avatar">
            {normalizedUser.username.charAt(0).toUpperCase()}
          </div>
        )}
      </div>

      <div className="follow-row">
        <span className="username-link">@{normalizedUser.username}</span>

        {!isSelf && showFollowButton && (
          <FollowButton
            userId={normalizedUser.id}
            isFollowed={isFollowed}
            onChange={updateFollowState}
            onError={onError}
            profileUpdater={profileUpdater}
          />
        )}
      </div>

      <p className="user-fullname">
        {normalizedUser.firstName} {normalizedUser.lastName}
      </p>
    </div>
  );
}
