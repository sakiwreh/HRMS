import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  createSocialPost,
  type SocialPostCreateInput,
  type SocialPostCreateRequest,
} from "../api/socialApi";

export default function useCreateSocialPost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (input: SocialPostCreateInput) => createSocialPost(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
    },
  });
}