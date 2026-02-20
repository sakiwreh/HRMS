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
};
 
export const fetchMyProfile = async (): Promise<EmployeeProfile> => {
  const res = await api.get("/employees/me");
  return res.data;
};