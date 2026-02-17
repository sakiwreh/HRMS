import { useQuery } from "@tanstack/react-query";
import { fetchParticipants } from "../api/travelApi";
 
export default function useParticipants(travelId: number) {
  return useQuery({
    queryKey: ["participants", travelId],
    queryFn: () => fetchParticipants(travelId),
    enabled: !!travelId,
  });
}