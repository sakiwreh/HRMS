import { Navigate } from "react-router-dom";
 
export default function SessionRedirect() {
  const user = localStorage.getItem("auth_user");
 
  if (user) return <Navigate to="/dashboard" replace />;
 
  return <Navigate to="/login" replace />;
}

//Safe to delete