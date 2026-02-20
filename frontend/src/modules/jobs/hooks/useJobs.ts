import { useQuery } from "@tanstack/react-query";
import { fetchJobs } from "../api/jobApi";
 
export default function useJobs() {
  return useQuery({
    queryKey: ["jobs"],
    queryFn: fetchJobs,
  });
}