import { useForm } from "react-hook-form";
import { useAppSelector } from "../../../store/hooks";
import useEmployees from "../hooks/useEmployees";
import useDocumentTypes from "../hooks/useDocumentTypes";
import { useUploadDocument } from "../hooks/useUploadDocument";
import toast from "react-hot-toast";
 
type Props = {
  travelId: number;
  onSuccess: () => void;
};
 
type FormData = {
  title: string;
  type: string;
  uploadedFor?: string;
  file: FileList;
};
 
export default function UploadDocumentForm({ travelId, onSuccess }: Props) {
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
  const { data: employees = [] } = useEmployees();
  const { data: types = [] } = useDocumentTypes();
  const uploadMutation = useUploadDocument(travelId);
 
  const { register, handleSubmit, reset } = useForm<FormData>();
 
  const onSubmit = async (data: FormData) => {
    const form = new FormData();
    form.append("file", data.file[0]);
    form.append("description", data.title);
    form.append("docType", data.type);
 
    if (isHR && data.uploadedFor) form.append("uploadedFor", data.uploadedFor);
 
    try {
      await uploadMutation.mutateAsync(form);
      toast.success("Document uploaded successfully");
      reset();
      onSuccess();
    } catch {
      // error toast handled by axios interceptor
    }
  };
 
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="text-sm">Title</label>
        <input
          {...register("title", { required: true })}
          className="w-full border rounded-md px-3 py-2 mt-1"
        />
      </div>
 
      <div>
        <label className="text-sm">Document Type</label>
        <select
          {...register("type", { required: true })}
          className="w-full border rounded-md px-3 py-2 mt-1"
        >
          <option value="">Select type</option>
          {types.map((t: string) => (
            <option key={t}>{t}</option>
          ))}
        </select>
      </div>
 
      {/* Only HR can specify which employee the doc is for */}
      {isHR && (
        <div>
          <label className="text-sm">Upload For</label>
          <select
            {...register("uploadedFor")}
            className="w-full border rounded-md px-3 py-2 mt-1"
          >
            <option value="">General (Entire Travel Plan)</option>
            {employees.map((e: any) => (
              <option key={e.id} value={e.id}>
                {e.name}
              </option>
            ))}
          </select>
        </div>
      )}
 
      <div>
        <input type="file" {...register("file", { required: true })} />
      </div>
 
      <div className="flex justify-end">
        <button
          type="submit"
          disabled={uploadMutation.isPending}
          className="bg-blue-600 text-white px-5 py-2 rounded-md"
        >
          {uploadMutation.isPending ? "Uploading..." : "Upload"}
        </button>
      </div>
    </form>
  );
}