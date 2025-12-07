import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import PrivateRoute from "../components/routing/PrivateRoute";

import MainLayout from "../components/layout/MainLayout";

import HomePage from "../pages/home/HomePage";

import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";

import UserProfilePage from "../pages/users/UserProfilePage";
import UserEditPage from "../pages/users/UserEditPage";

import SinglePostPage from "../pages/posts/SinglePostPage";
import CreatePostPage from "../pages/posts/CreatePostPage";
import EditPostPage from "../pages/posts/EditPostPage";

import AdminAllUsersPage from "../pages/admin/AdminAllUsersPage";

import NotFoundPage from "../pages/errors/NotFoundPage";

export default function AppRouter() {
    const { user, authLoading } = useAuth();

    if (authLoading) return <p>Loading...</p>;

    return (
        <Routes>
            <Route element={<MainLayout />}>
                <Route path="/" element={<HomePage />} />

                {!user && (
                    <>
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/register" element={<RegisterPage />} />
                    </>
                )}

                {user && (
                    <>
                        <Route path="/login" element={<Navigate to="/" replace />} />
                        <Route path="/register" element={<Navigate to="/" replace />} />
                    </>
                )}

                <Route
                    path="/users/:userId"
                    element={
                        <PrivateRoute>
                            <UserProfilePage />
                        </PrivateRoute>
                    }
                />

                <Route
                    path="/users/:userId/edit"
                    element={
                        <PrivateRoute>
                            <UserEditPage />
                        </PrivateRoute>
                    }
                />

                <Route
                    path="/posts/:postId"
                    element={
                        <PrivateRoute>
                            <SinglePostPage />
                        </PrivateRoute>
                    }
                />

                <Route
                    path="/posts/:postId/edit"
                    element={
                        <PrivateRoute>
                            <EditPostPage />
                        </PrivateRoute>
                    }
                />

                <Route
                    path="/posts/create"
                    element={
                        <PrivateRoute>
                            <CreatePostPage />
                        </PrivateRoute>
                    }
                />

                {user?.role === "ADMIN" && (
                    <>
                        <Route
                            path="/admin/users"
                            element={
                                <PrivateRoute>
                                    <AdminAllUsersPage />
                                </PrivateRoute>
                            }
                        />
                    </>
                )}

                {user && user.role !== "ADMIN" && (
                    <Route path="/admin/*" element={<Navigate to="/" replace />} />
                )}

                {!user && (
                    <Route path="/admin/*" element={<Navigate to="/login" replace />} />
                )}

                <Route path="*" element={<NotFoundPage />} />

            </Route>
        </Routes>
    );
}