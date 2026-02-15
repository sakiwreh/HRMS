import { NavLink } from "react-router-dom";
 
interface Props {
  collapsed: boolean;
  setCollapsed: (val: boolean) => void;
}
 
export default function Sidebar({ collapsed }: Props) {
  return (
    <div
      className={`bg-white border-r transition-all duration-300 ${
        collapsed ? "w-16" : "w-56"
      }`}
    >
      <div className="p-4 font-bold text-blue-600 text-lg">
        {collapsed ? "HR" : "HRMS"}
      </div>
 
      <nav className="flex flex-col gap-1 px-2">
        <NavItem to="/dashboard" label="Dashboard" collapsed={collapsed} />
        <NavItem to="/travel" label="Travel" collapsed={collapsed} />
        <NavItem to="/expense" label="Expense" collapsed={collapsed} />
        <NavItem to="/jobs" label="Jobs" collapsed={collapsed} />
      </nav>
    </div>
  );
}
 
function NavItem({
  to,
  label,
  collapsed,
}: {
  to: string;
  label: string;
  collapsed: boolean;
}) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `px-3 py-2 rounded text-sm ${
          isActive ? "bg-blue-500 text-white" : "hover:bg-gray-200"
        }`
      }
    >
      {collapsed ? label.charAt(0) : label}
    </NavLink>
  );
}
 