import { useState } from "react";
import api from "../../api/axios";

export function usePostCreate() {
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  async function save(values) {
    setSaving(true);
    setError("");

    try {
      const res = await api.post("/api/private/posts", {
        title: values.title,
        content: values.content,
      });

      return { success: true, id: res.data.id };
    } catch (err) {
      setError(err.message || "Failed to create post.");
      return { success: false };
    } finally {
      setSaving(false);
    }
  }

  return { save, saving, error };
}