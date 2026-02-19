import { useQuery } from "@tanstack/react-query";
import { fetchAllReferrals } from "../api/jobApi";
 
export default function useAllReferrals() {
  return useQuery({
    queryKey: ["referrals", "all"],
    queryFn: fetchAllReferrals,
  });
}