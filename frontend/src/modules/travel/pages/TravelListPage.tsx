import { Link } from "react-router-dom";
import useTravels from "../hooks/useTravels";
import { useAppSelector } from "../../../store/hooks";
import CreateTravelForm from "../components/CreateTravelForm";
 
export default function TravelListPage() {
  const { data, isLoading } = useTravels();
  const user = useAppSelector(s => s.auth.user);
 
  if (isLoading) return <div>Loading...</div>;
 
  return (
    <div>
      <div className="flex justify-between mb-4">
        <h2 className="text-xl font-semibold">Travel Plans</h2>
 
        {user?.role === "HR" && <CreateTravelForm />}
      </div>
 
      <div className="bg-white rounded shadow divide-y">
        {data?.map((t: any) => (
          <Link
            key={t.id}
            to={`/dashboard/travel/${t.id}`}
            className="block p-4 hover:bg-gray-50"
          >
            <div className="font-medium">{t.title}</div>
            <div className="text-sm text-gray-500">
              {t.destination} | {t.departureDate} → {t.returnDate}
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
 