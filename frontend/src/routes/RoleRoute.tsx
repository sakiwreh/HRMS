import { Navigate } from "react-router-dom";
import { useAppSelector } from "../store/hooks";
 
export default function RoleRoute({ children, roles }: any) {
  const { user } = useAppSelector((s) => s.auth);
 
  if (!user || !roles.includes(user.role)) return <Navigate to="/login" replace />;
 
  return children;
}

//Safe to delete