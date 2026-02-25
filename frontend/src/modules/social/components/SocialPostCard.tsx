import { useState } from "react";
import toast from "react-hot-toast";
import type {
  SocialPostResponse,
  SocialPostUpdateRequest,
  SocialVisibility,
} from "../api/socialApi";
import SocialCommentSection from "./SocialcommentsSection";
import { SlDislike, SlLike } from "react-icons/sl";

type Props = {
  post: SocialPostResponse;
  currentUserId: number;
  isHr: boolean;
  onLike: (postId: number) => Promise<void>;
  onUnlike: (postId: number) => Promise<void>;
  onUpdate: (postId: number, payload: SocialPostUpdateRequest) => Promise<void>;
  onDelete: (postId: number, reason?: string) => Promise<void>;
  isBusy?: boolean;
};

function getInitials(name: string | null | undefined): string {
  if (!name || !name.trim()) return "?";
  return name
    .split(" ")
    .map((w) => w[0])
    .filter(Boolean)
    .slice(0, 2)
    .join("")
    .toUpperCase();
  }

export default function SocialPostCard({
  post,
  currentUserId,
  isHr,
  onLike,
  onUnlike,
  onUpdate,
  onDelete,
  isBusy = false,
}: Props) {
  const [isEditing, setIsEditing] = useState(false);
  const [showComments, setShowComments] = useState(false);

  const [title, setTitle] = useState(post.title);
  const [description, setDescription] = useState(post.description);
  const [tagsInput, setTagsInput] = useState(post.tags.join(", "));
  const [visibility, setVisibility] = useState<SocialVisibility>(post.visibility);

  const isOwner = post.author.id === currentUserId;
  const canEdit = isOwner && !post.systemGenerated;
  const canDelete = isOwner || isHr;

  const parseTags = (value: string): string[] => {
    const unique = new Map<string, string>();
    value
      .split(",")
      .map((part) => part.trim())
      .filter((part) => part.length > 0)
      .forEach((part) => {
        unique.set(part.toLowerCase(), part);
      });
    return Array.from(unique.values());
  };

  const saveEdit = async () => {
    const trimmedTitle = title.trim();
    const trimmedDescription = description.trim();
    if (!trimmedTitle || !trimmedDescription) return;

    await onUpdate(post.id, {
      title: trimmedTitle,
      description: trimmedDescription,
      visibility,
      tags: parseTags(tagsInput),
    });
    setIsEditing(false);
    toast.success("Post updated");
  };

  const deletePost = async () => {
    let reason: string | undefined;
    if (isHr && !isOwner) {
      const input = window.prompt("Reason for HR deletion (optional):");
      reason = input === null ? undefined : input;
    }
    await onDelete(post.id, reason);
    toast.success("Post deleted");
  };

  const likePost = async () => {
    await onLike(post.id);
  };

  const unlikePost = async () => {
    await onUnlike(post.id);
  };

  return (
    <article className="bg-white rounded-xl shadow p-4">
      <div className="flex gap-3">
        <div
          className="w-9 h-9 rounded-full flex items-center justify-center text-sm font-semibold shrink-0 bg-gray-200 text-gray-600">
          {getInitials(post.author.name)}
        </div>
        <div>
          <p className="text-sm font-semibold text-gray-800">{post.author.name}</p>
          <p className="text-xs text-gray-500">{new Date(post.createdAt).toLocaleString()}</p>
        </div>

        {post.systemGenerated && (
          <span className="text-xs px-2 py-1 rounded-full bg-purple-100 text-purple-700 h-fit">
            System Post
          </span>
        )}
      </div>

      {isEditing ? (
        <div className="mt-3 space-y-2">
          <input
            type="text"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            className="w-full border rounded px-3 py-2 text-sm"
          />
          <textarea
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            className="w-full border rounded px-3 py-2 text-sm"
            rows={3}
          />
          <div className="grid gap-2 md:grid-cols-2">
            <input
              type="text"
              value={tagsInput}
              onChange={(event) => setTagsInput(event.target.value)}
              className="w-full border rounded px-3 py-2 text-sm"
              placeholder="Tags separated by comma"
            />
            <select
              value={visibility}
              onChange={(event) => setVisibility(event.target.value as SocialVisibility)}
              className="w-full border rounded px-3 py-2 text-sm"
            >
              <option value="ALL">All Employees</option>
              <option value="DEPARTMENT">Department</option>
              <option value="MANAGER_ONLY">Manager Only</option>
            </select>
          </div>
          <div className="flex gap-2">
            <button
              type="button"
              onClick={saveEdit}
              disabled={isBusy || title.trim().length === 0 || description.trim().length === 0}
              className="text-xs px-3 py-1.5 rounded bg-green-600 text-white disabled:opacity-50"
            >
              Save
            </button>
            <button
              type="button"
              onClick={() => setIsEditing(false)}
              className="text-xs px-3 py-1.5 rounded bg-gray-200 text-gray-700"
            >
              Cancel
            </button>
          </div>
        </div>
      ) : (
        <div className="mt-3 space-y-2">
          <h3 className="text-lg font-semibold text-gray-900">{post.title}</h3>
          <p className="text-sm text-gray-700 whitespace-pre-wrap">{post.description}</p>
          {post.tags.length > 0 && (
            <div className="flex flex-wrap gap-2">
              {post.tags.map((tag) => (
                <span
                  key={`${post.id}-${tag}`}
                  className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full"
                >
                  #{tag}
                </span>
              ))}
            </div>
          )}
        </div>
      )}

      <div className="mt-3 flex flex-wrap items-center gap-2 text-sm">
        <span className="text-gray-600">Likes: {post.likeCount}</span>
        <span className="text-gray-600">Comments: {post.commentCount}</span>
        <button
          type="button"
          onClick={likePost}
          disabled={isBusy}
          className="text-blue-600 hover:underline disabled:opacity-50"
        >
          <SlLike/>
        </button>
        <button
          type="button"
          onClick={unlikePost}
          disabled={isBusy}
          className="text-blue-600 hover:underline disabled:opacity-50"
        >
          <SlDislike/>
        </button>
        <button
          type="button"
          onClick={() => setShowComments((prev) => !prev)}
          className="text-blue-600 hover:underline"
        >
          {showComments ? "Hide Comments" : "Show Comments"}
        </button>
        {canEdit && (
          <button
            type="button"
            onClick={() => setIsEditing(true)}
            className="text-amber-600 hover:underline"
          >
            Edit
          </button>
        )}
        {canDelete && (
          <button
            type="button"
            onClick={deletePost}
            disabled={isBusy}
            className="text-red-600 hover:underline disabled:opacity-50"
          >
            Delete
          </button>
        )}
      </div>

      {showComments && (
        <SocialCommentSection
          postId={post.id}
          currentUserId={currentUserId}
          isHr={isHr}
        />
      )}
    </article>
  );
}