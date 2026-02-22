import { useState } from "react";
import useJobs from "../hooks/useJobs";
import JobCard from "../components/JobCard";
import Modal from "../../../shared/components/Modal";
import JobCreateForm from "../components/JobCreationForm";
import { useAppSelector } from "../../../store/hooks";
 
const statusColor: Record<string, string> = {
  OPEN: "bg-green-100 text-green-700",
  CLOSED: "bg-red-100 text-red-700",
  HOLD: "bg-yellow-100 text-yellow-700",
};
 
export default function JobListPage() {
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
  const { data: jobs, isLoading } = useJobs(isHR);
  const [createOpen, setCreateOpen] = useState(false);
  const [filterStatus, setFilterStatus] = useState<string>("");
 
  const filtered = filterStatus
    ? jobs?.filter((j: any) => j.status === filterStatus)
    : jobs;
 
  if (isLoading) return <div>Loading jobs...</div>;
 
  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center flex-wrap gap-3">
        <h1 className="text-xl font-semibold">Job Openings</h1>
 
        <div className="flex items-center gap-3">
          {isHR && (
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="border rounded px-3 py-1.5 text-sm"
            >
              <option value="">All Statuses</option>
              {Object.keys(statusColor).map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          )}
 
          {isHR && (
            <button
              onClick={() => setCreateOpen(true)}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
            >
              + Post Job
            </button>
          )}
        </div>
      </div>
 
      {filtered?.length === 0 && (
        <div className="bg-white rounded-xl shadow p-8 text-center text-gray-400">
          No {filterStatus ? filterStatus.toLowerCase() : "open"} positions
          right now
        </div>
      )}
 
      <div className="grid gap-4 md:grid-cols-2">
        {filtered?.map((job: any) => (
          <JobCard key={job.id} job={job} role={user?.role || ""} />
        ))}
      </div>
 
      <Modal
        title="Post a New Job"
        open={createOpen}
        onClose={() => setCreateOpen(false)}
      >
        <JobCreateForm onDone={() => setCreateOpen(false)} />
      </Modal>
    </div>
  );
}