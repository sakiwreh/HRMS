import { Navigate } from "react-router-dom";
import { useAppSelector } from "../store/hooks";
 
export default function ProtectedRoute({ children }: { children: any }) {
  const reduxUser = useAppSelector(state => state.auth.user);
  const localUser = localStorage.getItem("auth_user");
 
  // if both missing, not authenticated
  if (!reduxUser && !localUser)
    return <Navigate to="/login" replace />;
 
  return children;
}
 