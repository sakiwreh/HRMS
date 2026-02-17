import { useMutation, useQueryClient } from "@tanstack/react-query";
import { uploadDocument } from "../api/travelApi";
 
export function useUploadDocument(travelId?: number) {
  const qc = useQueryClient();
 
  return useMutation({
    mutationFn: (formData: FormData) => {
      if (!travelId) throw new Error("TravelId missing");
      return uploadDocument(travelId, formData);
    },
 
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["documents", travelId] });
    },
  });
}
 