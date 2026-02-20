import { useQuery } from "@tanstack/react-query";
import { fetchEmployeeLookup } from "../api/orgApi";
 
export default function useEmployeeLookup() {
  return useQuery({
    queryKey: ["employee-lookup"],
    queryFn: fetchEmployeeLookup,
    staleTime: 60_000,
  });
}