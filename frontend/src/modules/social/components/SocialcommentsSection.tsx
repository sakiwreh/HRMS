import { useState } from "react";
import toast from "react-hot-toast";
import type { SocialCommentResponse } from "../api/socialApi";
import useAddSocialComment from "../hooks/useAddSocialComment";
import useDeleteSocialComment from "../hooks/useDeleteSocialComment";
import useSocialComments from "../hooks/useSocialComments";
import useUpdateSocialComment from "../hooks/useUpdateSocialComment";

type Props = {
  postId: number;
  currentUserId: number;
  isHr: boolean;
};

export default function SocialCommentSection({
  postId,
  currentUserId,
  isHr,
}: Props) {
  const { data, isLoading } = useSocialComments(postId);
  const addCommentMutation = useAddSocialComment();
  const updateCommentMutation = useUpdateSocialComment();
  const deleteCommentMutation = useDeleteSocialComment();

  const [newComment, setNewComment] = useState("");
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editingText, setEditingText] = useState("");

  const comments = data?.content ?? [];

  const handleAddComment = async () => {
    const text = newComment.trim();
    if (!text) return;

    await addCommentMutation.mutateAsync({
      postId,
      payload: { text },
    });
    setNewComment("");
    toast.success("Comment added");
  };

  const startEditing = (comment: SocialCommentResponse) => {
    setEditingCommentId(comment.id);
    setEditingText(comment.text);
  };

  const cancelEditing = () => {
    setEditingCommentId(null);
    setEditingText("");
  };

  const saveEdit = async (commentId: number) => {
    const text = editingText.trim();
    if (!text) return;

    await updateCommentMutation.mutateAsync({
      commentId,
      postId,
      payload: { text },
    });
    cancelEditing();
    toast.success("Comment updated");
  };

  const deleteComment = async (comment: SocialCommentResponse) => {
    const isOwner = comment.author.id === currentUserId;
    let reason: string | undefined;

    if (isHr && !isOwner) {
      const input = window.prompt("Reason for HR deletion (optional):");
      reason = input === null ? undefined : input;
    }

    await deleteCommentMutation.mutateAsync({
      commentId: comment.id,
      postId,
      reason,
    });
    toast.success("Comment deleted");
  };

  return (
    <div className="mt-3 border-t pt-3 space-y-3">
      <div className="flex gap-2">
        <input
          type="text"
          value={newComment}
          onChange={(event) => setNewComment(event.target.value)}
          placeholder="Write a comment..."
          className="flex-1 border rounded px-3 py-2 text-sm"
        />
        <button
          type="button"
          onClick={handleAddComment}
          disabled={addCommentMutation.isPending || newComment.trim().length === 0}
          className="bg-blue-600 hover:bg-blue-700 text-white text-sm px-3 py-2 rounded disabled:opacity-50"
        >
          Comment
        </button>
      </div>

      {isLoading && <p className="text-sm text-gray-500">Loading comments...</p>}

      {!isLoading && comments.length === 0 && (
        <p className="text-sm text-gray-500">No comments yet.</p>
      )}

      <div className="space-y-2">
        {comments.map((comment) => {
          const isOwner = comment.author.id === currentUserId;
          const canModify = isOwner || isHr;
          const isEditing = editingCommentId === comment.id;

          return (
            <div key={comment.id} className="bg-gray-50 rounded p-3">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-800">
                    {comment.author.name}
                  </p>
                  <p className="text-xs text-gray-500">
                    {new Date(comment.createdAt).toLocaleString()}
                  </p>
                </div>

                {canModify && (
                  <div className="flex gap-2">
                    <button
                      type="button"
                      onClick={() => startEditing(comment)}
                      className="text-xs text-blue-600 hover:underline"
                    >
                      Edit
                    </button>
                    <button
                      type="button"
                      onClick={() => deleteComment(comment)}
                      disabled={deleteCommentMutation.isPending}
                      className="text-xs text-red-600 hover:underline disabled:opacity-50"
                    >
                      Delete
                    </button>
                  </div>
                )}
              </div>

              {isEditing ? (
                <div className="mt-2 flex gap-2">
                  <input
                    type="text"
                    value={editingText}
                    onChange={(event) => setEditingText(event.target.value)}
                    className="flex-1 border rounded px-2 py-1 text-sm"
                  />
                  <button
                    type="button"
                    onClick={() => saveEdit(comment.id)}
                    disabled={updateCommentMutation.isPending || editingText.trim().length === 0}
                    className="text-xs px-2 py-1 rounded bg-green-600 text-white disabled:opacity-50"
                  >
                    Save
                  </button>
                  <button
                    type="button"
                    onClick={cancelEditing}
                    className="text-xs px-2 py-1 rounded bg-gray-200 text-gray-700"
                  >
                    Cancel
                  </button>
                </div>
              ) : (
                <p className="mt-2 text-sm text-gray-700 whitespace-pre-wrap">{comment.text}</p>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}