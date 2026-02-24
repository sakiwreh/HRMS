import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  updateSocialComment,
  type SocialCommentUpdateRequest,
} from "../api/socialApi";

type UpdateCommentInput = {
  commentId: number;
  postId: number;
  payload: SocialCommentUpdateRequest;
};

export default function useUpdateSocialComment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ commentId, payload }: UpdateCommentInput) =>
      updateSocialComment(commentId, payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
      queryClient.invalidateQueries({
        queryKey: ["social-comments", variables.postId],
      });
    },
  });
}