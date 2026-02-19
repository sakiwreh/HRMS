import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { shareJob } from "../api/jobApi";
import Modal from "../../../shared/components/Modal";
 
type Props = {
  jobId: number;
  jobTitle: string;
  open: boolean;
  onClose: () => void;
};
 
export default function ShareJobModal({ jobId, jobTitle, open, onClose }: Props) {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
 
  const mutation = useMutation({
    mutationFn: () => shareJob(jobId, email),
    onSuccess: () => {
      setEmail("");
      setError("");
      onClose();
    },
  });
 
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !/\S+@\S+\.\S+/.test(email)) {
      setError("Enter a valid email address");
      return;
    }
    setError("");
    mutation.mutate();
  };
 
  return (
    <Modal title={`Share: ${jobTitle}`} open={open} onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-3">
        <p className="text-sm text-gray-500">
          The recipient will get an email with the job description attached.
        </p>
 
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Recipient email"
          className="border p-2 w-full rounded"
        />
        {error && <p className="text-red-500 text-sm">{error}</p>}
        {mutation.isError && (
          <p className="text-red-500 text-sm">Failed to share. Try again.</p>
        )}
 
        <button
          type="submit"
          disabled={mutation.isPending}
          className="bg-blue-600 hover:bg-blue-700 text-white py-2 rounded w-full disabled:opacity-50"
        >
          {mutation.isPending ? "Sending..." : "Share Job"}
        </button>
      </form>
    </Modal>
  );
}