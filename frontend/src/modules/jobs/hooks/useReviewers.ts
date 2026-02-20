import { useQuery } from "@tanstack/react-query";
import { fetchReviewers } from "../api/jobApi";
 
export default function useReviewers(jobId?: number) {
  return useQuery({
    queryKey: ["reviewers", jobId],
    queryFn: () => fetchReviewers(jobId!),
    enabled: !!jobId,
  });
}