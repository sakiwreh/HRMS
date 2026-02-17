import { createBrowserRouter, Navigate } from "react-router-dom";
import LoginPage from "../modules/auth/pages/LoginPage";
import AppLayout from "../layouts/AppLayout";
import ProtectedRoute from "../routes/ProtectedRoute";
import DashboardPage from "../modules/dashboard/pages/DashboardPage";
import TravelListPage from "../modules/travel/pages/TravelListPage";
import TravelDetailsPage from "../modules/travel/pages/TravelDetailsPage";
import MyExpensesPage from "../modules/expense/pages/MyExpensePage";
import ExpenseReviewPage from "../modules/expense/pages/ExpenseReviewPage";
export const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/login" replace />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/dashboard",
    element: (
      <ProtectedRoute>
        <AppLayout />
      </ProtectedRoute>
    ),
 
    children: [
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
      // Expenses
      {
        path:"expenses",
        children:[
          {
            index:true,
            element: <MyExpensesPage/>
          },
          {
            path:"review",
            element:<ExpenseReviewPage/>
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