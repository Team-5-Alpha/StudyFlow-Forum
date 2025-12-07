import { useEffect, useState, useCallback } from "react";
import api from "../../api/axios";
import { applyFollowState } from "../../utils/followUtils";
import { applyLikeState, normalizePost } from "../../utils/postUtils";
import { useAuth } from "../../context/AuthContext";

export function usePostFeed() {
  const { user } = useAuth();
  const [feed, setFeed] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const PAGE_SIZE = 10;

  function syncAuthorFollow(authorId, isFollowed) {
    if (!authorId && authorId !== 0) return;
    setFeed(prev =>
      prev.map(post =>
        post.author?.id === authorId
          ? { ...post, author: applyFollowState(post.author, isFollowed) }
          : post
      )
    );
  }

  function syncPostLike(postId, liked, likesCount) {
    setFeed(prev =>
      prev.map(post =>
        post.id === postId
          ? applyLikeState(post, liked, likesCount)
          : post
      )
    );
  }

  const mergePosts = useCallback((posts, append = false) => {
    setFeed(prev => {
      const base = append ? prev : [];
      const map = new Map(base.map(p => [p.id, p]));

      const next = append ? [...base] : [];

      posts.forEach(post => {
        const existing = map.get(post.id);
        const normalized = normalizePost(post, existing);

        if (existing) {
          const idx = next.findIndex(p => p.id === post.id);
          if (idx !== -1) {
            next[idx] = normalized;
          } else {
            next.push(normalized);
          }
        } else {
          next.push(normalized);
        }

        map.set(post.id, normalized);
      });

      return next;
    });
  }, []);

  const loadPrivatePage = useCallback(
    async (pageToLoad = 0, append = false) => {
      const firstPage = pageToLoad === 0 && !append;
      if (firstPage) {
        setLoading(true);
      } else {
        setLoadingMore(true);
      }

      try {
        const res = await api.get("/api/private/posts", {
          params: {
            sortBy: "createdAt",
            sortOrder: "desc",
            page: pageToLoad,
            size: PAGE_SIZE,
          },
        });
        const items = res.data || [];
        mergePosts(items, append);
        setHasMore(items.length === PAGE_SIZE);
        setPage(pageToLoad);
      } catch (err) {
        console.error("LOAD FEED ERROR:", err);
        setHasMore(false);
        try {
          const res = await api.get("/api/public/posts/latest");
          const items = res.data || [];
          mergePosts(items, false);
        } catch (innerErr) {
          console.error("LOAD FEED ERROR (public fallback failed):", innerErr);
        }
      } finally {
        if (firstPage) {
          setLoading(false);
        } else {
          setLoadingMore(false);
        }
      }
    },
    [PAGE_SIZE, mergePosts]
  );

  const loadPublic = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get("/api/public/posts/latest");
      const items = res.data || [];
      mergePosts(items, false);
      setHasMore(false);
      setPage(0);
    } catch (err) {
      console.error("LOAD FEED ERROR:", err);
    } finally {
      setLoading(false);
    }
  }, [mergePosts]);

  const load = useCallback(async () => {
    if (user) {
      await loadPrivatePage(0, false);
    } else {
      await loadPublic();
    }
  }, [user, loadPrivatePage, loadPublic]);

  const loadMore = useCallback(async () => {
    if (!user || loading || loadingMore || !hasMore) return;
    await loadPrivatePage(page + 1, true);
  }, [user, loading, loadingMore, hasMore, loadPrivatePage, page]);

  async function like(id) {
    try {
      await api.post(`/api/private/posts/${id}/likes`);
      load();
    } catch (err) {
      console.error("LIKE FEED ERROR:", err);
    }
  }

  async function unlike(id) {
    try {
      await api.delete(`/api/private/posts/${id}/likes`);
      load();
    } catch (err) {
      console.error("UNLIKE FEED ERROR:", err);
    }
  }

  useEffect(() => {
    load();
  }, [load]);

  return {
    feed,
    loading,
    loadingMore,
    hasMore,
    loadMore,
    like,
    unlike,
    reload: load,
    syncAuthorFollow,
    syncPostLike,
  };
}
