import api from "../../../lib/axios"
import type { ExpenseResponse } from "../../expense/api/expenseApi";

export interface TeamMember {
  id: number;
  name: string;
  email: string | null;
  designation: string | null;
  department: string | null;
  dob: string | null;
  doj: string | null;
  profilePath: string | null;
}

export interface TravelPlan {
  id: number;
  title: string;
  description: string;
  destination: string;
  cancelled: boolean;
  departureDate: string;
  returnDate: string;
  createdAt: string;
  createdBy: number;
  maxPerDayAmount: number | null;
}

export const fetchTeamMembers = async (): Promise<TeamMember[]> => {
  const res = await api.get("/manager/team");
  // console.log(res);
  return res.data;
};

export const fetchTeamTravels = async (): Promise<TravelPlan[]> => {
  const res = await api.get("/manager/team/travels");
  return res.data;
};

export const fetchTeamExpenses = async (): Promise<ExpenseResponse[]> => {
  const res = await api.get("/manager/team/expenses");
  return res.data;
};