import { NavLink } from "react-router-dom";
import { navigation } from "../../../config/navigation";
import { useAppSelector } from "../../../store/hooks";
 
export default function Sidebar() {
  const user = useAppSelector(state => state.auth.user);
  if (!user) return null;
 
  const items = navigation[user.role];
 
  return (
    <aside className="w-56 bg-white border-r p-4">
      <h1 className="text-lg font-bold mb-6">HRMS</h1>
 
      <nav className="flex flex-col gap-2">
        {items.map(item => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `p-2 rounded ${
                isActive ? "bg-blue-500 text-white" : "hover:bg-gray-200"
              }`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}