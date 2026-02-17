import { NavLink } from "react-router-dom";
import useTravels from "../hooks/useTravels";
import { useState } from "react";
import { useAppSelector } from "../../../store/hooks";
import CreateTravelForm from "../components/CreateTravelForm";
import Modal from "../../../shared/components/Modal";
 
export default function TravelListPage() {
  const { data, isLoading } = useTravels();
  const user = useAppSelector((s) => s.auth.user);
 
  const [open, setOpen] = useState(false);
 
  if (isLoading) return <div>Loading...</div>;
 
  return (
    <div className="space-y-4">
 
      {/* HEADER */}
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">Travel Plans</h1>
 
        {user?.role === "HR" && (
          <button
            onClick={() => setOpen(true)}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
          >
            + Create Travel
          </button>
        )}
      </div>
 
      {/* LIST */}
      <div className="bg-white rounded-xl shadow divide-y">
        {data?.map((t: any) => (
          <NavLink
            key={t.id}
            to={`/dashboard/travel/${t.id}`}
            className={({ isActive }) =>
              `block p-4 transition ${
                isActive ? "bg-blue-50" : "hover:bg-gray-50"
              }`
            }
          >
            <div className="font-medium text-gray-800">{t.title}</div>
            <div className="text-sm text-gray-500">
              {t.destination} • {t.departureDate} → {t.returnDate}
            </div>
          </NavLink>
        ))}
      </div>
 
      {/* MODAL */}
      <Modal
        title="Create Travel Plan"
        open={open}
        onClose={() => setOpen(false)}
      >
        <CreateTravelForm />
      </Modal>
    </div>
  );
}
 