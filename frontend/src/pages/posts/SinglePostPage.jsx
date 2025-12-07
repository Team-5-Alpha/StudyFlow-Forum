import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useEffect, useState } from "react";
import Spinner from "../../components/common/Spinner";
import PostCard from "../../components/posts/PostCard";
import CommentThread from "../../components/posts/CommentThread";
import api from "../../api/axios";

import { usePost } from "../../hooks/post/usePost";
import { useComments } from "../../hooks/comment/useComments";

import "../../styles/pages/posts/SinglePostPage.css"
import "../../styles/components/posts/PostForm.css";
import "../../styles/components/common/FormControls.css";

export default function SinglePostPage() {
  const { postId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState("");
  const [form, setForm] = useState({
    title: "",
    content: "",
    tags: "",
  });

  const { post, loading, remove, reload } = usePost(postId);
  const {
    loading: loadingComments,
    comments,
    actions,
  } = useComments(postId);

  useEffect(() => {
    if (!post) return;
    setForm({
      title: post.title || "",
      content: post.content || "",
      tags: Array.isArray(post.tags) ? post.tags.join(", ") : "",
    });
  }, [post]);

  if (loading) return <Spinner />;
  if (!post) return <p>Post not found.</p>;

  async function handleDelete() {
    await remove();

    if (window.history.length > 1) navigate(-1);
    else navigate("/");
  }

  const isOwner = user?.id === post.author.id;
  const isAdmin = user?.role === "ADMIN";

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleUpdate(e) {
    e.preventDefault();
    try {
      setSaving(true);
      setSaveError("");
      if (!form.title.trim() || !form.content.trim()) {
        setSaveError("Title and content are required.");
        return;
      }
      const tagsArray = (form.tags || "")
        .split(",")
        .map((t) => t.trim())
        .filter(Boolean);

      await api.put(`/api/private/posts/${post.id}`, {
        title: form.title,
        content: form.content,
        tags: tagsArray,
      });
      await reload();
      setEditing(false);
    } catch (err) {
      setSaveError(err?.message || "Failed to update post.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="single-post-conatiner">
      {editing ? (
        <>
          <PostCard
            post={post}
            onChange={reload}
            onLikeChange={() => reload()}
            editing
            editValues={form}
            onEditChange={change}
            showLikeButton={false}
          />
          {saveError && <div className="error-banner">{saveError}</div>}
          <div className="post-form-actions" style={{ marginTop: "8px" }}>
            <button
              type="button"
              className="secondary-btn"
              onClick={() => setEditing(false)}
            >
              Cancel
            </button>
            <button type="button" className="accent-btn" onClick={handleUpdate}>
              {saving ? "Saving..." : "Save Changes"}
            </button>
          </div>
        </>
      ) : (
        <PostCard
          post={post}
          onChange={reload}
          onLikeChange={() => reload()}
          showActions={isOwner || isAdmin}
          onEdit={isOwner ? () => setEditing(true) : undefined}
          onDelete={handleDelete}
        />
      )}

      <div className="comments-container">
        <CommentThread
        loading={loadingComments}
        comments={comments}
        actions={actions}
      />
      </div>
    </div>
  );
}
