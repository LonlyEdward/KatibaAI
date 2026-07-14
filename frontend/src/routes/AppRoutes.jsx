import { Routes, Route } from "react-router-dom";

import Landing from "../pages/landing/Landing";
import Login from "../pages/auth/Login";
import Register from "../pages/auth/Register";
import Chat from "../pages/chat/Chat";
import History from "../pages/history/History";
import Profile from "../pages/profile/Profile";
import Settings from "../pages/settings/Settings";
import NotFound from "../pages/NotFound";

import MainLayout from "../layouts/MainLayout";
import AuthLayout from "../layouts/AuthLayout";

import ProtectedRoute from "./ProtectedRoute";

function AppRoutes() {
  return (
    <Routes>
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<Login />} />

        <Route path="/register" element={<Register />} />
      </Route>

      <Route path="/" element={<Landing />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/chat" element={<Chat />} />

          <Route path="/history" element={<History />} />

          <Route path="/profile" element={<Profile />} />

          <Route path="/settings" element={<Settings />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

export default AppRoutes;
