import { useState } from "react";
import { useAppSelector } from "../../../store/hooks";
import {
  useAllGamesV2,
  useActiveGamesV2,
  useMyInterestsV2,
  useRegisterInterestV2,
  useRemoveInterestV2,
  useToggleGameV2,
  useMyRequestsV2,
  useMyBookingsV2,
  useCancelRequestV2,
} from "../hooks/useGames";
import type { GameV2Dto, BookingV2Dto } from "../api/gameApi";
import Modal from "../../../shared/components/Modal";
import GameFormV2 from "../components/GameFormV2";
import GameSlotsViewV2 from "./GameSlotsViewV2";

/* helpers */
function formatTime(dt: string) {
  return dt?.split("T")[1]?.slice(0, 5) ?? "";
}
function formatDate(dt: string) {
  return dt?.split("T")[0] ?? "";
}

const statusBadge: Record<string, string> = {
  ACTIVE: "bg-green-100 text-green-700",
  PENDING: "bg-yellow-100 text-yellow-700",
  EXPIRED: "bg-gray-100 text-gray-500",
  CANCELLED: "bg-red-100 text-red-600",
};

export default function GameV2Page() {
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";

  /* data hooks */
  const allGames = useAllGamesV2(isHR);
  const activeGames = useActiveGamesV2();
  const { data: interests = [] } = useMyInterestsV2();
  const regInterest = useRegisterInterestV2();
  const rmInterest = useRemoveInterestV2();
  const toggleGame = useToggleGameV2();
  const { data: myBookings = [], isLoading: loadingB } = useMyBookingsV2();
  const cancelReq = useCancelRequestV2();

  const games: GameV2Dto[] = (isHR ? allGames.data : activeGames.data) ?? [];
  const loading = isHR ? allGames.isLoading : activeGames.isLoading;

  /* local state */
  const [formOpen, setFormOpen] = useState(false);
  const [editGame, setEditGame] = useState<GameV2Dto | undefined>();
  const [selectedGame, setSelectedGame] = useState<GameV2Dto | null>(null);

  const interestedIds = new Set(interests.map((g: GameV2Dto) => g.id));

  /* slot sub-view */
  if (selectedGame) {
    return (
      <GameSlotsViewV2
        game={selectedGame}
        onBack={() => setSelectedGame(null)}
      />
    );
  }

  if (loading) return <div className="p-4">Loading...</div>;

  return (
    <div className="space-y-6">
      {/* GAME CARDS */}
      <section className="space-y-4">
        <div className="flex justify-between items-center">
          <h1 className="text-xl font-semibold">
            {isHR ? "Game Management (New)" : "Games (New)"}
          </h1>
          {isHR && (
            <button
              onClick={() => { setEditGame(undefined); setFormOpen(true); }}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
            >
              + Create Game
            </button>
          )}
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {games.length === 0 && (
            <p className="text-gray-400 col-span-full text-center py-8">
              No games configured yet
            </p>
          )}

          {games.map((g) => {
            const interested = interestedIds.has(g.id);
            return (
              <div
                key={g.id}
                className={`bg-white rounded-xl shadow p-5 space-y-3 border-l-4 ${
                  g.active ? "border-green-500" : "border-gray-300"
                }`}
              >
                <div className="flex justify-between items-start">
                  <div>
                    <h2 className="text-lg font-semibold text-gray-800">{g.name}</h2>
                    <span
                      className={`text-xs px-2 py-0.5 rounded-full ${
                        g.active ? "bg-green-100 text-green-700" : "bg-red-100 text-red-600"
                      }`}
                    >
                      {g.active ? "Active" : "Inactive"}
                    </span>
                  </div>
                  {interested && (
                    <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">
                      Interested
                    </span>
                  )}
                </div>

                <div className="text-sm text-gray-500 space-y-1">
                  <p>Hours: {g.startHour} – {g.endHour}</p>
                  <p>Slot Duration: {g.maxDurationMins} mins</p>
                  <p>Max Players/Slot: {g.maxPlayersPerSlot}</p>
                  <p>Cancellation Lead: {g.cancellationBeforeMins} mins</p>
                </div>

                {/* ACTIONS */}
                <div className="flex flex-wrap gap-2 pt-2">
                  {g.active && (
                    <button
                      onClick={() => setSelectedGame(g)}
                      className="text-sm bg-blue-50 text-blue-700 px-3 py-1 rounded-lg hover:bg-blue-100"
                    >
                      View Slots
                    </button>
                  )}

                  {!isHR && g.active && (
                    <button
                      onClick={() =>
                        interested ? rmInterest.mutate(g.id) : regInterest.mutate(g.id)
                      }
                      disabled={regInterest.isPending || rmInterest.isPending}
                      className={`text-sm px-3 py-1 rounded-lg ${
                        interested
                          ? "bg-red-50 text-red-700 hover:bg-red-100"
                          : "bg-green-50 text-green-700 hover:bg-green-100"
                      }`}
                    >
                      {interested ? "Remove Interest" : "Register Interest"}
                    </button>
                  )}

                  {isHR && (
                    <>
                      <button
                        onClick={() => { setEditGame(g); setFormOpen(true); }}
                        className="text-sm bg-yellow-50 text-yellow-700 px-3 py-1 rounded-lg hover:bg-yellow-100"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => toggleGame.mutate(g.id)}
                        className={`text-sm px-3 py-1 rounded-lg ${
                          g.active
                            ? "bg-red-50 text-red-700 hover:bg-red-100"
                            : "bg-green-50 text-green-700 hover:bg-green-100"
                        }`}
                      >
                        {g.active ? "Deactivate" : "Activate"}
                      </button>
                    </>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </section>

      {/* MY ACTIVITY*/}
      {!isHR && (
        <>
          <section className="overflow-y-auto">
            <h2 className="text-lg font-semibold text-gray-800 mb-3">My Bookings</h2>
            {loadingB ? (
              <p className="text-gray-400 text-sm">Loading...</p>
            ) : myBookings.length === 0 ? (
              <p className="text-gray-400 text-sm">No bookings yet</p>
            ) : (
              <div className="bg-white rounded-xl shadow divide-y h-90 overflow-y-auto">
                {myBookings.map((b: BookingV2Dto) => (
                  <div key={b.id} className="p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
                    <div>
                      <p className="font-medium text-gray-800">{b.gameName}</p>
                      <p className="text-sm text-gray-500">
                        {formatDate(b.slotStart)} &middot; {formatTime(b.slotStart)} – {formatTime(b.slotEnd)}
                      </p>
                      <p className="text-xs text-gray-400">Participants: {b.participantNames.join(", ")}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className={`text-xs px-2 py-0.5 rounded-full ${statusBadge[b.status] ?? "bg-gray-100"}`}>
                        {b.status}
                      </span>
                      {b.status === "ACTIVE" && (
                        <button
                          onClick={() => cancelReq.mutate(b.id)}
                          disabled={cancelReq.isPending}
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
        </>
      )}

      {/* CREATE / EDIT MODAL */}
      <Modal
        title={editGame ? "Edit Game" : "Create Game"}
        open={formOpen}
        onClose={() => setFormOpen(false)}
      >
        <GameFormV2 game={editGame} onDone={() => setFormOpen(false)} />
      </Modal>
    </div>
  );
}