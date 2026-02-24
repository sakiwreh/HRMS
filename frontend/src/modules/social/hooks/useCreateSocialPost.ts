import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  createSocialPost,
  type SocialPostCreateRequest,
} from "../api/socialApi";

export default function useCreateSocialPost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: SocialPostCreateRequest) => createSocialPost(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
    },
  });
}