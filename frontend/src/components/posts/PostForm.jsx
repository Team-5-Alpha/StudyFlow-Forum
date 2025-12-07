import { useState, useEffect } from "react";
import Input from "../common/Input";
import FieldLengthHint from "../common/FieldLengthHint";

import "../../styles/components/posts/PostForm.css";
import "../../styles/components/common/FormControls.css";

export default function PostForm({
  initialValues = { title: "", content: "" },
  submitLabel = "Submit",
  showTags = false,
  onSubmit,
}) {
  const [form, setForm] = useState(initialValues);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setForm(initialValues);
  }, [initialValues]);

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function handleSubmit(e) {
    e.preventDefault();
    const nextErrors = {};
    if (!form.title.trim()) nextErrors.title = "Title is required.";
    if (!form.content.trim()) nextErrors.content = "Content is required.";

    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    const tagsArray = (form.tags || "")
      .split(",")
      .map((t) => t.trim())
      .filter(Boolean);

    onSubmit({ ...form, tags: tagsArray });
  }

  return (
    <form onSubmit={handleSubmit} className="form-card">
      <h3 className="form-title">Create a post</h3>

      <div className="form-block">
        <Input
          label="Title"
          name="title"
          value={form.title}
          onChange={change}
          error={errors.title}
        />
        <FieldLengthHint value={form.title} min={16} max={64} />
      </div>

      <div className="form-block">
        <div className="form-field">
          <label className="input-label">Content</label>
          <textarea
            className={`form-textarea ${errors.content ? "input-error" : ""}`}
            name="content"
            rows={6}
            value={form.content}
            onChange={change}
            placeholder="Share your thoughts..."
          />
          {errors.content && <span className="input-error-message">{errors.content}</span>}
          <FieldLengthHint value={form.content} min={32} max={8192} />
        </div>
      </div>

      {showTags && (
        <div className="form-block">
          <Input
            label="Tags (comma-separated)"
            name="tags"
            value={form.tags}
            onChange={change}
            error={errors.tags}
          />
        </div>
      )}

      <div className="post-form-actions">
        <button type="submit" className="accent-btn">
          {submitLabel}
        </button>
      </div>
    </form>
  );
}
