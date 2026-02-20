import api from "../../../lib/axios";
 
export const fetchJobs = async () => {
  const res = await api.get("/jobs/all");
  return res.data;
};
 
export const fetchJobById = async (id: number) => {
  const res = await api.get(`/jobs/${id}`);
  return res.data;
};
 
export const createJob = async (formData: FormData) => {
  const res = await api.post("/jobs", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};
 
export const updateJobStatus = async (
  id: number,
  data: { status: string; reason: string }
) => {
  const res = await api.patch(`/jobs/${id}/update`, data);
  return res.data;
};
 
export const fetchReviewers = async (jobId: number) => {
  const res = await api.get(`/jobs/${jobId}/reviewers`);
  return res.data;
};
 
 
export const removeReviewer = async (jobId: number, empId: number) => {
  const res = await api.delete(`/jobs/${jobId}/remove-reviewer/${empId}`);
  return res.data;
};

export const addReviewers = async (
  jobId: number,
  empIds: number[]
) => {
  const res = await api.post(`/jobs/${jobId}/add-reviewer`, { empIds });
  return res.data;
};
 
export const shareJob = async (jobId: number, candidateEmail: string) => {
  const res = await api.post(`/job/${jobId}/share`, {candidateEmail});
  return res.data;
};
 
export const referCandidate = async (jobId: number, formData: FormData) => {
  const res = await api.post(`/job/${jobId}/refer`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};
 
export const fetchMyReferrals = async () => {
  const res = await api.get("/job/referrals/me");
  return res.data;
};
 
export const fetchAllReferrals = async () => {
  const res = await api.get("/job/referrals/all");
  return res.data;
};
 
export const updateReferralStatus = async (
  id: number,
  status: string
) => {
  const res = await api.patch(`/job/refer/${id}`, JSON.stringify(status), {
    headers: { "Content-Type": "application/json" },
  });
  return res.data;
};
 
export const fetchEmployees = async () => {
  const res = await api.get("/employees/lookup");
  return res.data;
};