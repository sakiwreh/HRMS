import { useInfiniteQuery, useQuery } from "@tanstack/react-query";
import {
  fetchSocialFeed,
  type SocialFeedParams,
} from "../api/socialApi";

export default function useSocialFeed(params: Omit<SocialFeedParams, "page">) {
  return useInfiniteQuery({
    queryKey: ["social-feed", params],
    queryFn: ({pageParam = 0})=> fetchSocialFeed({...params, page:pageParam}),
    initialPageParam:0,
    getNextPageParam: (lastPage) => (lastPage.last ? undefined : lastPage.number + 1),
  });
}