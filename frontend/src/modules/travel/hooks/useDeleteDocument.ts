import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteDocument } from "../api/travelApi";
 
export default function useDeleteDocument(travelId?: number) {
  const qc = useQueryClient();
 
  return useMutation({
    mutationFn: (docId: number) => deleteDocument(docId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["documents", travelId] });
    },
  });
}
 