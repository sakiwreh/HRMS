import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteSocialComment } from "../api/socialApi";

type DeleteCommentInput = {
  commentId: number;
  postId: number;
  reason?: string;
};

export default function useDeleteSocialComment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ commentId, reason }: DeleteCommentInput) =>
      deleteSocialComment(commentId, reason),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
      queryClient.invalidateQueries({
        queryKey: ["social-comments", variables.postId],
      });
    },
  });
}