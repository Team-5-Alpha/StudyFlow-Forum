import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import { useUserProfile } from "../../hooks/user/useUserProfile";
import { useUserAdminActions } from "../../hooks/user/useUserAdminActions";
import { useFollowActions } from "../../hooks/user/useFollowActions";
import { applyFollowState, resolveIsFollowed } from "../../utils/followUtils";

import PostCard from "../../components/posts/PostCard";
import api from "../../api/axios";
import Spinner from "../../components/common/Spinner";
import AdminActionsDropdown from "../../components/admin/AdminActionsDropdown";
import UserConnectionsModal from "../../components/users/UserConnectionsModal";

import "../../styles/pages/users/UserProfilePage.css";
import "../../styles/components/common/FormControls.css";

export default function UserProfilePage() {
  const { userId } = useParams();
  const uid = Number(userId);
  const navigate = useNavigate();

  const { user: actingUser } = useAuth();

  const {
    profile,
    setProfile,
    posts,
    loading,
    setPosts,
  } = useUserProfile(uid);

  const [connectionsOpen, setConnectionsOpen] = useState(false);
  const [connectionsMode, setConnectionsMode] = useState("followers");

  function syncFollowState(newState) {
    if (typeof newState !== "boolean") return;

    setPosts(prev =>
      prev.map(p => ({
        ...p,
        author: applyFollowState(p.author, newState),
      }))
    );
  }

  const { follow, unfollow } = useFollowActions(uid, {
    setProfile,
    onChange: syncFollowState,
  });
  const admin = useUserAdminActions(uid, setProfile);

  useEffect(() => {
    if (!profile || !posts.length) return;

    const followState = resolveIsFollowed(profile);
    setPosts(prev =>
      prev.map(p =>
        p.author?.id === profile.id
          ? { ...p, author: applyFollowState(p.author, followState) }
          : p
      )
    );
  }, [
    profile,
    profile?.followedByCurrentUser,
    profile?.isFollowedByCurrentUser,
    profile?.followedByMe,
    profile?.isFollowedByMe,
    profile?.id,
    posts.length,
    setPosts,
  ]);

  if (loading) return <Spinner />;
  if (!profile) return <p>User not found.</p>;

  const isOwner = actingUser?.id === uid;
  const actingIsAdmin = actingUser?.role === "ADMIN";
  const isFollowed = resolveIsFollowed(profile);

  const isProfileAdmin = profile.role === "ADMIN";
  const isProfileBlocked = profile.blocked;

  function openFollowers() {
    setConnectionsMode("followers");
    setConnectionsOpen(true);
  }

  function openFollowing() {
    setConnectionsMode("following");
    setConnectionsOpen(true);
  }

  return (
    <div className="profile-container">

        <h2 className="profile-status">
        {isProfileAdmin ? "Admin:" : "User:"}
        <span className={isProfileBlocked ? "status-blocked" : "status-active"}>
          {isProfileBlocked ? "Blocked" : "Active"}
        </span>
      </h2>
      
      <div className="profile-header-container">
        
        {profile.profilePhotoUrl ? (
          <img className="profile-image" src={profile.profilePhotoUrl} alt="Profile" />
        ) : (
          <div className="profile-image fallback-avatar">
            {profile.username.charAt(0).toUpperCase()}
          </div>
        )}

        <div className="profile-details">
          <h2 className="username">@{profile.username}</h2>
          <p className="other-info">{profile.firstName} {profile.lastName}</p>
          <p className="other-info">{profile.email}</p>

          <div className="follow-info-container">
            <button className="secondary-btn" onClick={openFollowers}>
              Followers: {profile.followersCount}
            </button>

            <button className="secondary-btn" onClick={openFollowing}>
              Following: {profile.followingCount}
            </button>
          </div>

          {!isOwner &&
            (!isFollowed ? (
              <button className="accent-btn" onClick={follow}>
                Follow
              </button>
            ) : (
              <button className="accent-btn" onClick={unfollow}>
                Unfollow
              </button>
            ))}

          {isOwner && (
            <button
              className="accent-btn"
              onClick={() => navigate(`/users/${uid}/edit`)}
            >
              Edit Profile
            </button>
          )}

          {actingIsAdmin && !isOwner && (
            <AdminActionsDropdown
              profile={profile}
              onBlock={admin.block}
              onUnblock={admin.unblock}
              onPromote={admin.promote}
            />
          )}
        </div>
      </div>

      <h1>Posts by {profile.username}</h1>

      {isOwner && (
        <button
          className="accent-btn"
          style={{ marginBottom: "20px" }}
          onClick={() => navigate("/posts/create")}
        >
          Create New Post
        </button>
      )}

      <div className="posts-container">
      {posts.length === 0 ? (
        <p className="other-info">This user has not created any posts yet.</p>
      ) : (
        posts.map(post => (
          <PostCard
            key={post.id}
            post={post}
            profileUpdater={setProfile}
            onChange={syncFollowState}
            showActions={actingIsAdmin || actingUser?.id === post.author?.id}
            onEdit={actingUser?.id === post.author?.id ? () => navigate(`/posts/${post.id}`) : undefined}
            onDelete={async () => {
              try {
                await api.delete(`/api/private/posts/${post.id}`);
                setPosts(prev => prev.filter(p => p.id !== post.id));
              } catch (err) {
                console.error("DELETE POST ERROR:", err);
              }
            }}
          />
        ))
      )}

      <UserConnectionsModal
        userId={uid}
        mode={connectionsMode}
        isOpen={connectionsOpen}
        onClose={() => setConnectionsOpen(false)}
      />
      </div>
    </div>
  );
}
