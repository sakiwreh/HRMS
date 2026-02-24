import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteSocialPost } from "../api/socialApi";

type DeletePostInput = {
  postId: number;
  reason?: string;
};

export default function useDeleteSocialPost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ postId, reason }: DeletePostInput) =>
      deleteSocialPost(postId, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
    },
  });
}