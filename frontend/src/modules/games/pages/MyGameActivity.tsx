import {
  useMyBookings,
  useMyRequests,
  useCancelBooking,
  useCancelRequest,
} from "../hooks/useGames";
import type { BookingDto, WaitlistDto } from "../api/gameApi";
 
function formatTime(dt: string) {
  return dt?.split("T")[1]?.slice(0, 5) ?? "";
}
 
function formatDate(dt: string) {
  return dt?.split("T")[0] ?? "";
}
 
const statusBadge: Record<string, string> = {
  ACTIVE: "bg-green-100 text-green-700",
  COMPLETED: "bg-gray-100 text-gray-500",
  CANCELLED: "bg-red-100 text-red-600",
  WAIT: "bg-yellow-100 text-yellow-700",
  ALLOCATED: "bg-green-100 text-green-700",
  EXPIRED: "bg-gray-100 text-gray-500",
};
 
export default function MyGameActivity() {
  const { data: bookings = [], isLoading: loadingB } = useMyBookings();
  const { data: requests = [], isLoading: loadingR } = useMyRequests();
  const cancelB = useCancelBooking();
  const cancelR = useCancelRequest();
 
  if (loadingB || loadingR) return <p className="p-4">Loading...</p>;
 
  return (
    <div className="space-y-6">
      {/* ─── Upcoming Bookings ─── */}
      <section>
        <h2 className="text-lg font-semibold text-gray-800 mb-3">
          My Bookings
        </h2>
 
        {bookings.length === 0 ? (
          <p className="text-gray-400 text-sm">No bookings yet</p>
        ) : (
          <div className="bg-white rounded-xl shadow divide-y">
            {bookings.map((b: BookingDto) => (
              <div
                key={b.id}
                className="p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2"
              >
                <div>
                  <p className="font-medium text-gray-800">{b.gameName}</p>
                  <p className="text-sm text-gray-500">
                    {formatDate(b.slotStart)} &middot;{" "}
                    {formatTime(b.slotStart)} – {formatTime(b.slotEnd)}
                  </p>
                  <p className="text-xs text-gray-400">
                    Participants: {b.participantNames.join(", ")}
                  </p>
                </div>
 
                <div className="flex items-center gap-2">
                  <span
                    className={`text-xs px-2 py-0.5 rounded-full ${
                      statusBadge[b.status] ?? "bg-gray-100"
                    }`}
                  >
                    {b.status}
                  </span>
 
                  {b.status === "ACTIVE" && (
                    <button
                      onClick={() => cancelB.mutate(b.id)}
                      disabled={cancelB.isPending}
                      className="text-xs bg-red-50 text-red-700 px-2 py-1 rounded hover:bg-red-100"
                    >
                      Cancel
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
 
      {/* ─── My Requests ─── */}
      <section>
        <h2 className="text-lg font-semibold text-gray-800 mb-3">
          My Requests
        </h2>
 
        {requests.length === 0 ? (
          <p className="text-gray-400 text-sm">No requests yet</p>
        ) : (
          <div className="bg-white rounded-xl shadow divide-y">
            {requests.map((r: WaitlistDto) => (
              <div
                key={r.id}
                className="p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2"
              >
                <div>
                  <p className="font-medium text-gray-800">{r.gameName}</p>
                  <p className="text-sm text-gray-500">
                    {formatDate(r.slotStart)} &middot;{" "}
                    {formatTime(r.slotStart)} – {formatTime(r.slotEnd)}
                  </p>
                  <p className="text-xs text-gray-400">
                    Participants: {r.participantNames.join(", ")}
                  </p>
                  <p className="text-xs text-gray-400">
                    Priority Score: {r.priorityScore}
                  </p>
                </div>
 
                <div className="flex items-center gap-2">
                  <span
                    className={`text-xs px-2 py-0.5 rounded-full ${
                      statusBadge[r.status] ?? "bg-gray-100"
                    }`}
                  >
                    {r.status}
                  </span>
 
                  {r.status === "WAIT" && (
                    <button
                      onClick={() => cancelR.mutate(r.id)}
                      disabled={cancelR.isPending}
                      className="text-xs bg-red-50 text-red-700 px-2 py-1 rounded hover:bg-red-100"
                    >
                      Cancel
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}