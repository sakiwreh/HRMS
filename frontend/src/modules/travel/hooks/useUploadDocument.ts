import { useMutation, useQueryClient } from "@tanstack/react-query";
import { uploadDocument } from "../api/travelApi";
 
export default function useUploadDocument(travelId: string) {
  const qc = useQueryClient();
 
  return useMutation({
    mutationFn: uploadDocument,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["documents", travelId] });
    },
  });
}
 