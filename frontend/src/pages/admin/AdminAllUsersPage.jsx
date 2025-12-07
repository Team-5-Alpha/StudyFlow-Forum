import { useSearchParams } from "react-router-dom";
import { useAdminUsers } from "../../hooks/user/useAdminUsers";
import { useAuth } from "../../context/AuthContext";

import Spinner from "../../components/common/Spinner";
import AdminUsersList from "../../components/admin/AdminUsersList";

import "../../styles/pages/admin/AdminAllUsersPage.css";

export default function AdminAllUsersPage() {
  const { user: actingUser } = useAuth();
  const [searchParams] = useSearchParams();
  const presetUsername = searchParams.get("username") || "";

  const {
    users,
    filters,
    updateFilter,
    loading,
    handleSearch,
    reset,
    updateUser,
  } = useAdminUsers({
    searchTerm: presetUsername,
    searchField: "username",
  });

  if (!actingUser || actingUser.role !== "ADMIN") return <p>Access denied.</p>;
  if (loading) return <Spinner />;

  return (
    <div className="admin-users-page">
      <h2>Admin: All Users</h2>

      <div>
        <form className="admin-users-form" onSubmit={handleSearch}>
          <div className="admin-users-grid">
            <div className="input-wrapper admin-searchbar">
              <label className="input-label">Search</label>
              <input
                className="input-field"
                placeholder="Search users..."
                value={filters.searchTerm}
                onChange={(e) => updateFilter("searchTerm", e.target.value)}
              />
              <div className="admin-search-fields">
                {["username", "firstName", "lastName", "email"].map((field) => (
                  <label key={field} className="admin-search-option">
                    <input
                      type="radio"
                      name="searchField"
                      value={field}
                      checked={filters.searchField === field}
                      onChange={(e) => updateFilter("searchField", e.target.value)}
                    />
                    <span>{field}</span>
                  </label>
                ))}
              </div>
            </div>
            <div className="input-wrapper">
              <label className="input-label">Role</label>
              <select
                className="input-field"
                value={filters.role}
                onChange={(e) => updateFilter("role", e.target.value)}
              >
                <option value="ALL">All</option>
                <option value="USER">User</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>
            <div className="input-wrapper">
              <label className="input-label">Status</label>
              <select
                className="input-field"
                value={filters.status}
                onChange={(e) => updateFilter("status", e.target.value)}
              >
                <option value="ALL">All</option>
                <option value="ACTIVE">Active</option>
                <option value="BLOCKED">Blocked</option>
              </select>
            </div>
          </div>

          <div className="admin-users-actions">
            <button className="primary-btn" type="submit">
              Search
            </button>
            <button
              className="secondary-btn"
              type="button"
              onClick={() => {
                reset();
                handleSearch();
              }}
            >
              Reset
            </button>
          </div>
        </form>
      </div>

      <AdminUsersList
        users={users}
        actingUser={actingUser}
        onUpdate={updateUser}
      />
    </div>
  );
}
