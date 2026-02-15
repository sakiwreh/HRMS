import { Navigate } from "react-router-dom";
import { useAppSelector } from "../store/hooks";
 
export default function ProtectedRoute({ children }: any) {
  const { user, initialized } = useAppSelector((s) => s.auth);
 
  if (!initialized) return <div>Loading...</div>;
  if (!user) return <Navigate to="/login" replace />;
 
  return children;
}