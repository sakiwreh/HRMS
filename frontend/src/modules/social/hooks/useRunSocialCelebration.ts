import { useMutation, useQueryClient } from "@tanstack/react-query";
import { runDailySocialCelebrations } from "../api/socialApi";

export default function useRunSocialCelebrations() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => runDailySocialCelebrations(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["social-feed"] });
    },
  });
}