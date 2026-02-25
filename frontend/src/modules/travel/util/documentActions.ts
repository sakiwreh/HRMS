import api from "../../../lib/axios";
export const previewDocument = (docId: string) => {
  const url = `http://localhost:8080/${docId}`;
  window.open(url, "_blank");
};
 
export const downloadDocument = async (docId: number, fileName?: string) => {
  const response = await api.get(
    `/travel-plan/documents/${docId}/download`,
    { responseType: "blob" }
  );
 
  const blob = new Blob([response.data]);
  const link = document.createElement("a");
 
  link.href = window.URL.createObjectURL(blob);
  link.download = fileName || `document-${docId}`;
 
  document.body.appendChild(link);
  link.click();
  link.remove();
};