import { useQuery } from "@tanstack/react-query";
import { fetchOrgChart } from "../api/orgApi";
 
export default function useOrgChart(empId: number | null) {
  return useQuery({
    queryKey: ["org-chart", empId],
    queryFn: () => fetchOrgChart(empId!),
    enabled: !!empId && empId > 0,
    retry: 1,
    staleTime: 30_000,
  });
}