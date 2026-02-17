import { useMemo, useState } from "react";
import useDocuments from "../../hooks/useDocuments";
import UploadDocumentModal from "../UploadDocumentForm";
 
type Props = {
  travel: any;
};
 
export default function DocumentsTab({ travel }: Props) {
  const travelId = travel?.id;
 
  const { data: documents = [], isLoading } = useDocuments(travelId);
 
  const [open, setOpen] = useState(false);
 
  /* ---------------- GROUP DOCUMENTS ---------------- */
 
  const { hrDocs, employeeDocs } = useMemo(() => {
    const hr: any[] = [];
    const emp: Record<number, any[]> = {};
 
    documents.forEach((doc: any) => {
      if (!doc.uploadedFor) {
        hr.push(doc);
      } else {
        const id = doc.uploadedFor.id;
        if (!emp[id]) emp[id] = [];
        emp[id].push(doc);
      }
    });
 
    return { hrDocs: hr, employeeDocs: emp };
  }, [documents]);
 
  if (!travelId) return null;
  if (isLoading) return <div>Loading documents...</div>;
 
  /* ---------------- UI ---------------- */
 
  return (
    <div className="space-y-6">
 
      {/* Upload Button */}
      <div className="flex justify-end">
        <button
          onClick={() => setOpen(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-md"
        >
          Upload Document
        </button>
      </div>
 
      {/* HR DOCUMENTS */}
      <div>
        <h3 className="text-lg font-semibold mb-3">General Documents</h3>
 
        <div className="grid gap-3">
          {hrDocs.length === 0 && (
            <div className="text-gray-400">No general documents</div>
          )}
 
          {hrDocs.map((doc: any) => (
            <DocumentCard key={doc.id} doc={doc} />
          ))}
        </div>
      </div>
 
      {/* EMPLOYEE DOCUMENTS */}
      <div>
        <h3 className="text-lg font-semibold mb-3">Employee Documents</h3>
 
        {Object.keys(employeeDocs).length === 0 && (
          <div className="text-gray-400">No employee documents</div>
        )}
 
        {Object.entries(employeeDocs).map(([empId, docs]) => (
          <div key={empId} className="mb-6 border rounded-lg p-4">
            <h4 className="font-medium mb-2">
              {(docs as any[])[0]?.uploadedFor?.name ?? "Employee"}
            </h4>
 
            <div className="grid gap-2">
              {(docs as any[]).map(doc => (
                <DocumentCard key={doc.id} doc={doc} />
              ))}
            </div>
          </div>
        ))}
      </div>
 
      {/* MODAL */}
      {open && (
        <UploadDocumentModal
          travelId={travelId}
          onClose={() => setOpen(false)}
        />
      )}
    </div>
  );
}
 
/* ---------------- CARD ---------------- */
 
function DocumentCard({ doc }: { doc: any }) {
  const fileUrl = doc.fileUrl || doc.url || doc.path;
 
  return (
    <div className="border rounded-md p-3 flex justify-between items-center">
      <div>
        <div className="font-medium">{doc.title}</div>
        <div className="text-xs text-gray-500">{doc.documentType}</div>
      </div>
 
      <div className="flex gap-3">
        <a
          href={fileUrl}
          target="_blank"
          className="text-blue-600 text-sm"
        >
          Preview
        </a>
 
        <a
          href={fileUrl}
          download
          className="text-green-600 text-sm"
        >
          Download
        </a>
      </div>
    </div>
  );
}
 