import { NavLink, useParams } from "react-router-dom";
import useTravel from "../hooks/useTravel";
import TravelTabs from "../components/tabs/TravelTabs";
 
export default function TravelDetailsPage() {
  const { id } = useParams();
  const { data, isLoading } = useTravel(id);
 
  if (isLoading) return <div>Loading travel...</div>;
  if (!data) return <div>Travel not found</div>;
 
  return (
    <>
    <NavLink to="/dashboard/travel" className="text-sm text-blue-600 hover:underline">
        Back to Travel
      </NavLink>
    <div className="space-y-6">
      <div className="bg-white p-6 rounded shadow">
        <h1 className="text-2xl font-semibold">{data.title}</h1>
        <p className="text-gray-500">
          {data.destination} | {data.departureDate} -- {data.returnDate}
        </p>
      </div>
      <TravelTabs travel={data} />
    </div>
    </>
  );
}