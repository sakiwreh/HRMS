import api from "../../../lib/axios";
 
/* ===================== TRAVEL ===================== */
 
export const fetchTravels = async () => {
  const res = await api.get("/travel-plans");
  return res.data;
};
 
export const fetchTravelById = async (id: number) => {
  const res = await api.get(`/travel-plans/${id}`);
  return res.data;
};
 
export const createTravel = async (data: any) => {
  const res = await api.post("/travel-plans", data);
  return res.data;
};
 
/* ===================== DOCUMENT ===================== */
 
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
 
/** Get all employees */
export const fetchEmployees = async () => {
  const res = await api.get("/employees/lookup");
  return res.data;
};
 
/** Assign employees to travel */
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

export const fetchParticipants = async (travelId : number) => {
  const res = await api.get(`/travel-plans/${travelId}/participants`);
  return res.data;
}

export const fetchDocumentTypes = async () => {
  const res = await api.get("/lookups/document-types");
  return res.data;
};