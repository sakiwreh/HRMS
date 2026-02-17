import { useQuery } from "@tanstack/react-query";
import { fetchMyExpenses } from "../api/expenseApi";
 
export default function useMyExpenses(enabled = true) {
  return useQuery({
    queryKey: ["my-expenses"],
    queryFn: fetchMyExpenses,
    enabled,
  });
}
 