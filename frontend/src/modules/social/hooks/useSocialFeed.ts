import { useQuery } from "@tanstack/react-query";
import {
  fetchSocialFeed,
  type SocialFeedParams,
} from "../api/socialApi";

export default function useSocialFeed(params: SocialFeedParams) {
  return useQuery({
    queryKey: ["social-feed", params],
    queryFn: () => fetchSocialFeed(params),
  });
}