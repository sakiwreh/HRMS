import { useQuery } from "@tanstack/react-query";
import { fetchTravelById } from "../api/travelApi";
 
export default function useTravel(id: string) {
  return useQuery({
    queryKey: ["travel", id],
    queryFn: () => fetchTravelById(id),
    enabled: !!id,
  });
}
 