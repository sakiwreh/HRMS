import { useAppSelector } from "../../../store/hooks";
import EmployeeDashboard from "./EmployeeDashboard";
import HrDashboard from "./HrDashboard";
import ManagerDashboard from "./ManagerDashboard";
 
export default function DashboardPage() {
  const user = useAppSelector(state => state.auth.user);
 
  if (!user) return null;
 
  if (user.role === "HR") return <HrDashboard />;
  if (user.role === "MANAGER") return <ManagerDashboard />;
  return <EmployeeDashboard />;
}
 