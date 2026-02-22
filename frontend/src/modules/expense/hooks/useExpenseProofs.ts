import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  fetchExpenseProofs,
  uploadExpenseProof,
  deleteExpenseProof,
} from "../api/expenseApi";
import toast from "react-hot-toast";
 
export function useExpenseProofs(expenseId?: number) {
  return useQuery({
    queryKey: ["expense-proofs", expenseId],
    queryFn: () => fetchExpenseProofs(expenseId!),
    enabled: !!expenseId,
  });
}
 
export function useUploadProof(expenseId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (formData: FormData) => uploadExpenseProof(expenseId, formData),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["expense-proofs", expenseId] });
      qc.invalidateQueries({ queryKey: ["my-expenses"] });
      toast.success("Proof uploaded");
    },
  });
}
 
export function useDeleteProof(expenseId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (proofId: number) => deleteExpenseProof(proofId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["expense-proofs", expenseId] });
      qc.invalidateQueries({ queryKey: ["my-expenses"] });
      toast.success("Proof deleted");
    },
  });
}
 