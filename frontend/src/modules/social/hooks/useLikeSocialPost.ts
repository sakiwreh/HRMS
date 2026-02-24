import { useMutation, useQueryClient } from "@tanstack/react-query";
import { likeSocialPost } from "../api/socialApi";

export default function useLikeSocialPost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (postId: number) => likeSocialPost(postId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
    },
  });
}