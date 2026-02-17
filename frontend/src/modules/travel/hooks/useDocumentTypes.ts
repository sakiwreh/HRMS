import { useQuery } from "@tanstack/react-query";
import { fetchDocumentTypes } from "../api/travelApi";
 
export default function useDocumentTypes() {
  return useQuery({
    queryKey: ["document-types"],
    queryFn: fetchDocumentTypes,
  });
}