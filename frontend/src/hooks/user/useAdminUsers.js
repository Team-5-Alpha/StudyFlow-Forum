import { useEffect, useMemo, useState, useCallback } from "react";
import api from "../../api/axios";

const baseFilters = {
  searchTerm: "",
  searchField: "username",
  role: "ALL",
  status: "ALL",
};

export function useAdminUsers(presetFilters = {}) {
  const {
    searchTerm = "",
    searchField = "username",
    role = "ALL",
    status = "ALL",
  } = presetFilters;

  const mergedInitial = useMemo(
    () => ({
      ...baseFilters,
      searchTerm,
      searchField,
      role,
      status,
    }),
    [searchTerm, searchField, role, status]
  );
  const [users, setUsers] = useState([]);
  const [filters, setFilters] = useState(mergedInitial);
  const [loading, setLoading] = useState(true);

  const buildParams = useCallback((activeFilters) => {
    const current = activeFilters || baseFilters;
    const params = {};

    if (current.searchTerm.trim()) {
      const value = current.searchTerm.trim();

      switch (current.searchField) {
        case "firstName":
          params.firstName = value;
          break;
        case "lastName":
          params.lastName = value;
          break;
        case "email":
          params.email = value;
          break;
        case "username":
        default:
          params.username = value;
          break;
      }
    }

    if (current.status === "BLOCKED") params.isBlocked = true;
    if (current.status === "ACTIVE") params.isBlocked = false;

    return params;
  }, []);

  const load = useCallback(async (activeFilters) => {
    try {
      setLoading(true);
      const res = await api.get("/api/admin/users", { params: buildParams(activeFilters) });
      setUsers(res.data || []);
    } finally {
      setLoading(false);
    }
  }, [buildParams]);

  async function handleSearch(e) {
    if (e) e.preventDefault();
    await load(filters);
  }

  function updateFilter(field, value) {
    setFilters((prev) => ({ ...prev, [field]: value }));
  }

  const filteredUsers = useMemo(() => {
    if (filters.role === "ALL") return users;
    return users.filter((u) => u.role === filters.role);
  }, [users, filters.role]);

  useEffect(() => {
    setFilters(mergedInitial);
    load(mergedInitial);
  }, [mergedInitial, load]);

  function reset() {
    setFilters(baseFilters);
    load(baseFilters);
  }

  function updateUser(userId, updater) {
    setUsers((prev) =>
      prev.map((u) => {
        if (u.id !== userId) return u;
        if (typeof updater === "function") return updater(u);
        return { ...u, ...updater };
      })
    );
  }

  return {
    users: filteredUsers,
    filters,
    updateFilter,
    loading,
    handleSearch,
    reset,
    updateUser,
  };
}
