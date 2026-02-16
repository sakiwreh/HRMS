import { Navigate } from "react-router-dom";
 
export default function SessionRedirect() {
  const user = localStorage.getItem("auth_user");
 
  // already logged in → go dashboard
  if (user) return <Navigate to="/dashboard" replace />;
 
  // not logged → go login
  return <Navigate to="/login" replace />;
}