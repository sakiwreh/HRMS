import api from "../../../lib/axios";
 
export type EmployeeNode = {
  id: number;
  name: string;
  designation: string | null;
  department: string | null;
  profilePath?: string | null;
};
 
export type OrgChartResponse = {
  selected: EmployeeNode;
  chain: EmployeeNode[];
  reports: EmployeeNode[];
};
 
export type EmployeeLookup = {
  id: number;
  name: string;
  email: string;
  designation: string | null;
  department: string | null;
  profilePath?: string | null;
};
 
export const fetchOrgChart = async (empId: number): Promise<OrgChartResponse> => {
  if (!empId || empId <= 0) throw new Error("Invalid employee ID");
  const res = await api.get(`/employees/org-chart/${empId}`);
  return res.data;
};
 
export const fetchEmployeeLookup = async (): Promise<EmployeeLookup[]> => {
  const res = await api.get("/employees/lookup");
  return res.data;
};