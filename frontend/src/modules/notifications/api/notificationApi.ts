import api from "../../../lib/axios";
 
export interface Notification {
  id: number;
  subject: string;
  body: string;
  read: boolean;
  createdAt: string;
}
 
export const fetchNotifications = async (): Promise<Notification[]> => {
  const res = await api.get("/notifications/me");
  return res.data;
};
 
export const fetchUnreadCount = async (): Promise<number> => {
  const res = await api.get("/notifications/unread-count");
  return res.data;
};
 
export const markAsRead = async (id: number): Promise<void> => {
  await api.patch(`/notifications/read/${id}`);
};