import { useState } from "react";
import "../../styles/components/common/FormControls.css";
import FieldLengthHint from "../common/FieldLengthHint";
import "../../styles/components/posts/CommentCard.css";

export default function CommentReplyForm({ parentId, onCancel, onReply }) {
  const [text, setText] = useState("");

  function handleSubmit(e) {
    e.preventDefault();
    const value = text.trim();
    if (!value) return;
    onReply(value, parentId);
    setText("");
  }

  return (
    <form className="comment-edit-form form-card" onSubmit={handleSubmit}>
      <textarea
        className="form-textarea"
        rows={3}
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Write a reply..."
      />
      <FieldLengthHint value={text} min={4} max={4096} />

      <div className="comment-edit-buttons">
        {onCancel && (
          <button type="button" className="secondary-btn" onClick={onCancel}>
            Cancel
          </button>
        )}
        <button type="submit" className="accent-btn">
          Send Reply
        </button>
      </div>
    </form>
  );
}
