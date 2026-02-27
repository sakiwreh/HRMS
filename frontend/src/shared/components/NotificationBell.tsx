import { useRef, useState, useEffect } from "react";
import {
  useNotifications,
  useUnreadCount,
  useMarkAsRead,
} from "../../modules/notifications/hooks/useNotifications";
 
export default function NotificationBell() {
  const [open, setOpen] = useState(false);
  const panelRef = useRef<HTMLDivElement>(null);
 
  const { data: unread = 0 } = useUnreadCount();
  const { data: notifications = [], isLoading } = useNotifications();
  const markRead = useMarkAsRead();
 
  // Close when clicking outside
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (panelRef.current && !panelRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);
 
  const handleClick = (id: number, isRead: boolean) => {
    if (!isRead) markRead.mutate(id);
  };
 
  const timeAgo = (dateStr: string) => {
    // Get difference
    const diff = Date.now() - new Date(dateStr).getTime();
    // Get mins
    const mins = Math.floor(diff / 60_000);
    // Assign title
    if (mins < 1) return "just now";
    if (mins < 60) return `${mins}m ago`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return `${hrs}h ago`;
    const days = Math.floor(hrs / 24);
    return `${days}d ago`;
  };
 
  return (
    <div className="relative" ref={panelRef}>
      {/* Bell */}
      <button
        onClick={() => setOpen((o) => !o)}
        className="relative p-1.5 rounded-lg hover:bg-gray-100 transition-colors"
        aria-label="Notifications"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="h-5 w-5 text-white hover:text-gray-900"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={2}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
          />
        </svg>
 
        {/* Unread */}
        {unread > 0 && (
          <span className="absolute -top-0.5 -right-0.5 bg-red-500 text-white text-[10px] font-bold rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
            {unread > 99 ? "99+" : unread}
          </span>
        )}
      </button>
 
      {/* Dropdown */}
      {open && (
        <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border z-50 max-h-96 flex flex-col">
          <div className="px-4 py-3 border-b font-semibold text-sm text-gray-700 flex justify-between items-center">
            <span>Notifications</span>
            {unread > 0 && (
              <span className="text-xs text-blue-600">
                {unread} unread
              </span>
            )}
          </div>
 
          <div className="overflow-y-auto flex-1">
            {isLoading ? (
              <div className="p-4 text-center text-sm text-gray-400">
                Loading...
              </div>
            ) : notifications.length === 0 ? (
              <div className="p-4 text-center text-sm text-gray-400">
                No notifications yet
              </div>
            ) : (
              notifications.map((n) => (
                <button
                  key={n.id}
                  onClick={() => handleClick(n.id, n.isRead)}
                  className={`w-full text-left px-4 py-3 border-b last:border-b-0 hover:bg-gray-50 transition-colors ${
                    !n.isRead ? "bg-blue-50" : ""
                  }`}
                >
                  <div className="flex justify-between items-start gap-2">
                    <p className="text-sm font-medium text-gray-800 leading-tight">
                      {n.subject}
                    </p>
                    {!n.isRead && (
                      <span className="mt-1 h-2 w-2 rounded-full bg-blue-500 shrink-0" />
                    )}
                  </div>
                  <p className="text-xs text-gray-500 mt-1 line-clamp-2">
                    {n.body}
                  </p>
                  <p className="text-[10px] text-gray-400 mt-1">
                    {timeAgo(n.createdAt)}
                  </p>
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}