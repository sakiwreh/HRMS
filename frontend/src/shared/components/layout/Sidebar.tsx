import { NavLink } from "react-router-dom";
import { useAppSelector } from "../../../store/hooks";
import { navigation, type Role } from "../../../config/navigation";
 
export default function Sidebar() {
  const user = useAppSelector((s) => s.auth.user);
  const role = (user?.role ?? "EMPLOYEE") as Role;
  const menu = navigation[role];
 
  return (
    <aside className="w-64 h-screen border-r border-gray-200 flex flex-col bg-[#3FA037]">
      <div className="px-6 py-5 text-xl font-bold text-white border-b">HRMS</div>
      <nav className="flex-1 p-3 space-y-1">
        {menu.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            end
            className={({ isActive }) =>
              `
              flex items-center px-4 py-2 rounded-lg text-sm font-medium transition-all duration-150
              ${
                isActive
                  ? "bg-blue-50 text-blue-600"
                  : "text-white hover:bg-gray-100 hover:text-gray-900"
              }
              `
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
      <div className="p-4 border-t text-xs text-white">HRMS v1.0</div>
    </aside>
  );
}