import { NavLink } from "react-router-dom";
import useTravels from "../hooks/useTravels";
import useMyTravels from "../hooks/useMyTravels";
import { useState } from "react";
import { useAppSelector } from "../../../store/hooks";
import CreateTravelForm from "../components/CreateTravelForm";
import Modal from "../../../shared/components/Modal";
 
export default function TravelListPage() {
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
 
  const allTravels = useTravels(isHR);
  const myTravels = useMyTravels(!isHR);
 
  const { data, isLoading } = isHR ? allTravels : myTravels;
 
  const [open, setOpen] = useState(false);
 
  if (isLoading) return <div>Loading...</div>;
 
  return (
    <div className="space-y-4">
      {/* HEADER */}
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">
          {isHR ? "Travel Plans" : "My Travels"}
        </h1>
 
        {isHR && (
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
        {data?.length === 0 && (
          <div className="p-6 text-center text-gray-400">
            No travel plans found
          </div>
        )}
 
        {data?.map((t: any) => {
          const isCancelled = t.cancelled;
 
          const inner = (
            <>
              <div className="flex items-center gap-2">
                <span className="font-medium text-gray-800">{t.title}</span>
                {isCancelled && (
                  <span className="text-xs bg-red-100 text-red-600 px-2 py-0.5 rounded-full">
                    Cancelled
                  </span>
                )}
              </div>
              <div className="text-sm text-gray-500">
                {t.destination} • {t.departureDate} → {t.returnDate}
              </div>
            </>
          );
 
          if (isCancelled) {
            return (
              <div
                key={t.id}
                className="block p-4 bg-gray-50 opacity-60 cursor-not-allowed"
              >
                {inner}
              </div>
            );
          }
 
          return (
            <NavLink
              key={t.id}
              to={`/dashboard/travel/${t.id}`}
              className={({ isActive }) =>
                `block p-4 transition ${
                  isActive ? "bg-blue-50" : "hover:bg-gray-50"
                }`
              }
            >
              {inner}
            </NavLink>
          );
        })}
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