import { useQuery } from "@tanstack/react-query";
import { fetchCategories } from "../api/expenseApi";
 
export default function useCategories() {
  return useQuery({
    queryKey: ["expense-categories"],
    queryFn: fetchCategories,
  });
}
 