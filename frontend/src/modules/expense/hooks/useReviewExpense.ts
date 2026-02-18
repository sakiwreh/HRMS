import { useMutation, useQueryClient } from "@tanstack/react-query";
import { reviewExpense, type ReviewExpenseRequest } from "../api/expenseApi";
 
export default function useReviewExpense() {
  const qc = useQueryClient();
 
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: ReviewExpenseRequest }) =>
      reviewExpense(id, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pending-expenses"] });
      qc.invalidateQueries({ queryKey: ["filtered-expenses"] });
    },
  });
}
 