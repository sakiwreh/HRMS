import { createBrowserRouter, Navigate } from "react-router-dom";
import LoginPage from "../modules/auth/pages/LoginPage";
import AppLayout from "../layouts/AppLayout";
import ProtectedRoute from "../routes/ProtectedRoute";
import DashboardPage from "../modules/dashboard/pages/DashboardPage";
import TravelListPage from "../modules/travel/pages/TravelListPage";
import TravelDetailsPage from "../modules/travel/pages/TravelDetailsPage";
import MyExpensesPage from "../modules/expense/pages/MyExpensePage";
import ExpenseReviewPage from "../modules/expense/pages/ExpenseReviewPage";
import JobListPage from "../modules/jobs/pages/JobListPage";
import JobDetailPage from "../modules/jobs/pages/JobDetailsPage";
import MyReferralsPage from "../modules/jobs/pages/MyReferralsPage";
import AllReferralsPage from "../modules/jobs/pages/AllReferralsPage";
import OrgChartPage from "../modules/org/pages/OrgChartPage";
import ExpenseDetailPage from "../modules/expense/pages/ExpenseDetailPage";
import GamesLayout from "../modules/games/pages/GamesLayout";
import MyGameActivity from "../modules/games/pages/MyGameActivity";
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
          {
            path:":id",
            element:<ExpenseDetailPage/>
          }
        ],
      },
      //Jobs
      {
        path:"jobs",
        children:[
          {
            index:true,
            element:<JobListPage/>
          },
          {
            path:":id",
            element:<JobDetailPage/>
          },
        ]
      },
      //Referrals
      {
        path:"referrals",
        element:<MyReferralsPage/>
      },
      {
        path:"referrals/review",
        element:<AllReferralsPage/>
      },
      //Organization
      {
        path:"org",
        element:<OrgChartPage/>
      },
      // 
      {
        path: "games",
        children: [
          {
            index: true,
            element: <GamesLayout/>
          },
          {
            path: "activity",
            element: <MyGameActivity/>
          }
        ]
      }
    ],
  },
  {
    path: "*",
    element: <Navigate to="/login" replace />,
  },
]);