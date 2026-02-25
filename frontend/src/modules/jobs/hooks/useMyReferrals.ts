import { useQuery } from "@tanstack/react-query";
import { fetchMyReferrals } from "../api/jobApi";
 
export default function useMyReferrals() {
  return useQuery({
    queryKey: ["referrals", "me"],
    queryFn: fetchMyReferrals,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}