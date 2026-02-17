import { useQuery } from "@tanstack/react-query";
import { fetchEmployees } from "../api/travelApi";
 
export default function useEmployees() {
  return useQuery({
    queryKey: ["employees"],
    queryFn: fetchEmployees,
  });
}