import { NavLink } from "react-router-dom";
 
const menu = [
  { name: "Dashboard", path: "/dashboard" },
  { name: "Travel Plans", path: "/dashboard/travel" },
  { name: "Expense Review", path: "/dashboard/expenses" },
  { name: "Jobs", path: "/dashboard/jobs" },
  { name: "Organization", path: "/dashboard/org" },
  { name: "Games", path: "/dashboard/games" },
  { name: "Notifications", path: "/dashboard/notifications" },
];
 
export default function Sidebar() {
  return (
    <aside className="w-64 h-screen bg-white border-r border-gray-200 flex flex-col">
 
      {/* Logo */}
      <div className="px-6 py-5 text-xl font-bold border-b">
        HRMS
      </div>
 
      {/* Menu */}
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
                  ? "bg-blue-50 text-blue-600 border-l-4 border-blue-600"
                  : "text-gray-600 hover:bg-gray-100 hover:text-gray-900"
              }
              `
            }
          >
            {item.name}
          </NavLink>
        ))}
      </nav>
 
      {/* Footer user */}
      <div className="p-4 border-t text-xs text-gray-400">
        HRMS v1.0
      </div>
    </aside>
  );
}
 