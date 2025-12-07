import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import { useHomeAnonymous } from "../../hooks/post/useHomeAnonymous";
import { usePostFeed } from "../../hooks/post/usePostFeed";
import { usePostCreate } from "../../hooks/post/usePostCreate";

import Spinner from "../../components/common/Spinner";
import PostForm from "../../components/posts/PostForm";
import PostCard from "../../components/posts/PostCard";
import api from "../../api/axios";

import "../../styles/pages/home/HomePage.css";
import logo from "../../assets/images/logo-l.svg"

export default function HomePage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const anon = useHomeAnonymous();
  const feedData = usePostFeed();
  const createPost = usePostCreate();
  const [showForm, setShowForm] = useState(false);

  const loading = user ? feedData.loading : anon.loading;
  if (loading) return <Spinner />;

  if (!user) {
    const { topCommented, recentPosts } = anon;

    return (
      <div className="home-container">
        <h1 className="welcome">Welcome to</h1>
        <img className="logo-l" src={logo} alt="Logo" />
        <p>Browse discussions, explore ideas and join the conversation.</p>
        <div className="auth-container">
            <button className="primary-btn" onClick={() => navigate("/login")}>Login</button>
            <button className="primary-btn" onClick={() => navigate("/register")}>Register</button>
        </div>
        

        <div className="posts-container">
          <div className="home-posts-list">
            <h1 className="welcome">Top 10 Commented</h1>
            {topCommented.map((p) => (
              <PostCard
                key={p.id}
                post={p}
                showFollowButton={false}
                showLikeButton={false}
                refreshOnMount={false}
              />
            ))}
          </div>

          <div className="home-posts-list">
            <h1 className="welcome">Latest 10 Posts</h1>
            {recentPosts.map((p) => (
              <PostCard
                key={p.id}
                post={p}
                showFollowButton={false}
                showLikeButton={false}
                refreshOnMount={false}
              />
            ))}
          </div>
        </div>
      </div>
    );
  }

  const {
    feed,
    reload,
    syncAuthorFollow,
    syncPostLike,
    loadMore,
    hasMore,
    loadingMore,
  } = feedData;
  const { save, saving, error } = createPost;

  async function handleCreate(values) {
    const result = await save(values);
    if (result.success && result.id) {
      setShowForm(false);
      navigate(`/posts/${result.id}`);
    }
  }

  return (
    <div className="home-container">
      <div className="welcome">
        <h1 className="welcome">Welcome back,</h1>
        <h1 className="user">{user.username}</h1>
      </div>

      <img className="logo-l" src={logo} alt="Logo" />

      <button className="primary-btn" onClick={() => setShowForm(!showForm)}>
        {showForm ? "Cancel" : "Create Post"}
      </button>

      <div className="create-post-container">
        {showForm && (
          <div className="create-post-container">
            <h3>Create New Post</h3>

            {error && <div className="error-banner">{error}</div>}

            <PostForm
              initialValues={{ title: "", content: "", tags: "" }}
              submitLabel={saving ? "Publishing..." : "Publish Post"}
              onSubmit={handleCreate}
            />
          </div>
        )}
      </div>
      

      <h2>Your Feed</h2>

      <div className="home-posts-list">
        {feed.length === 0 ? (
        <p>No posts yet… maybe you should create one?</p>
      ) : (
        feed.map((post) => {
          const canDelete = user?.role === "ADMIN" || user?.id === post.author?.id;
          const canEdit = user?.id === post.author?.id;
          const handleChange = (newState) => {
            if (typeof newState === "boolean") {
              syncAuthorFollow(post.author?.id, newState);
            }
          };

          const handleLikeChange = (liked, likesCount) => {
            syncPostLike(post.id, liked, likesCount);
          };

          return (
            <PostCard
              key={post.id}
              post={post}
              onChange={handleChange}
              onLikeChange={handleLikeChange}
              showActions={canDelete || canEdit}
              onEdit={canEdit ? () => navigate(`/posts/${post.id}`) : undefined}
              onDelete={async () => {
                try {
                  await api.delete(`/api/private/posts/${post.id}`);
                  await reload();
                } catch (err) {
                  console.error("DELETE POST ERROR:", err);
                }
              }}
            />
          );
        })
      )}
      </div>
      {hasMore && (
        <div className="feed-pagination">
          <button
            className="secondary-btn"
            onClick={loadMore}
            disabled={loadingMore}
          >
            {loadingMore ? "Loading..." : "Load more"}
          </button>
        </div>
      )}
    </div>
  );
}
