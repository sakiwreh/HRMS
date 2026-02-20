import { useQuery } from "@tanstack/react-query";
import { fetchJobById } from "../api/jobApi";
 
export default function useJob(id?: string) {
  return useQuery({
    queryKey: ["job", id],
    queryFn: () => fetchJobById(Number(id)),
    enabled: !!id,
  });
}