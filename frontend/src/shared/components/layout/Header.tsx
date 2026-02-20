import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { clearUser } from "../../../modules/auth/authSlice";
import NotificationBell from "../NotificationBell";
import ProfileModal from "../../../modules/profile/components/ProfileModal";
import { queryClient } from "../../../app/providers";
 
export default function Header() {
  const user = useAppSelector((state) => state.auth.user);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [profileOpen, setProfileOpen] = useState(false);
 
  const logout = () => {
    localStorage.removeItem("auth_user");
    dispatch(clearUser());
    queryClient.clear();
    navigate("/login");
  };
 
  const initials = user?.name
    ? user.name
        .split(" ")
        .map((w) => w[0])
        .slice(0, 2)
        .join("")
        .toUpperCase()
    : "?";
 
  return (
    <>
      <header className="bg-white border-b px-6 py-3 flex justify-between items-center">
        <div className="font-semibold">Welcome, {user?.name}</div>
 
        <div className="flex items-center gap-4">
          <NotificationBell />
          <span className="text-sm text-gray-500">{user?.role}</span>
 
          {/* Profile avatar */}
          <button
            type="button"
            onClick={() => setProfileOpen(true)}
            className="w-8 h-8 rounded-full bg-blue-500 text-white text-xs font-semibold flex items-center justify-center hover:bg-blue-600 transition"
            title="View Profile"
          >
            {initials}
          </button>
 
          <button
            onClick={logout}
            className="text-red-500 text-sm hover:underline"
          >
            Logout
          </button>
        </div>
      </header>
 
      <ProfileModal open={profileOpen} onClose={() => setProfileOpen(false)} />
    </>
  );
}