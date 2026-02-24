import { useMutation, useQueryClient } from "@tanstack/react-query";
import { unlikeSocialPost } from "../api/socialApi";

export default function useUnlikeSocialPost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (postId: number) => unlikeSocialPost(postId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
    },
  });
}