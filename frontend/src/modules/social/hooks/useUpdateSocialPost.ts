import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateSocialPost, type SocialPostUpdateRequest } from "../api/socialApi";

type UpdatePostInput = {
  postId: number;
  payload: SocialPostUpdateRequest;
};

export default function useUpdateSocialPost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ postId, payload }: UpdatePostInput) => updateSocialPost(postId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
    },
  });
}