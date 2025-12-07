import { useEffect, useState } from "react";
import api from "../../api/axios";
import { normalizePost } from "../../utils/postUtils";

export function useHomeAnonymous() {
  const [topCommented, setTopCommented] = useState([]);
  const [recentPosts, setRecentPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    try {
      const [topRes, recentRes] = await Promise.all([
        api.get("/api/public/posts/top-commented"),
        api.get("/api/public/posts/latest")
      ]);

      setTopCommented((topRes.data || []).map(post => normalizePost(post)));
      setRecentPosts((recentRes.data || []).map(post => normalizePost(post)));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  return { topCommented, recentPosts, loading };
}
