import { useQuery } from "@tanstack/react-query";
import { fetchMyDrafts } from "../api/expenseApi";
 
export default function useDrafts() {
  return useQuery({
    queryKey: ["my-drafts"],
    queryFn: fetchMyDrafts,
  });
}
 