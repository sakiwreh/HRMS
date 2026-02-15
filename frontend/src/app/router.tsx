import { createBrowserRouter, Navigate } from "react-router-dom";
import LoginPage from "../modules/auth/pages/LoginPage";
import DashboardLayout from "../layouts/DashboardLayout";
import ProtectedRoute from "../routes/ProtectedRoute";
import RoleRoute from "../routes/RoleRoute";
 
const router = createBrowserRouter([
  // default open
  {
    path: "/",
    element: <Navigate to="/login" replace />
  },
 
  // auth
  {
    path: "/login",
    element: <LoginPage />
  },
 
  // dashboard
  {
    path: "/dashboard",
    element: (
      <ProtectedRoute>
        <DashboardLayout />
      </ProtectedRoute>
    )
  },
 
  // role example
  {
    path: "/hr",
    element: (
      <ProtectedRoute>
        <RoleRoute roles={["HR"]}>
          <div>HR Panel</div>
        </RoleRoute>
      </ProtectedRoute>
    )
  }
]);
 
export default router;