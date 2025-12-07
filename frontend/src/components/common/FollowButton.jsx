import { useFollowActions } from "../../hooks/user/useFollowActions";
import "../../styles/components/common/FormControls.css";

export default function FollowButton({
  userId,
  isFollowed,
  onChange,
  onError,
  profileUpdater,
  className = "",
  stopPropagation = true
}) {
  const { follow, unfollow } = useFollowActions(userId, {
    setProfile: profileUpdater,
    onChange: (newState) => {
      onChange?.(newState);
    },
    onError: (err) => {
      if (onError) {
        onError(err);
      } else {
        const message = err?.message || "Follow action failed.";
        window.alert(message);
      }
    },
  });

  function handleFollow(e) {
    if (stopPropagation) e.stopPropagation();
    follow();
  }

  function handleUnfollow(e) {
    if (stopPropagation) e.stopPropagation();
    unfollow();
  }

  return (
    <>
      {isFollowed ? (
        <button className={`accent-btn ${className}`} onClick={handleUnfollow}>
          Unfollow
        </button>
      ) : (
        <button className={`accent-btn ${className}`} onClick={handleFollow}>
          Follow
        </button>
      )}
    </>
  );
}
