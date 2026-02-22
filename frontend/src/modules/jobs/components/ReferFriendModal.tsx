import { useForm } from "react-hook-form";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { referCandidate } from "../api/jobApi";
import Modal from "../../../shared/components/Modal";
 
type Props = {
  jobId: number;
  jobTitle: string;
  open: boolean;
  onClose: () => void;
};
 
type ReferForm = {
  candidateFullName: string;
  email: string;
  candidatePhoneNumber: string;
  notes: string;
  file: FileList;
};
 
export default function ReferFriendModal({ jobId, jobTitle, open, onClose }: Props) {
  const qc = useQueryClient();
 
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ReferForm>();
 
  const mutation = useMutation({
    mutationFn: (fd: FormData) => referCandidate(jobId, fd),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["referrals"] });
      reset();
      onClose();
    },
  });
 
  const onSubmit = (data: ReferForm) => {
    const fd = new FormData();
    fd.append("candidateFullName", data.candidateFullName);
    if (data.email) fd.append("email", data.email);
    fd.append("candidatePhoneNumber", data.candidatePhoneNumber);
    if (data.notes) fd.append("notes", data.notes);
    if (data.file?.[0]) fd.append("file", data.file[0]);
    mutation.mutate(fd);
  };
 
  return (
    <Modal title={`Refer for: ${jobTitle}`} open={open} onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <input
          className="border p-2 w-full rounded"
          placeholder="Friend's full name"
          {...register("candidateFullName", { required: "Name is required" })}
        />
        {errors.candidateFullName && (
          <p className="text-red-500 text-sm">{errors.candidateFullName.message}</p>
        )}
 
        <input
          type="email"
          className="border p-2 w-full rounded"
          placeholder="Friend's email (optional)"
          {...register("email", {
            pattern: {
              value: /^\S+@\S+\.\S+$/,
              message: "Enter a valid email address",
            },
          })}
        />
        {errors.email && (
          <p className="text-red-500 text-sm">{errors.email.message}</p>
        )}
 
        <input
          className="border p-2 w-full rounded"
          placeholder="Phone number"
          {...register("candidatePhoneNumber", { required: "Phone required" })}
        />
        {errors.candidatePhoneNumber && (
          <p className="text-red-500 text-sm">{errors.candidatePhoneNumber.message}</p>
        )}
 
        <label className="block">
          <span className="text-sm text-gray-600">Upload CV</span>
          <input
            type="file"
            accept=".pdf,.doc,.docx"
            className="block w-full text-sm mt-1 file:mr-3 file:py-1.5 file:px-3 file:rounded file:border-0 file:bg-gray-100 file:text-gray-700 hover:file:bg-gray-200"
            {...register("file")}
          />
        </label>
 
        <textarea
          className="border p-2 w-full rounded"
          placeholder="Short note (optional)"
          rows={2}
          {...register("notes")}
        />
 
        {mutation.isError && (
          <p className="text-red-500 text-sm">Something went wrong. Try again.</p>
        )}
 
        <button
          type="submit"
          disabled={mutation.isPending}
          className="bg-indigo-600 hover:bg-indigo-700 text-white py-2 rounded w-full disabled:opacity-50"
        >
          {mutation.isPending ? "Submitting..." : "Submit Referral"}
        </button>
      </form>
    </Modal>
  );
}