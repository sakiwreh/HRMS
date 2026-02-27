import api from "../../../lib/axios";
 
export type EmployeeProfile = {
  id: number;
  firstName: string;
  middleName: string | null;
  lastName: string;
  email: string | null;
  designation: string | null;
  department: string | null;
  role: string | null;
  managerName: string | null;
  dob: string | null;
  doj: string | null;
  profilePath?: string | null;
};
 
export const fetchMyProfile = async (): Promise<EmployeeProfile> => {
  const res = await api.get("/employees/me");
  return res.data;
};

export const updateMyProfile = async (data: Partial<EmployeeProfile>) => {
  const res = await api.put("/employees/me", data);
  return res.data as EmployeeProfile;
};

export const uploadMyProfilePhoto = async (file: File) => {
  const fd = new FormData();
  fd.append("file", file);
  const res = await api.post("/employees/me/photo", fd, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data as string;
};