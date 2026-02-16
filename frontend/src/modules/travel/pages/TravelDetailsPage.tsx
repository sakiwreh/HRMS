import { useParams } from "react-router-dom";
import useTravel from "../hooks/useTravel";
import { useAppSelector } from "../../../store/hooks";
import AssignEmployees from "../components/AssignEmployees";
import DocumentsTab from "../components/DocumentsTab";
 
export default function TravelDetailsPage() {
  const { id = "" } = useParams();
  const { data, isLoading } = useTravel(id);
  const user = useAppSelector(s => s.auth.user);
 
  if (isLoading) return <div>Loading...</div>;
 
  return (
    <div>
      <h2 className="text-xl font-semibold">{data.title}</h2>
      <p className="text-gray-600 mb-4">{data.destination}</p>
 
      {user?.role === "HR" && <AssignEmployees travelId={id} />}
 
      <DocumentsTab travelId={id} />
    </div>
  );
}
 