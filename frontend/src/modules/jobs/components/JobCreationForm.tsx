import { useForm } from "react-hook-form";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createJob } from "../api/jobApi";
import toast from "react-hot-toast";
 
type Form = {
  title: string;
  description: string;
  communicationEmail: string;
  experienceRequired: string;
  file: FileList;
};
 
export default function JobCreateForm({ onDone }: { onDone?: () => void }) {
  const qc = useQueryClient();
 
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<Form>();
 
  const mutation = useMutation({
    mutationFn: createJob,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["jobs"] });
      reset();
      toast.success("Job posted successfully");
      onDone?.();
    },
  });
 
  const onSubmit = (data: Form) => {
    const fd = new FormData();
    fd.append("title", data.title);
    fd.append("description", data.description);
    fd.append("communicationEmail", data.communicationEmail);
    fd.append("experienceRequired", data.experienceRequired);
    if (data.file?.[0]) fd.append("file", data.file[0]);
    mutation.mutate(fd);
  };
 
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
      <input
        className="border p-2 w-full rounded"
        placeholder="Job title"
        {...register("title", { required: "Title required" })}
      />
      {errors.title && <p className="text-red-500 text-sm">{errors.title.message}</p>}
 
      <textarea
        className="border p-2 w-full rounded"
        rows={3}
        placeholder="Job description"
        {...register("description", { required: "Description required" })}
      />
      {errors.description && (
        <p className="text-red-500 text-sm">{errors.description.message}</p>
      )}
 
      <input
        type="email"
        className="border p-2 w-full rounded"
        placeholder="Communication email"
        {...register("communicationEmail", { required: "Email required" })}
      />
      {errors.communicationEmail && (
        <p className="text-red-500 text-sm">{errors.communicationEmail.message}</p>
      )}
 
      <input
        type="number"
        step="0.1"
        min="0"
        className="border p-2 w-full rounded"
        placeholder="Min experience (years)"
        {...register("experienceRequired", { required: "Required" })}
      />
 
      <label className="block">
        <span className="text-sm text-gray-600">Job Description (PDF)</span>
        <input
          type="file"
          accept=".pdf,.doc,.docx"
          className="block w-full text-sm mt-1 file:mr-3 file:py-1.5 file:px-3 file:rounded file:border-0 file:bg-gray-100 file:text-gray-700 hover:file:bg-gray-200"
          {...register("file", { required: "JD file required" })}
        />
        {errors.file && <p className="text-red-500 text-sm">{errors.file.message}</p>}
      </label>
 
      {mutation.isError && (
        <p className="text-red-500 text-sm">Failed to create job. Check inputs.</p>
      )}
 
      <button
        type="submit"
        disabled={mutation.isPending}
        className="bg-blue-600 hover:bg-blue-700 text-white py-2 rounded w-full disabled:opacity-50"
      >
        {mutation.isPending ? "Creating..." : "Post Job"}
      </button>
    </form>
  );
}