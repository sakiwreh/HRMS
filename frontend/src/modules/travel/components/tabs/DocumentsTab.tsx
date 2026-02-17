import { useState } from "react";
import useDocuments from "../../hooks/useDocuments";
import Modal from "../../../../shared/components/Modal";
import UploadDocumentForm from "../UploadDocumentForm";
import { previewDocument, downloadDocument } from "../../util/documentActions";
 
type Props = {
  travel: any;
};
 
export default function DocumentsTab({ travel }: Props) {
  const travelId = travel?.id;
  const { data: documents = [], isLoading } = useDocuments(travelId);
  const [open, setOpen] = useState(false);
 
  if (!travelId) return null;
 
  if (isLoading)
    return <div className="text-gray-500">Loading documents...</div>;
 
  return (
    <div className="space-y-5">
      <div className="flex justify-between items-center">
        <h2 className="text-lg font-semibold">Travel Documents</h2>
 
        <button
          onClick={() => setOpen(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
        >
          Upload Document
        </button>
      </div>
      <div className="space-y-3">
        {documents.length === 0 && (
          <div className="text-gray-400 border rounded-md p-4 text-center">
            No documents uploaded yet
          </div>
        )}
 
        {documents.map((doc: any) => (
          <DocumentCard key={doc.id} doc={doc} />
        ))}
      </div>
      <Modal
        title="Upload Document"
        open={open}
        onClose={() => setOpen(false)}
      >
        <UploadDocumentForm
          travelId={travelId}
          onSuccess={() => setOpen(false)}
        />
      </Modal>
    </div>
  );
}
 
function DocumentCard({ doc }: { doc: any }) {
  return (
    <div className="border rounded-md p-4 flex justify-between items-center bg-white shadow-sm">
      <div className="space-y-1">
        <div className="font-medium text-gray-800">
          {doc.description || doc.fileName}
        </div>
 
        <div className="text-xs text-gray-500">
          {doc.docType}
        </div>
 
        <div className="text-xs text-gray-400">
          {(doc.fileSize / 1024).toFixed(1)} KB
        </div>
      </div>
      <div className="flex gap-4">
 
        <button
          type="button"
          onClick={() => previewDocument(doc.id)}
          className="text-blue-600 text-sm hover:underline"
        >
          Preview
        </button>
 
        <button
          type="button"
          onClick={() => downloadDocument(doc.id, doc.fileName)}
          className="text-green-600 text-sm hover:underline"
        >
          Download
        </button>
 
      </div>
    </div>
  );
}