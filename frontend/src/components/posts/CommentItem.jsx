import CommentForm from "./CommentForm";
import CommentReplyForm from "./CommentReplyForm";
import { useAuth } from "../../context/AuthContext";

import "../../styles/components/posts/CommentCard.css";
import "../../styles/components/common/FormControls.css";

export default function CommentItem({
  comment,
  level,
  replyId,
  setReplyId,
  editingId,
  setEditingId,
  actions
}) {
  const { user } = useAuth();
  const isOwner = user?.id === comment.author.id;
  const isAdmin = user?.role === "ADMIN";
  const canDelete = isOwner || isAdmin;

  const indent = Math.min(level, 2) * 20;
  const hasLiked = comment.likedByCurrentUser;

  const createdAt = comment.createdAt
    ? new Date(comment.createdAt).toLocaleString([], {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      })
    : "";

  function renderAvatar() {
    if (comment.author?.profilePhotoUrl) {
      return (
        <img
          className="comment-avatar-img"
          src={comment.author.profilePhotoUrl}
          alt="Profile"
        />
      );
    }

    const initial = comment.author?.username
      ? comment.author.username.charAt(0).toUpperCase()
      : "?";

    return <div className="comment-avatar-fallback">{initial}</div>;
  }

  return (
    <>
      <div style={{ marginLeft: indent }}>
        <div className="comment-card">
          <div className="comment-header">
            <div className="comment-author">
              <div className="comment-avatar">{renderAvatar()}</div>
              <div className="comment-author-info">
                <span className="comment-username">@{comment.author.username}</span>
                <span className="comment-date">{createdAt}</span>
              </div>
            </div>
          </div>

          <div className="comment-body">
            {editingId === comment.id ? (
            <CommentForm
              initialValue={comment.content}
              submitLabel="Save"
              rows={3}
              onCancel={() => setEditingId(null)}
              onSubmit={async (text) => {
                await actions.update(comment.id, text);
                setEditingId(null);
              }}
            />
          ) : (
            <p className="comment-content">{comment.content}</p>
          )}
          </div>

          <div className="comment-actions">
            <div className="comment-actions-left">
              <button
                className={hasLiked ? "unlike-btn" : "like-btn"}
                onClick={() =>
                  hasLiked
                    ? actions.unlike(comment.id)
                    : actions.like(comment.id)
                }
              >
                {hasLiked ? "Unlike" : "Like"} {comment.likesCount}
              </button>

              <button
                className="secondary-btn"
                onClick={() =>
                  setReplyId(replyId === comment.id ? null : comment.id)
                }
              >
                Reply
              </button>
            </div>

            <div className="comment-actions-right">
              {isOwner && (
                <button className="secondary-btn" onClick={() => setEditingId(comment.id)}>
                  Edit
                </button>
              )}

              {canDelete && (
                <button className="red-btn" onClick={() => actions.remove(comment.id)}>
                  Delete
                </button>
              )}
            </div>
          </div>
        </div>

        {replyId === comment.id && (
          <div className="comment-reply-form" style={{ marginLeft: Math.min(level + 1, 2) * 20 }}>
            <CommentReplyForm
              parentId={comment.id}
              onCancel={() => setReplyId(null)}
              onReply={async (text, parentId) => {
                await actions.reply(text, parentId);
                setReplyId(null);
              }}
            />
          </div>
        )}
      </div>

      {comment.children?.length > 0 &&
        comment.children.map((child) => (
          <CommentItem
            key={child.id}
            comment={child}
            level={level + 1}
            replyId={replyId}
            setReplyId={setReplyId}
            editingId={editingId}
            setEditingId={setEditingId}
            actions={actions}
          />
        ))}
    </>
  );
}
