import { useState, useEffect } from "react";
import "../../styles/components/common/FormControls.css";
import FieldLengthHint from "../common/FieldLengthHint";
import "../../styles/components/posts/CommentCard.css";

export default function CommentForm({
  initialValue = "",
  submitLabel = "Submit",
  rows = 3,
  onSubmit,
  onCancel,
}) {
  const [text, setText] = useState(initialValue);

  useEffect(() => {
    setText(initialValue);
  }, [initialValue]);

  async function handleSubmit(e) {
    e.preventDefault();
    const value = text.trim();
    if (!value) return;
    await onSubmit?.(value);
    setText("");
  }

  return (
    <form className="comment-edit-form form-card" onSubmit={handleSubmit}>
      <textarea
        className="form-textarea"
        rows={rows}
        value={text}
        onChange={(e) => setText(e.target.value)}
      />
      <FieldLengthHint value={text} min={4} max={4096} />

      <div className="comment-edit-buttons">
        {onCancel && (
          <button type="button" className="secondary-btn" onClick={onCancel}>
            Cancel
          </button>
        )}
        <button type="submit" className="accent-btn">
          {submitLabel}
        </button>
      </div>
    </form>
  );
}
