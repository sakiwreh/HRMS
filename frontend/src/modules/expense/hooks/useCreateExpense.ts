import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createExpense, type ExpenseRequest } from "../api/expenseApi";
 
export default function useCreateExpense() {
  const qc = useQueryClient();
 
  return useMutation({
    mutationFn: (data: ExpenseRequest) => createExpense(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["my-expenses"] });
    },
  });
}
 