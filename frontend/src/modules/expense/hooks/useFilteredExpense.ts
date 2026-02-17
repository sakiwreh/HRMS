import { useQuery } from "@tanstack/react-query";
import {
  fetchFilteredExpenses,
  type ExpenseFilterParams,
} from "../api/expenseApi";
 
export default function useFilteredExpenses(
  params: ExpenseFilterParams,
  enabled = true
) {
  return useQuery({
    queryKey: ["filtered-expenses", params],
    queryFn: () => fetchFilteredExpenses(params),
    enabled,
  });
}
 