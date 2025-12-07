import { useState, useEffect, useCallback } from "react";
import api from "../../api/axios";
import { cleanPayload } from "../../utils/cleanPayload";

export function useUserEdit(userId, isAdmin) {
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    password: "",
    profilePhotoURL: ""
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    try {
      setLoading(true);
      const res = await api.get(`/api/private/users/${userId}`);

      setForm({
        firstName: res.data.firstName || "",
        lastName: res.data.lastName || "",
        email: res.data.email || "",
        phoneNumber: res.data.phoneNumber || "",
        password: "",
        profilePhotoURL: res.data.profilePhotoUrl || ""
      });
    } catch {
      setError("Failed to load user.");
    } finally {
      setLoading(false);
    }
  }, [userId]);

  async function save() {
    setSaving(true);
    setError("");

    const endpoint = isAdmin
      ? `/api/admin/users/${userId}`
      : `/api/private/users/${userId}`;

    try {
      await api.put(endpoint, cleanPayload(form));
      return true;
    } catch {
      setError("Failed to update user.");
      return false;
    } finally {
      setSaving(false);
    }
  }

  useEffect(() => { load(); }, [load]);

  return { form, setForm, loading, saving, error, save, reload: load };
}
