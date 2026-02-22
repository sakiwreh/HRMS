import api from "../../../lib/axios";
 
//Expenses
 
export interface ExpenseRequest {
  travelId: number;
  categoryId: number;
  amount: number;
  description?: string;
  expenseDate: string;
  draft?: boolean;
}
 
export interface ReviewExpenseRequest {
  approved: boolean;
  remarks?: string;
}
 
export interface ExpenseResponse {
  id: number;
  employeeId: number;
  employeeName: string | null;
  travelId: number | null;
  travelTitle: string | null;
  category: string;
  amount: number;
  description: string;
  expenseDate: string;
  status: "DRAFT" | "PENDING" | "APPROVED" | "REJECTED";
  reviewedBy: string | null;
  remarks: string | null;
  proofCount: number;
}
 
export interface ExpenseCategory {
  id: number;
  name: string;
  limit_in_inr: number | null;
}
 
export interface ExpenseFilterParams {
  employeeId?: number;
  status?: "DRAFT" | "PENDING" | "APPROVED" | "REJECTED";
  travelId?: number;
  fromDate?: string;
  toDate?: string;
}
 
export interface ExpenseProof {
  id: number;
  expenseId: number;
  description: string;
  fileName: string;
  fileSize: number;
  uploadedById: number;
}
 
//Expenses
 
export const createExpense = async (data: ExpenseRequest): Promise<ExpenseResponse> => {
  const res = await api.post("/expenses", data);
  return res.data;
};
 
export const fetchMyExpenses = async (): Promise<ExpenseResponse[]> => {
  const res = await api.get("/expenses/me");
  return res.data;
};

export const fetchExpenseById = async (id: number) : Promise<ExpenseResponse> =>{
  const res = await api.get(`/expenses/${id}`);
  return res.data;
}
 
export const fetchMyDrafts = async (): Promise<ExpenseResponse[]> => {
  const res = await api.get("/expenses/drafts");
  return res.data;
};
 
export const submitDraft = async (id: number): Promise<ExpenseResponse> => {
  const res = await api.patch(`/expenses/${id}/submit`);
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
 
//Category
 
export const fetchCategories = async (): Promise<ExpenseCategory[]> => {
  const res = await api.get("/expenses/categories");
  return res.data;
};
 
//travel total
 
export const fetchTravelExpenseTotal = async (travelId: number): Promise<number> => {
  const res = await api.get(`/expenses/travel/${travelId}/total`);
  return res.data;
};
 
//filter
 
export const fetchFilteredExpenses = async (
  params: ExpenseFilterParams
): Promise<ExpenseResponse[]> => {
  const res = await api.get("/expenses/filter", { params });
  return res.data;
};
 
//proofs
 
export const fetchExpenseProofs = async (expenseId: number): Promise<ExpenseProof[]> => {
  const res = await api.get(`/expenses/${expenseId}/proofs`);
  return res.data;
};
 
export const uploadExpenseProof = async (
  expenseId: number,
  formData: FormData
): Promise<ExpenseProof> => {
  const res = await api.post(`/expenses/${expenseId}/proofs`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};
 
export const downloadExpenseProof = async (proofId: number, fileName: string) => {
  const res = await api.get(`/expenses/proofs/${proofId}/download`, {
    responseType: "blob",
  });
  const blob = new Blob([res.data]);
  const link = document.createElement("a");
  link.href = window.URL.createObjectURL(blob);
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  link.remove();
};
 
export const deleteExpenseProof = async (proofId: number): Promise<void> => {
  await api.delete(`/expenses/proofs/${proofId}/delete`);
};
