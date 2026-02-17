export function groupDocumentsByEmployee(documents: any[]) {
  const grouped: Record<string, any[]> = {};
 
  documents.forEach(doc => {
    const key = doc.employeeName || "General";
    if (!grouped[key]) grouped[key] = [];
    grouped[key].push(doc);
  });
 
  return grouped;
}
 