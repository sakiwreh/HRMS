import { useQuery } from "@tanstack/react-query";
import { fetchDocuments } from "../api/travelApi";
import useUploadDocument from "../hooks/useUploadDocument";
import { useAppSelector } from "../../../store/hooks";
import { useState } from "react";
 
export default function DocumentsTab({ travelId }: { travelId: string }) {
 
  const user = useAppSelector(s => s.auth.user);
 
  const { data } = useQuery({
    queryKey: ["documents", travelId],
    queryFn: () => fetchDocuments(travelId),
  });
 
  const upload = useUploadDocument(travelId);
 
  const [empId, setEmpId] = useState("");
  const [type, setType] = useState("GENERAL");
 
  const handleUpload = (e: any) => {
    const file = e.target.files[0];
    if (!file) return;
 
    const fd = new FormData();
    fd.append("file", file);
    fd.append("travelId", travelId);
    fd.append("documentType", type);
 
    // only HR can assign to employee
    if (user?.role === "HR" && empId) {
      fd.append("employeeId", empId);
    }
 
    upload.mutate(fd);
  };
 
  return (
    <div>
      <h3 className="font-semibold mb-3">Documents</h3>
 
      {/* Upload section */}
      <div className="bg-gray-50 p-3 rounded mb-4 flex gap-2 items-center">
 
        {/* Document Type */}
        <select
          value={type}
          onChange={e => setType(e.target.value)}
          className="border p-1"
        >
          <option value="GENERAL">General</option>
          <option value="TICKET">Ticket</option>
          <option value="HOTEL">Hotel</option>
          <option value="VISA">Visa</option>
        </select>
 
        {/* HR only employee selector */}
        {user?.role === "HR" && (
          <input
            placeholder="Employee Id (optional)"
            value={empId}
            onChange={e => setEmpId(e.target.value)}
            className="border p-1 w-40"
          />
        )}
 
        <input type="file" onChange={handleUpload} />
 
      </div>
 
      {/* Document list */}
      <div className="bg-white rounded shadow divide-y">
        {data?.map((d: any) => (
          <div key={d.id} className="p-3">
            <div className="font-medium">{d.fileName}</div>
 
            <div className="text-xs text-gray-500">
              {d.documentType}
              {d.employeeId && ` • Employee #${d.employeeId}`}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}