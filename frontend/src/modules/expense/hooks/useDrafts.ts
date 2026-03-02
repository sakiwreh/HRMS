import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchMyDrafts, updateDraft, type ExpenseRequest } from "../api/expenseApi";
import toast from "react-hot-toast";
 
export default function useDrafts() {
  return useQuery({
    queryKey: ["my-drafts"],
    queryFn: fetchMyDrafts,
  });
}

export function useUpdateDraft(){
  const qc = useQueryClient();
  return useMutation({
    mutationFn:({id,data}:{id:number,data:ExpenseRequest})=>updateDraft(id,data),
    onSuccess: () => {
      toast.success("Draft updated");
      qc.invalidateQueries({queryKey: ["my-drafts"]});
      qc.invalidateQueries({queryKey: ["my-expenses"]});
    }
  })
}
 