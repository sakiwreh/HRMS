import { useQuery } from "@tanstack/react-query";
import { fetchSocialComments } from "../api/socialApi";

export default function useSocialComments(postId: number, enabled = true) {
  return useQuery({
    queryKey: ["social-comments", postId],
    queryFn: () => fetchSocialComments(postId),
    enabled,
  });
}