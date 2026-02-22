import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { shareJob } from "../api/jobApi";
import Modal from "../../../shared/components/Modal";
import toast from "react-hot-toast";
 
type Props = {
  jobId: number;
  jobTitle: string;
  open: boolean;
  onClose: () => void;
};
 
const EMAIL_RE = /^\S+@\S+\.\S+$/;
 
export default function ShareJobModal({
  jobId,
  jobTitle,
  open,
  onClose,
}: Props) {
  const [input, setInput] = useState("");
  const [emails, setEmails] = useState<string[]>([]);
  const [error, setError] = useState("");
 
  const mutation = useMutation({
    mutationFn: () => shareJob(jobId, emails),
    onSuccess: () => {
      toast.success(`Job shared with ${emails.length} recipient(s)`);
      setInput("");
      setEmails([]);
      setError("");
      onClose();
    },
  });
 
  const addEmail = () => {
    const trimmed = input.trim();
    if (!trimmed) return;
    if (!EMAIL_RE.test(trimmed)) {
      setError("Enter a valid email address");
      return;
    }
    if (emails.includes(trimmed)) {
      setError("Email already added");
      return;
    }
    setEmails((prev) => [...prev, trimmed]);
    setInput("");
    setError("");
  };
 
  const removeEmail = (e: string) =>
    setEmails((prev) => prev.filter((x) => x !== e));
 
  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" || e.key === ",") {
      e.preventDefault();
      addEmail();
    }
  };
 
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (input.trim()) addEmail();
    if (emails.length === 0 && !input.trim()) {
      setError("Add at least one email");
      return;
    }
    const finalEmails =
      input.trim() && EMAIL_RE.test(input.trim())
        ? [...emails, input.trim()]
        : emails;
    if (finalEmails.length === 0) return;
    setEmails(finalEmails);
    setInput("");
    mutation.mutate();
  };
 
  return (
    <Modal title={`Share: ${jobTitle}`} open={open} onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-3">
        <p className="text-sm text-gray-500">
          Add one or more emails. Press Enter or comma to add each email.
        </p>
 
        {/* Email tags */}
        {emails.length > 0 && (
          <div className="flex flex-wrap gap-1">
            {emails.map((em) => (
              <span
                key={em}
                className="bg-blue-100 text-blue-700 text-xs px-2 py-1 rounded-full flex items-center gap-1"
              >
                {em}
                <button
                  type="button"
                  onClick={() => removeEmail(em)}
                  className="text-blue-400 hover:text-blue-600 leading-none"
                >
                  ×
                </button>
              </span>
            ))}
          </div>
        )}
 
        <div className="flex gap-2">
          <input
            type="text"
            value={input}
            onChange={(e) => {
              setInput(e.target.value);
              setError("");
            }}
            onKeyDown={handleKeyDown}
            placeholder="Recipient email"
            className="border p-2 flex-1 rounded"
          />
          <button
            type="button"
            onClick={addEmail}
            className="text-sm text-blue-600 hover:text-blue-800 px-3"
          >
            Add
          </button>
        </div>
 
        {error && <p className="text-red-500 text-sm">{error}</p>}
 
        <button
          type="submit"
          disabled={
            mutation.isPending || (emails.length === 0 && !input.trim())
          }
          className="bg-blue-600 hover:bg-blue-700 text-white py-2 rounded w-full disabled:opacity-50"
        >
          {mutation.isPending
            ? "Sending..."
            : `Share Job${emails.length > 1 ? ` (${emails.length})` : ""}`}
        </button>
      </form>
    </Modal>
  );
}