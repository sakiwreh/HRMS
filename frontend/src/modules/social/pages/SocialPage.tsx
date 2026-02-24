import { useMemo, useState } from "react";
import toast from "react-hot-toast";
import { useAppSelector } from "../../../store/hooks";
import useEmployeeLookup from "../../org/hooks/useEmployeeLookup";
import type {
  SocialPostCreateRequest,
  SocialPostUpdateRequest,
} from "../api/socialApi";
import SocialFilterBar from "../components/SocialFilterBar";
import SocialPostCard from "../components/SocialPostCard";
import SocialPostComposer from "../components/SocialPostComposer";
import useCreateSocialPost from "../hooks/useCreateSocialPost";
import useDeleteSocialPost from "../hooks/useDeleteSocialPost";
import useLikeSocialPost from "../hooks/useLikeSocialPost";
import useRunSocialCelebrations from "../hooks/useRunSocialCelebration";
import useSocialFeed from "../hooks/useSocialFeed";
import useUnlikeSocialPost from "../hooks/useUnlikeSocialPost";
import useUpdateSocialPost from "../hooks/useUpdateSocialPost";

const toStartDateTime = (dateValue: string): string | undefined => {
  if (!dateValue) return undefined;
  return `${dateValue}T00:00:00`;
};

const toEndDateTime = (dateValue: string): string | undefined => {
  if (!dateValue) return undefined;
  return `${dateValue}T23:59:59`;
};

export default function SocialPage() {
  const user = useAppSelector((state) => state.auth.user);
  const isHr = user?.role === "HR";
  const currentUserId = user?.id ?? 0;

  const [authorId, setAuthorId] = useState("");
  const [tag, setTag] = useState("");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");

  const queryParams = useMemo(() => {
    const parsedAuthorId = authorId ? Number(authorId) : undefined;
    const tagValue = tag.trim();

    return {
      authorId: parsedAuthorId,
      tag: tagValue || undefined,
      from: toStartDateTime(fromDate),
      to: toEndDateTime(toDate),
      page: 0,
      size: 20,
      sort: "createdAt,desc",
    };
  }, [authorId, tag, fromDate, toDate]);

  const { data: employeesData } = useEmployeeLookup();
  const { data: feedData, isLoading } = useSocialFeed(queryParams);

  const createPostMutation = useCreateSocialPost();
  const updatePostMutation = useUpdateSocialPost();
  const deletePostMutation = useDeleteSocialPost();
  const likePostMutation = useLikeSocialPost();
  const unlikePostMutation = useUnlikeSocialPost();
  const runCelebrationsMutation = useRunSocialCelebrations();

  const posts = feedData?.content ?? [];
  const employees = employeesData ?? [];

  const busy =
    createPostMutation.isPending ||
    updatePostMutation.isPending ||
    deletePostMutation.isPending ||
    likePostMutation.isPending ||
    unlikePostMutation.isPending;

  const createPost = async (payload: SocialPostCreateRequest) => {
    await createPostMutation.mutateAsync(payload);
    toast.success("Post created");
  };

  const updatePost = async (postId: number, payload: SocialPostUpdateRequest) => {
    await updatePostMutation.mutateAsync({ postId, payload });
  };

  const deletePost = async (postId: number, reason?: string) => {
    await deletePostMutation.mutateAsync({ postId, reason });
  };

  const likePost = async (postId: number) => {
    await likePostMutation.mutateAsync(postId);
  };

  const unlikePost = async (postId: number) => {
    await unlikePostMutation.mutateAsync(postId);
  };

  const runCelebrations = async () => {
    await runCelebrationsMutation.mutateAsync();
    toast.success("Daily celebrations run completed");
  };

  const clearFilters = () => {
    setAuthorId("");
    setTag("");
    setFromDate("");
    setToDate("");
  };

  if (!user) return null;

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">Achievements & Celebrations</h1>
        {isHr && (
          <button
            type="button"
            onClick={runCelebrations}
            disabled={runCelebrationsMutation.isPending}
            className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm px-4 py-2 rounded-lg disabled:opacity-50"
          >
            {runCelebrationsMutation.isPending
              ? "Running..."
              : "Run Daily Celebrations"}
          </button>
        )}
      </div>

      <SocialPostComposer
        submitting={createPostMutation.isPending}
        onSubmit={createPost}
      />

      <SocialFilterBar
        employees={employees}
        authorId={authorId}
        tag={tag}
        fromDate={fromDate}
        toDate={toDate}
        onAuthorIdChange={setAuthorId}
        onTagChange={setTag}
        onFromDateChange={setFromDate}
        onToDateChange={setToDate}
        onClear={clearFilters}
      />

      {isLoading && <div className="text-sm text-gray-500">Loading social feed...</div>}

      {!isLoading && posts.length === 0 && (
        <div className="bg-white rounded-xl shadow p-6 text-center text-gray-500">
          No achievement posts found.
        </div>
      )}

      <div className="space-y-4">
        {posts.map((post) => (
          <SocialPostCard
            key={post.id}
            post={post}
            currentUserId={currentUserId}
            isHr={isHr}
            onLike={likePost}
            onUnlike={unlikePost}
            onUpdate={updatePost}
            onDelete={deletePost}
            isBusy={busy}
          />
        ))}
      </div>
    </div>
  );
}