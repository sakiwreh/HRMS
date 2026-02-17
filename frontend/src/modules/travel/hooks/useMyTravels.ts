import { useQuery } from "@tanstack/react-query";
import { fetchMyTravels } from "../api/travelApi";
 
export default function useMyTravels(enabled = true) {
  return useQuery({
    queryKey: ["my-travels"],
    queryFn: fetchMyTravels,
    enabled,
  });
}