import { useParams, NavLink } from "react-router-dom";
import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import useJob from "../hooks/useJob";
import useReviewers from "../hooks/useReviewers";
import { updateJobStatus, addReviewers, removeReviewer, fetchEmployees } from "../api/jobApi";
import { useQuery } from "@tanstack/react-query";
 
const STATUS_OPTIONS = ["OPEN", "CLOSED", "HOLD"];
 
export default function JobDetailPage() {
  const { id } = useParams();
  const qc = useQueryClient();
  const { data: job, isLoading } = useJob(id);
  const { data: reviewers } = useReviewers(job?.id);
 
  /* Update Status */
  const [status, setStatus] = useState("");
  const [reason, setReason] = useState("");
 
  const statusMutation = useMutation({
    mutationFn: () => updateJobStatus(Number(id), { status, reason }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["job", id] });
      qc.invalidateQueries({ queryKey: ["jobs"] });
      setReason("");
    },
  });
 
  /* Manage Reviewer */
  const [selectedEmp, setSelectedEmp] = useState("");
  const { data: employees } = useQuery({
    queryKey: ["employees"],
    queryFn: fetchEmployees,
  });
 
  const addMutation = useMutation({
    mutationFn: () => addReviewers(Number(id), [Number(selectedEmp)]),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["reviewers", Number(id)] });
      setSelectedEmp("");
    },
  });
 
  const removeMutation = useMutation({
    mutationFn: (empId: number) => removeReviewer(Number(id), empId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["reviewers", Number(id)] });
    },
  });
 
  if (isLoading) return <div>Loading...</div>;
  if (!job) return <div>Job not found</div>;
 
  return (
    <div className="space-y-6">
      <NavLink to="/dashboard/jobs" className="text-sm text-blue-600 hover:underline">
        Back to Jobs
      </NavLink>
 
      <div className="bg-white rounded-lg shadow p-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-2xl font-semibold">{job.title}</h1>
            <p className="text-gray-500 mt-1">{job.description}</p>
          </div>
          <span className="text-xs bg-green-100 text-green-700 px-2.5 py-1 rounded-full">
            {job.status}
          </span>
        </div>
        <div className="mt-3 text-sm text-gray-500 flex gap-4 flex-wrap">
          <span>{job.experienceRequired}+ yrs</span>
          <span>Contact: {job.communicationEmail}</span>
          {job.createdByName && <span>Posted by {job.createdByName}</span>}
          {job.reason && <span>Reason: {job.reason}</span>}
        </div>
      </div>
 
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="font-semibold mb-3">Update Status</h2>
        <div className="flex gap-3 items-end flex-wrap">
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="border p-2 rounded min-w-[140px]"
          >
            <option value="">Select status</option>
            {STATUS_OPTIONS.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
 
          <input
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            placeholder="Reason"
            className="border p-2 rounded flex-1"
          />
 
          <button
            onClick={() => statusMutation.mutate()}
            disabled={!status || !reason || statusMutation.isPending}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded disabled:opacity-50"
          >
            {statusMutation.isPending ? "Updating..." : "Update"}
          </button>
        </div>
      </div>
 
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="font-semibold mb-3">CV Reviewers</h2>
 
        {reviewers?.length === 0 && (
          <p className="text-gray-400 text-sm mb-3">No reviewers assigned yet</p>
        )}
 
        <div className="space-y-2 mb-4">
          {reviewers?.map((r: any) => (
            <div key={r.id} className="flex items-center justify-between bg-gray-50 rounded px-3 py-2">
              <div>
                <span className="font-medium text-gray-800">{r.name}</span>
                <span className="text-sm text-gray-500 ml-2">{r.email}</span>
              </div>
              <button
                onClick={() => removeMutation.mutate(r.id)}
                className="text-red-500 hover:text-red-700 text-sm"
              >
                Remove
              </button>
            </div>
          ))}
        </div>
 
        <div className="flex gap-3 items-end">
          <select
            value={selectedEmp}
            onChange={(e) => setSelectedEmp(e.target.value)}
            className="border p-2 rounded flex-1"
          >
            <option value="">Select employee</option>
            {employees?.map((emp: any) => (
              <option key={emp.id} value={emp.id}>
                {emp.name}
              </option> 
            ))}
          </select>
 
          <button
            onClick={() => addMutation.mutate()}
            disabled={!selectedEmp || addMutation.isPending}
            className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded disabled:opacity-50"
          >
            Add Reviewer
          </button>
        </div>
      </div>
    </div>
  );
}