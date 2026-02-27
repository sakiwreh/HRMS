import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { clearUser } from "../../../modules/auth/authSlice";
import NotificationBell from "../NotificationBell";
import ProfileModal from "../../../modules/profile/components/ProfileModal";
import { queryClient } from "../../../app/providers";
import useProfile from "../../../modules/profile/hooks/useProfile";
import api from "../../../lib/axios"
 
export default function Header() {
  const user = useAppSelector((state) => state.auth.user);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [profileOpen, setProfileOpen] = useState(false);
  const {data: profile} = useProfile();
 
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
      <header className="bg-[#2F8A2F] border-b px-6 py-3 flex justify-between items-center">
        <div className="font-semibold text-white">Welcome, {user?.name}</div>
 
        <div className="flex items-center gap-4">
          <NotificationBell />
          <span className="text-sm text-white">{user?.role}</span>
 
          {/* Profile avatar */}
          <button
            type="button"
            onClick={() => setProfileOpen(true)}
            className="w-10 h-10 rounded-full overflow-hidden bg-gray-100"
            title="View Profile"
          >
            {profile?.profilePath ? (
              <img src={`${api.defaults.baseURL}/employees/photo/${profile.id}`} alt="avatar" className="w-full h-full object-cover" />
            ) : (
              initials
            )}
          </button>
 
          <button
            onClick={logout}
            className="text-red-700 text-sm hover:underline"
          >
            Logout
          </button>
        </div>
      </header>
 
      <ProfileModal open={profileOpen} onClose={() => setProfileOpen(false)} />
    </>
  );
}