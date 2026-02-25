import { useParams, NavLink } from "react-router-dom";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  useExpenseProofs,
  useUploadProof,
  useDeleteProof,
} from "../hooks/useExpenseProofs";
import {
  downloadExpenseProof,
  fetchExpenseById
} from "../api/expenseApi";
import toast from "react-hot-toast";
import { useAppSelector } from "../../../store/hooks";
 
const statusBadge: Record<string, string> = {
  DRAFT: "bg-gray-100 text-gray-600",
  PENDING: "bg-yellow-100 text-yellow-700",
  APPROVED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};
 
export default function ExpenseDetailPage() {
  const { id } = useParams();
  const expenseId = Number(id);
 
  // Fetch expense from my-expenses cache or refetch
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
  const backLink = isHR ? "/dashboard/expenses/review" : "/dashboard/expenses";
  const backLabel = isHR ? "← Back to Review" : "← Back to Expenses";
 
  // Fetch single expense by id
  const { data: expense, isLoading: expLoading } = useQuery({
    queryKey: ["expense", expenseId],
    queryFn: () => fetchExpenseById(expenseId),
    enabled: !!expenseId,
  });
 
  const { data: proofs = [], isLoading: proofsLoading } =
    useExpenseProofs(expenseId);
  const uploadMutation = useUploadProof(expenseId);
  const deleteMutation = useDeleteProof(expenseId);
 
  const [desc, setDesc] = useState("");
  const [file, setFile] = useState<File | null>(null);
 
  const handleUpload = () => {
    if (!file) {
      toast.error("Please select a file");
      return;
    }
    const fd = new FormData();
    fd.append("file", file);
    fd.append("description", desc || file.name);
    uploadMutation.mutate(fd, {
      onSuccess: () => {
        setFile(null);
        setDesc("");
      },
    });
  };
 
  const handleDelete = (proofId: number) => {
    if (!confirm("Delete this proof?")) return;
    deleteMutation.mutate(proofId);
  };

  if(expLoading)
    return <p className="text-gray-500">Loading expense...</p>
 
  if (!expense)
    return (
      <div className="text-gray-500">
        <NavLink
          to={backLink}
          className="text-blue-600 hover:underline"
        >
          {backLabel}
        </NavLink>
        <p className="mt-4">Expense not found</p>
      </div>
    );
 
  return (
    <div className="space-y-6">
      <NavLink
        to={backLink}
        className="text-sm text-blue-600 hover:underline"
      >
        {backLabel}
      </NavLink>
 
      {/* Expense Info */}
      <div className="bg-white rounded-lg shadow p-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-xl font-semibold">
              {expense.category} — ₹{expense.amount}
            </h1>
            <p className="text-gray-500 mt-1">
              {expense.description || "No description"}
            </p>
          </div>
          <span
            className={`text-xs px-2.5 py-1 rounded-full ${statusBadge[expense.status]}`}
          >
            {expense.status}
          </span>
        </div>
 
        <div className="mt-4 grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
          <div>
            <p className="text-gray-500">Travel Plan</p>
            <p className="font-medium">{expense.travelTitle || "-"}</p>
          </div>
          <div>
            <p className="text-gray-500">Expense Date</p>
            <p className="font-medium">
              {new Date(expense.expenseDate).toLocaleDateString()}
            </p>
          </div>
          <div>
            <p className="text-gray-500">Reviewed By</p>
            <p className="font-medium">{expense.reviewedBy || "Pending to review"}</p>
          </div>
          {expense.remarks && (
            <div className="col-span-2">
              <p className="text-gray-500">Remarks</p>
              <p className="font-medium">{expense.remarks}</p>
            </div>
          )}
        </div>
      </div>
 
      {/* Proofs Section */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold mb-4">Proofs ({proofs.length})</h2>
 
        {proofsLoading ? (
          <p className="text-gray-400 text-sm">Loading proofs...</p>
        ) : proofs.length === 0 ? (
          <p className="text-gray-400 text-sm mb-4">No proofs uploaded yet</p>
        ) : (
          <div className="space-y-2 mb-4">
            {proofs.map((p: any) => (
              <div
                key={p.id}
                className="flex items-center justify-between bg-gray-50 rounded px-4 py-2"
              >
                <div>
                  <span className="font-medium text-gray-800 text-sm">
                    {p.description || p.fileName}
                  </span>
                  <span className="text-xs text-gray-400 ml-2">
                    {(p.fileSize / 1024).toFixed(1)} KB
                  </span>
                </div>
                <div className="flex gap-3">
                  <button
                    onClick={() => downloadExpenseProof(p.id, p.fileName)}
                    className="text-green-600 hover:underline text-sm"
                  >
                    Download
                  </button>
                  <button
                    onClick={() => handleDelete(p.id)}
                    className="text-red-500 hover:underline text-sm"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
 
        {/* Upload form */}
        {!isHR && 
        <div className="border-t pt-4 mt-4">
          <h3 className="text-sm font-medium text-gray-700 mb-2">
            Upload New Proof
          </h3>
          <div className="flex gap-3 items-end flex-wrap">
            <input
              type="text"
              placeholder="Description (optional)"
              value={desc}
              onChange={(e) => setDesc(e.target.value)}
              className="border rounded px-3 py-2 text-sm flex-1 min-w-[200px]"
            />
            <input
              type="file"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
              className="text-sm"
            />
            <button
              onClick={handleUpload}
              disabled={uploadMutation.isPending || !file}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded text-sm disabled:opacity-50"
            >
              {uploadMutation.isPending ? "Uploading..." : "Upload"}
            </button>
          </div>
        </div>
}
      </div>
    </div>
  );
}