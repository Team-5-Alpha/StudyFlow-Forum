import api from "../api/axios";

export function login(identifier, password) {
  return api.post("/api/public/auth/login", { identifier, password });
}

export function register(form) {
  return api.post("/api/public/auth/register", form);
}

export async function logout() {
  await api.post("/api/private/auth/logout");
}

export async function getCurrentUser() {
  return await api.get("/api/private/auth/me");
}
