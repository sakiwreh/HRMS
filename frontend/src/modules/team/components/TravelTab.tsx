import { NavLink } from "react-router-dom";

export default function TravelsTab({
  travels,
  isLoading,
}: {
  travels: any[];
  isLoading: boolean;
}) {
  
  const filteredData = travels?.filter((t:any)=> !t.cancelled);
  if (isLoading) return <div>Loading team travels...</div>;

  if (filteredData.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow p-6 text-center text-gray-400">
        No team travels found
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow divide-y">
      {filteredData.map((t: any) => {
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
              {t.destination} &bull; {t.departureDate} &rarr; {t.returnDate}
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
              `block p-4 transition ${isActive ? "bg-blue-50" : "hover:bg-gray-50"}`
            }
          >
            {inner}
          </NavLink>
        );
      })}
    </div>
  );
}