import { useState, useEffect, useCallback } from "react";
import api from "../../api/axios";

export function useComments(postId) {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);

    const res = await api.get("/api/private/comments", {
      params: { postId, page: 0, size: 200 }
    });

    const list = res.data || [];
    const map = {};

    list.forEach(c => {
      c.children = [];
      map[c.id] = c;
    });

    const roots = [];

    list.forEach(c => {
      if (c.parentCommentId && map[c.parentCommentId]) {
        map[c.parentCommentId].children.push(c);
      } else {
        roots.push(c);
      }
    });

    setComments(roots);
    setLoading(false);
  }, [postId]);

  async function create(content) {
    await api.post(`/api/private/posts/${postId}/comments`, { content });
    load();
  }

  async function reply(content, parentId) {
    await api.post(`/api/private/posts/${postId}/comments`, {
      content,
      parentCommentId: parentId
    });
    load();
  }

  async function toggleLike(comment) {
    if (comment.likedByCurrentUser) {
      await api.delete(`/api/private/comments/${comment.id}/likes`);
    } else {
      await api.post(`/api/private/comments/${comment.id}/likes`);
    }
    load();
  }

  async function like(id) {
    await api.post(`/api/private/comments/${id}/likes`);
    load();
  }

  async function unlike(id) {
    await api.delete(`/api/private/comments/${id}/likes`);
    load();
  }

  async function update(id, content) {
    await api.put(`/api/private/comments/${id}`, { content });
    load();
  }

  async function remove(id) {
    await api.delete(`/api/private/comments/${id}`);
    load();
  }

  useEffect(() => { load(); }, [load]);

  return {
    comments,
    loading,
    actions: { create, reply, update, remove, toggleLike, like, unlike }
  };
}
