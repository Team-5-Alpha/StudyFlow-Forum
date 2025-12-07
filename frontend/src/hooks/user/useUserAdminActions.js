import api from "../../api/axios";

export function useUserAdminActions(userId, setProfile) {

  async function block() {
    await api.put(`/api/admin/users/${userId}/block`);
    setProfile(prev => ({
      ...prev,
      blocked: true,
    }));
  }

  async function unblock() {
    await api.put(`/api/admin/users/${userId}/unblock`);
    setProfile(prev => ({
      ...prev,
      blocked: false,
    }));
  }

  async function promote() {
    await api.put(`/api/admin/users/${userId}/promote`);
    setProfile(prev => ({
      ...prev,
      role: "ADMIN",
    }));
  }

  return { block, unblock, promote };
}
