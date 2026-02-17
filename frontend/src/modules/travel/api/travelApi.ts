import api from "../../../lib/axios";
 
/* ===================== TRAVEL ===================== */
 
export const fetchTravels = async () => {
  const res = await api.get("/travel-plans");
  return res.data;
};
 
export const fetchMyTravels = async () => {
  const res = await api.get("/travel-plans/me");
  return res.data;
};
 
export const fetchTravelById = async (id: number) => {
  const res = await api.get(`/travel-plans/${id}`);
  return res.data;
};
 
export const createTravel = async (data: {
  title: string;
  description: string;
  destination: string;
  departureDate: string;
  returnDate: string;
  maxPerDayAmount?: number;
}) => {
  const res = await api.post("/travel-plans", data);
  return res.data;
};
 
export const cancelTravel = async (id: number) => {
  const res = await api.patch(`/travel-plans/${id}/cancel`);
  return res.data;
};
 
/* ===================== PARTICIPANTS ===================== */
 
export const fetchEmployees = async () => {
  const res = await api.get("/employees/lookup");
  return res.data;
};
 
export const assignParticipants = async (
  travelId: number,
  data: { employeeIds: number[] }
) => {
  const res = await api.post(
    `/travel-plans/${travelId}/add-participants`,
    data
  );
  return res.data;
};
 
export const removeParticipant = async (travelId: number, empId: number) => {
  const res = await api.post(
    `/travel-plans/${travelId}/remove-participants/${empId}`
  );
  return res.data;
};
 
export const fetchParticipants = async (travelId: number) => {
  const res = await api.get(`/travel-plans/${travelId}/participants`);
  return res.data;
};
 
/* ===================== DOCUMENTS ===================== */
 
export const uploadDocument = async (travelId: number, formData: FormData) => {
  const res = await api.post(`/travel-plans/${travelId}/documents`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};
 
export const fetchDocuments = async (travelId: number) => {
  const res = await api.get(`/travel-plans/${travelId}/documents`);
  return res.data;
};
 
export const deleteDocument = async (docId: number) => {
  const res = await api.delete(`/travel-plan/${docId}/documents/delete`);
  return res.data;
};
 
export const fetchDocumentTypes = async () => {
  const res = await api.get("/lookups/document-types");
  return res.data;
}