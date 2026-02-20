import { useQuery } from "@tanstack/react-query";
import { fetchMyProfile } from "../api/profileApi";
import { useAppSelector } from "../../../store/hooks";
 
export default function useProfile() {
    const userId = useAppSelector((s)=>s.auth.user?.id);
  return useQuery({
    queryKey: ["my-profile"],
    queryFn: fetchMyProfile,
    enabled: !!userId,
    staleTime: 60_000,
    retry: 1,
  });
}