import { useState, useEffect, useCallback } from "react";
import api from "../../api/axios";

export function usePostEdit(postId) {
  const [initialValues, setInitialValues] = useState({
    title: "",
    content: "",
    tags: "",
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    try {
      setLoading(true);
      const res = await api.get(`/api/private/posts/${postId}`);
      const p = res.data;

      setInitialValues({
        title: p.title,
        content: p.content,
        tags: p.tags?.join(", ") || "",
      });
    } catch (err) {
      setError(err.message || "Failed to load post.");
    } finally {
      setLoading(false);
    }
  }, [postId]);

  async function submit(values) {
    setSaving(true);
    setError("");

    try {
      const tags = values.tags
        .split(",")
        .map((t) => t.trim())
        .filter(Boolean);

      await api.put(`/api/private/posts/${postId}`, {
        title: values.title,
        content: values.content,
        tags,
      });

      return true;
    } catch (err) {
      setError(err.message || "Failed to update post.");
      return false;
    } finally {
      setSaving(false);
    }
  }

  useEffect(() => {
    load();
  }, [load]);

  return { initialValues, loading, saving, error, submit, reload: load };
}
