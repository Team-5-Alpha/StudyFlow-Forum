import { useState, useEffect, useCallback } from "react";
import api from "../../api/axios";
import { normalizeUserSummary } from "../../utils/followUtils";
import { normalizePost } from "../../utils/postUtils";

export function useUserProfile(userId) {
  const [profile, setProfile] = useState(null);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);

    const [p, ps] = await Promise.all([
      api.get(`/api/private/users/${userId}`),
      api.get(`/api/private/users/${userId}/posts`)
    ]);

    setProfile(normalizeUserSummary(p.data));
    setPosts(
      (ps.data || []).map(post => normalizePost(post))
    );
    setLoading(false);
  }, [userId]);

  useEffect(() => {
    if (userId) load();
  }, [userId, load]);

  return {
    profile,
    posts,
    loading,
    reload: load,
    setProfile,
    setPosts,
  };
}
