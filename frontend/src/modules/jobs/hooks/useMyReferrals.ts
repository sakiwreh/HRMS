import { useQuery } from "@tanstack/react-query";
import { fetchMyReferrals } from "../api/jobApi";
 
export default function useMyReferrals() {
  return useQuery({
    queryKey: ["referrals", "me"],
    queryFn: fetchMyReferrals,
  });
}