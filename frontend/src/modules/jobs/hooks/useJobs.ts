import { useQuery } from "@tanstack/react-query";
import { fetchJobs } from "../api/jobApi";
 
export default function useJobs(isHR = false) {
  return useQuery({
    queryKey: ["jobs", isHR ? "hr":"all"],
    queryFn: fetchJobs,
  });
}