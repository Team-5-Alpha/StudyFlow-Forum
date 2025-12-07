import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});

function normalizeError(status, payload = {}) {
  return {
    success: false,
    message: payload.message || "Request failed.",
    errorCode: payload.errorCode || null,
    errors: payload.errors || null,
    path: payload.path || null,
    status,
  };
}

api.interceptors.response.use(
  (response) => {
    const body = response.data;

    if (body && typeof body.success === "boolean") {
      if (!body.success) {
        return Promise.reject(
          normalizeError(response.status, body)
        );
      }

      return {
        ...response,
        data: body.data,
        meta: {
          success: body.success,
          message: body.message,
          errorCode: body.errorCode,
          errors: body.errors,
          path: body.path,
          timestamp: body.timestamp,
        },
      };
    }

    return response;
  },

  (error) => {
    if (!error.response) {
      return Promise.reject(
        normalizeError(null, {
          message: "Network error. Server offline?",
        })
      );
    }

    const { status, data } = error.response;

    if (data && typeof data.success === "boolean") {
      return Promise.reject(normalizeError(status, data));
    }

    if (data && data.message) {
      return Promise.reject(normalizeError(status, data));
    }

    return Promise.reject(
      normalizeError(status, { message: "Unexpected error" })
    );
  }
);

export default api;
