import { useMutation, useQueryClient } from "@tanstack/react-query";
import { addSocialComment, type SocialCommentCreateRequest } from "../api/socialApi";

type AddCommentInput = {
  postId: number;
  payload: SocialCommentCreateRequest;
};

export default function useAddSocialComment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ postId, payload }: AddCommentInput) =>
      addSocialComment(postId, payload),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
      queryClient.invalidateQueries({ queryKey: ["social-comments", variables.postId] });
    },
  });
}