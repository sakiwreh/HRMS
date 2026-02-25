import { useState } from "react";
import { useAppSelector } from "../../../../store/hooks";
import useDocuments from "../../hooks/useDocuments";
import useDeleteDocument from "../../hooks/useDeleteDocument";
import Modal from "../../../../shared/components/Modal";
import UploadDocumentForm from "../UploadDocumentForm";
import { previewDocument, downloadDocument } from "../../util/documentActions";
import toast from "react-hot-toast";
 
type Props = {
  travel: any;
};
 
export default function DocumentsTab({ travel }: Props) {
  const travelId = travel?.id;
  const isCancelled = travel?.cancelled;
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
  const { data: documents = [], isLoading } = useDocuments(travelId);
  const deleteMutation = useDeleteDocument(travelId);
  const [open, setOpen] = useState(false);
 
  if (!travelId) return null;
 
  if (isLoading)
    return <div className="text-gray-500">Loading documents...</div>;
 
  const handleDelete = (docId: number) => {
    if (!confirm("Delete this document?")) return;
    deleteMutation.mutate(docId, {
      onSuccess: () => toast.success("Document deleted"),
    });
  };
 
  // Separate common documents from employee's own documents
  const commonDocs = documents.filter((doc: any) => {
  if (isHR) {
    return doc.uploadedById === user?.id && !doc.uploadedForId;
  } else {
    return !doc.uploadedForId && doc.uploadedById !== user?.id;
  }
});
  const myDocs = documents.filter((d: any) => d.uploadedById===user?.id || d.uploadedForId === user?.id);
 
  return (
    <div className="space-y-5">
      <div className="flex justify-between items-center">
        <h2 className="text-lg font-semibold">Travel Documents</h2>
 
        {!isCancelled && (
          <button
            onClick={() => setOpen(true)}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            Upload Document
          </button>
        )}
      </div>
 
      {documents.length === 0 && (
        <div className="text-gray-400 border rounded-md p-4 text-center">
          No documents uploaded yet
        </div>
      )}
 
      {/* Common Documents */}
      {commonDocs.length > 0 && (
        <div>
          <h3 className="text-sm font-semibold text-gray-600 mb-2 uppercase tracking-wide">
            Common Documents
          </h3>
          <div className="space-y-3">
            {commonDocs.map((doc: any) => (
              <DocumentCard
                key={doc.id}
                doc={doc}
                canDelete={isHR || doc.uploadedById === user?.id}
                onDelete={() => handleDelete(doc.id)}
              />
            ))}
          </div>
        </div>
      )}
 
      {/* Own Documents */}
      {!isHR && myDocs.length > 0 && (
        <div>
          <h3 className="text-sm font-semibold text-gray-600 mb-2 uppercase tracking-wide">
            My Documents
          </h3>
          <div className="space-y-3">
            {myDocs.map((doc: any) => (
              <DocumentCard
                key={doc.id}
                doc={doc}
                canDelete={doc.uploadedById === user?.id}
                onDelete={() => handleDelete(doc.id)}
              />
            ))}
          </div>
        </div>
      )}
 
      {/* Employee Documents — HR view */}
      {isHR && documents.filter((d: any) => !!d.uploadedForId).length > 0 && (
        <div>
          <h3 className="text-sm font-semibold text-gray-600 mb-2 uppercase tracking-wide">
            Employee Documents
          </h3>
          <div className="space-y-3">
            {documents.filter((d: any) => !!d.uploadedForId).map((doc: any) => (
              <DocumentCard
                key={doc.id}
                doc={doc}
                canDelete={true}
                onDelete={() => handleDelete(doc.id)}
                showForLabel
              />
            ))}
          </div>
        </div>
      )}
 
      <Modal title="Upload Document" open={open} onClose={() => setOpen(false)}>
        <UploadDocumentForm
          travelId={travelId}
          onSuccess={() => setOpen(false)}
        />
      </Modal>
    </div>
  );
}
 
function DocumentCard({
  doc,
  canDelete,
  onDelete,
  showForLabel = false,
}: {
  doc: any;
  canDelete: boolean;
  onDelete: () => void;
  showForLabel?: boolean;
}) {
  return (
    <div className="border rounded-md p-4 flex justify-between items-center bg-white shadow-sm">
      <div className="space-y-1">
        <div className="font-medium text-gray-800">
          {doc.description || doc.fileName}
        </div>
        <div className="flex items-center gap-3 text-xs text-gray-500">
          <span>{doc.docType}</span>
          <span>{(doc.fileSize / 1024).toFixed(1)} KB</span>
          {showForLabel && doc.uploadedForName && (
            <span className="text-blue-600">for {doc.uploadedForName}</span>
          )}
        </div>
      </div>
      <div className="flex gap-4">
        <button
          type="button"
          onClick={() => previewDocument(doc.filePath)}
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
 
        {canDelete && (
          <button
            type="button"
            onClick={onDelete}
            className="text-red-600 text-sm hover:underline"
          >
            Delete
          </button>
        )}
      </div>
    </div>
  );
}