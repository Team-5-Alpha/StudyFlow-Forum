import { usePostEdit } from "../../hooks/post/usePostEdit";
import { useParams, useNavigate } from "react-router-dom";

import Spinner from "../../components/common/Spinner";
import PostForm from "../../components/posts/PostForm";

export default function EditPostPage() {
  const { postId } = useParams();
  const navigate = useNavigate();
  const { initialValues, loading, saving, error, submit } = usePostEdit(postId);

  if (loading) return <Spinner />;

  async function handleSubmit(values) {
    const ok = await submit(values);
    if (ok) navigate(`/posts/${postId}`);
  }

  return (
    <div className="page">
      <div>
        <h2>Edit Post</h2>

        {error && <div className="error-banner">{error}</div>}

        <PostForm
          initialValues={initialValues}
          submitLabel={saving ? "Saving..." : "Save Changes"}
          showTags={true}
          onSubmit={handleSubmit}
        />
      </div>
    </div>
  );
}
