import { createBrowserRouter, Navigate } from "react-router-dom";
import LoginPage from "../modules/auth/pages/LoginPage";
import AppLayout from "../layouts/AppLayout";
import ProtectedRoute from "../routes/ProtectedRoute";
import DashboardPage from "../modules/dashboard/pages/DashboardPage";
import TravelListPage from "../modules/travel/pages/TravelListPage";
import TravelDetailsPage from "../modules/travel/pages/TravelDetailsPage";
export const router = createBrowserRouter([
  /* ---------- PUBLIC ---------- */
  {
    path: "/",
    element: <Navigate to="/login" replace />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
 
  /* ---------- PROTECTED ---------- */
  {
    path: "/dashboard",
    element: (
      <ProtectedRoute>
        <AppLayout />
      </ProtectedRoute>
    ),
 
    children: [
      /* Default dashboard */
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: "travel",
        children: [
          {
            index: true,
            element: <TravelListPage />,
          },
          {
            path: ":id",
            element: <TravelDetailsPage />,
          },
        ],
      },
    ],
  },
  {
    path: "*",
    element: <Navigate to="/login" replace />,
  },
]);