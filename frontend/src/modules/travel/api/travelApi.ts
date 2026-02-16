import api from "../../../lib/axios";
 
/* ================= TRAVEL ================= */
 
export const fetchTravels = async () => {
  const res = await api.get("/travel-plans");
  return res.data;
};
 
export const fetchTravelById = async (id: string) => {
  const res = await api.get(`/travel-plans/${id}`);
  return res.data;
};
 
export const createTravel = async (data: any) => {
  const res = await api.post("/travel-plans", data);
  return res.data;
};
 
export const assignEmployees = async (id: string, empIds: number[]) => {
  const res = await api.post(`/travel-plans/${id}/participants`, empIds);
  return res.data;
};
 
/* ================= DOCUMENT ================= */
 
export const uploadDocument = async (formData: FormData) => {
  const res = await api.post("/travel-documents/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};
 
export const fetchDocuments = async (travelId: string) => {
  const res = await api.get(`/travel-documents/${travelId}`);
  return res.data;
};
 