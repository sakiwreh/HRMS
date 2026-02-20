import { useState } from "react";
import useJobs from "../hooks/useJobs";
import JobCard from "../components/JobCard";
import Modal from "../../../shared/components/Modal";
import JobCreateForm from "../components/JobCreationForm";
import { useAppSelector } from "../../../store/hooks";
 
export default function JobListPage() {
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
  const { data: jobs, isLoading } = useJobs();
  const [createOpen, setCreateOpen] = useState(false);
 
  if (isLoading) return <div>Loading jobs!!</div>;
 
  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">Job Openings</h1>
 
        {isHR && (
          <button
            onClick={() => setCreateOpen(true)}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
          >
            + Post Job
          </button>
        )}
      </div>
 
      {jobs?.length === 0 && (
        <div className="bg-white rounded-xl shadow p-8 text-center text-gray-400">
          No open positions right now
        </div>
      )}
 
      <div className="grid gap-4 md:grid-cols-2">
        {jobs?.map((job: any) => (
          <JobCard key={job.id} job={job} isHR={isHR} />
        ))}
      </div>
 
      <Modal title="Post a New Job" open={createOpen} onClose={() => setCreateOpen(false)}>
        <JobCreateForm onDone={() => setCreateOpen(false)} />
      </Modal>
    </div>
  );
}