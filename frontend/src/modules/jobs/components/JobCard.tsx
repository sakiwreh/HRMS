import { useState } from "react";
import { NavLink } from "react-router-dom";
import ShareJobModal from "./ShareJobModal";
import ReferFriendModal from "./ReferFriendModal";
 
type Job = {
  id: number;
  title: string;
  description: string;
  experienceRequired: number;
  status: string;
  communicationEmail: string;
  createdByName?: string;
};
 
export default function JobCard({ job, isHR }: { job: Job; isHR: boolean }) {
  const [shareOpen, setShareOpen] = useState(false);
  const [referOpen, setReferOpen] = useState(false);
 
  return (
    <>
      <div className="bg-white rounded-lg shadow p-5 flex flex-col gap-3">
        <div className="flex items-start justify-between">
          <div className="flex-1 min-w-0">
            <h3 className="font-semibold text-gray-900 truncate">{job.title}</h3>
            <p className="text-sm text-gray-500 mt-1 line-clamp-2">
              {job.description}
            </p>
          </div>
 
          <span className="ml-3 shrink-0 text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded-full">
            {job.status}
          </span>
        </div>
 
        <div className="flex items-center gap-4 text-sm text-gray-500">
          <span>{job.experienceRequired}+ yrs experience</span>
          {job.createdByName && <span>• Posted by {job.createdByName}</span>}
        </div>
 
        <div className="flex items-center gap-2 pt-1 border-t">
          <button
            onClick={() => setShareOpen(true)}
            className="text-sm text-blue-600 hover:text-blue-800 px-3 py-1.5 rounded hover:bg-blue-50"
          >
            Share Job
          </button>
 
          <button
            onClick={() => setReferOpen(true)}
            className="text-sm text-indigo-600 hover:text-indigo-800 px-3 py-1.5 rounded hover:bg-indigo-50"
          >
            Refer Friend
          </button>
 
          {isHR && (
            <NavLink
              to={`/dashboard/jobs/${job.id}`}
              className="ml-auto text-sm text-gray-500 hover:text-gray-800 px-3 py-1.5 rounded hover:bg-gray-100"
            >
              Manage
            </NavLink>
          )}
        </div>
      </div>
 
      <ShareJobModal
        jobId={job.id}
        jobTitle={job.title}
        open={shareOpen}
        onClose={() => setShareOpen(false)}
      />
 
      <ReferFriendModal
        jobId={job.id}
        jobTitle={job.title}
        open={referOpen}
        onClose={() => setReferOpen(false)}
      />
    </>
  );
}