
import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { clearUser } from "../../../modules/auth/authSlice";
import NotificationBell from "../NotificationBell";
 
export default function Header() {
  const user = useAppSelector(state => state.auth.user);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
 
  const logout = () => {
    localStorage.removeItem("auth_user");
    dispatch(clearUser());
    navigate("/login");
  };
 
  return (
    <header className="bg-white border-b px-6 py-3 flex justify-between">
      <div className="font-semibold">Welcome, {user?.name}</div>
 
      <div className="flex items-center gap-4">
        <NotificationBell/>
        <span className="text-sm text-gray-500">{user?.role}</span>
        <button
          onClick={logout}
          className="text-red-500 text-sm hover:underline"
        >
          Logout
        </button>
      </div>
    </header>
  );
}