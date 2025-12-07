import { useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import api from "../../api/axios";
import UserSummary from "../users/UserSummary";
import { normalizeUserSummary } from "../../utils/followUtils";
import { applyLikeState, normalizePost } from "../../utils/postUtils";
import FieldLengthHint from "../common/FieldLengthHint";

import "../../styles/components/posts/PostCard.css";
import "../../styles/components/common/FormControls.css";

export default function PostCard({
  post,
  onChange,
  onLikeChange,
  profileUpdater,
  showFollowButton = true,
  showLikeButton = true,
  refreshOnMount = true,
  editing = false,
  editValues,
  onEditChange,
  showActions = false,
  onEdit,
  onDelete,
}) {
  const navigate = useNavigate();

  const [normalizedPostState, setNormalizedPostState] = useState(() => normalizePost(post));

  useEffect(() => {
    setNormalizedPostState(normalizePost(post));
  }, [post]);

  const [isLiked, setIsLiked] = useState(normalizedPostState.likedByCurrentUser);
  const [likes, setLikes] = useState(normalizedPostState.likesCount);
  const author = useMemo(
    () => normalizeUserSummary(normalizedPostState.author),
    [normalizedPostState.author]
  );

  useEffect(() => {
    setIsLiked(Boolean(normalizedPostState.likedByCurrentUser));
    setLikes(normalizedPostState.likesCount ?? 0);
  }, [normalizedPostState.likedByCurrentUser, normalizedPostState.likesCount, normalizedPostState.id]);

  useEffect(() => {
    if (!refreshOnMount) return undefined;

    let mounted = true;

    async function refreshStatus() {
      try {
        const res = await api.get(`/api/private/posts/${normalizedPostState.id}`);
        if (!mounted) return;
        const fresh = normalizePost(res.data);
        setNormalizedPostState(fresh);
        setIsLiked(Boolean(fresh.likedByCurrentUser));
        setLikes(fresh.likesCount ?? 0);
      } catch (err) {
        console.error("POST STATUS LOAD ERROR:", err);
      }
    }

    if (normalizedPostState?.id) {
      refreshStatus();
    }

    return () => {
      mounted = false;
    };
  }, [normalizedPostState.id, refreshOnMount]);

  async function handleLike(nextLiked, e) {
    e.stopPropagation();

    try {
      const nextLikes = nextLiked
        ? likes + 1
        : Math.max(likes - 1, 0);

      setIsLiked(nextLiked);
      setLikes(nextLikes);

      if (nextLiked) {
        await api.post(`/api/private/posts/${normalizedPostState.id}/likes`);
      } else {
        await api.delete(`/api/private/posts/${normalizedPostState.id}/likes`);
      }

      onLikeChange?.(nextLiked, nextLikes);
    } catch (err) {
      const msg = String(err?.message || "").toLowerCase();

      if (msg.includes("already liked")) {
        const currentLikes = normalizedPostState.likesCount ?? likes ?? 0;
        const applied = applyLikeState(normalizedPostState, true, currentLikes);
        setIsLiked(applied.likedByCurrentUser);
        setLikes(applied.likesCount);
        onLikeChange?.(true, currentLikes);
        return;
      }

      if (msg.includes("not like") || msg.includes("not liked") || msg.includes("already unliked")) {
        const currentLikes = normalizedPostState.likesCount ?? likes ?? 0;
        const applied = applyLikeState(normalizedPostState, false, currentLikes);
        setIsLiked(applied.likedByCurrentUser);
        setLikes(applied.likesCount);
        onLikeChange?.(false, currentLikes);
        return;
      }

      setIsLiked(isLiked);
      setLikes(likes);
      console.error("LIKE ERROR:", err);
    }
  }

  function goToPost() {
    if (editing) return;
    navigate(`/posts/${normalizedPostState.id}`);
  }

  return (
    <div className="post-card">
      
      <div className="post-header">
        <div
          className="post-author-container"
          onClick={(e) => e.stopPropagation()}
        >
          <UserSummary
            user={author}
            compact={true}
            showFollowButton={showFollowButton}
            onChange={onChange}
            profileUpdater={profileUpdater}
            onClickUser={() => navigate(`/users/${author.id}`)}
          />
        </div>
        <span className="post-date">
          {new Date(normalizedPostState.createdAt).toLocaleDateString()}
        </span>
      </div>

      <div className="post-container" onClick={goToPost}>
        {editing ? (
          <>
            <input
              className={`form-input ${!editValues?.title ? "input-error" : ""}`}
              name="title"
              value={editValues?.title ?? ""}
              onChange={onEditChange}
              placeholder="Title"
            />
            <FieldLengthHint value={editValues?.title || ""} min={16} max={64} />
            <textarea
              className={`form-textarea ${!editValues?.content ? "input-error" : ""}`}
              name="content"
              rows={6}
              value={editValues?.content ?? ""}
              onChange={onEditChange}
              placeholder="Content"
            />
            <FieldLengthHint value={editValues?.content || ""} min={32} max={8192} />
          </>
        ) : (
          <>
            <h2 className="post-title">{normalizedPostState.title}</h2>
            <p className="post-content">{normalizedPostState.content}</p>
          </>
        )}
      </div>
      {(normalizedPostState.tags?.length > 0 || showLikeButton || editing || showActions) && (
        <div className="post-footer">
          <div className="post-footer-left">
            {showLikeButton && !editing && (
            isLiked ? (
              <button
                className="unlike-btn"
                onClick={(e) => handleLike(false, e)}
              >
                Unlike {likes}
              </button>
            ) : (
              <button
                className="like-btn"
                onClick={(e) => handleLike(true, e)}
              >
                Like {likes}
              </button>
            )
          )}
          {editing ? (
            <div className="post-tags">
              <input
                className="form-input"
                name="tags"
                value={editValues?.tags ?? ""}
                onChange={onEditChange}
                placeholder="Tags (comma-separated)"
              />
            </div>
          ) : (
            normalizedPostState.tags?.length > 0 && (
              <div className="post-tags">
                {Array.from(normalizedPostState.tags).map((t) => (
                  <div key={t} className="tag">{t}</div>
                ))}
              </div>
            )
          )}
        </div>
          
          
          {showActions && !editing && (
            <div className="post-actions">
              {onEdit && (
                <button className="secondary-btn" onClick={(e) => { e.stopPropagation(); onEdit(); }}>
                  Edit
                </button>
              )}
              {onDelete && (
                <button className="red-btn" onClick={(e) => { e.stopPropagation(); onDelete(); }}>
                  Delete
                </button>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
