import { useState, useEffect, useCallback } from "react";
import api from "../../api/axios";
import { normalizePost, applyLikeState } from "../../utils/postUtils";

export function usePost(postId) {
  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    const res = await api.get(`/api/private/posts/${postId}`);
    setPost(prev => normalizePost(res.data, prev));
    setLoading(false);
  }, [postId]);

  async function toggleLike() {
    if (!post) return;

    const nextLiked = !post.likedByCurrentUser;
    const optimistic = applyLikeState(post, nextLiked);
    setPost(optimistic);

    try {
      if (nextLiked) {
        await api.post(`/api/private/posts/${postId}/likes`);
      } else {
        await api.delete(`/api/private/posts/${postId}/likes`);
      }
    } catch (err) {
      setPost(post);
      console.error("LIKE ERROR:", err);
    }
  }

  async function remove() {
    await api.delete(`/api/private/posts/${postId}`);
  }

  useEffect(() => {
    load();
  }, [load]);

  return { post, loading, toggleLike, remove, reload: load };
}
