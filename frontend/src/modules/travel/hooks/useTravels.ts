import { useQuery } from "@tanstack/react-query";
import { fetchTravels } from "../api/travelApi";
 
export default function useTravels() {
  return useQuery({
    queryKey: ["travels"],
    queryFn: fetchTravels,
  });
}