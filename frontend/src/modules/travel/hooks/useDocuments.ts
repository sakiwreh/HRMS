import { useQuery } from "@tanstack/react-query";
import { fetchDocuments } from "../api/travelApi";
 
export default function useDocuments(travelId?: number) {
  return useQuery({
    queryKey: ["documents", travelId],
    queryFn: () => fetchDocuments(travelId!),
    enabled: !!travelId,
  });
}
 