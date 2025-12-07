export function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString();
}

export function formatDateTime(dateString) {
  return new Date(dateString).toLocaleString();
}

export function truncate(text, max = 120) {
  return text.length > max ? text.slice(0, max) + "…" : text;
}