import { usePostCreate } from "../../hooks/post/usePostCreate";
import { useNavigate } from "react-router-dom";

import Spinner from "../../components/common/Spinner";
import PostForm from "../../components/posts/PostForm";

export default function CreatePostPage() {
  const { save, saving, error } = usePostCreate();
  const navigate = useNavigate();

  async function handleSubmit(values) {
    const result = await save(values);
    if (result.success && result.id) {
      navigate(`/posts/${result.id}`);
    }
  }

  if (saving) return <Spinner />;

  return (
    <div className="page">
      <div>
        <h2>Create New Post</h2>

        {error && <div className="error-banner">{error}</div>}

        <PostForm
          initialValues={{ title: "", content: "", tags: "" }}
          submitLabel="Publish Post"
          onSubmit={handleSubmit}
        />
      </div>
    </div>
  );
}
