import { useQuery } from "@tanstack/react-query";
import { fetchPendingExpenses } from "../api/expenseApi";
 
export default function usePendingExpenses(enabled = true) {
  return useQuery({
    queryKey: ["pending-expenses"],
    queryFn: fetchPendingExpenses,
    enabled,
  });
}
 