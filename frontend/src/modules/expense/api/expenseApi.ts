import api from "../../../lib/axios";
 
export interface ExpenseRequest {
  travelId: number;
  categoryId: number;
  amount: number;
  description?: string;
  expenseDate: string;
}
 
export interface ReviewExpenseRequest {
  approved: boolean;
  remarks?: string;
}
 
export interface ExpenseResponse {
  id: number;
  employeeId: number;
  category: string;
  amount: number;
  description: string;
  expenseDate: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
  reviewedBy: string | null;
  remarks: string | null;
}
 
export interface ExpenseCategory {
  id: number;
  name: string;
  limit_in_inr: number | null;
}
 
export interface ExpenseFilterParams {
  employeeId?: number;
  status?: "PENDING" | "APPROVED" | "REJECTED";
  travelId?: number;
  fromDate?: string;
  toDate?: string;
}
 
export const createExpense = async (data: ExpenseRequest): Promise<ExpenseResponse> => {
  const res = await api.post("/expenses", data);
  return res.data;
};
 
export const fetchMyExpenses = async (): Promise<ExpenseResponse[]> => {
  const res = await api.get("/expenses/me");
  return res.data;
};
 
export const fetchPendingExpenses = async (): Promise<ExpenseResponse[]> => {
  const res = await api.get("/expenses/pending");
  return res.data;
};
 
export const reviewExpense = async (
  id: number,
  data: ReviewExpenseRequest
): Promise<ExpenseResponse> => {
  const res = await api.patch(`/expenses/${id}/review`, data);
  return res.data;
};
 
export const fetchCategories = async (): Promise<ExpenseCategory[]> => {
  const res = await api.get("/expenses/categories");
  return res.data;
};
 
export const fetchTravelExpenseTotal = async (travelId: number): Promise<number> => {
  const res = await api.get(`/expenses/travel/${travelId}/total`);
  return res.data;
};
 
export const fetchFilteredExpenses = async (
  params: ExpenseFilterParams
): Promise<ExpenseResponse[]> => {
  const res = await api.get("/expenses/filter", { params });
  return res.data;
};