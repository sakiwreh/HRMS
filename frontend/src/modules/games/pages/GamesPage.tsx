import { useState } from "react";
import { useAppSelector } from "../../../store/hooks";
import {
  useAllGames,
  useActiveGames,
  useMyInterests,
  useRegisterInterest,
  useRemoveInterest,
  useToggleGame,
} from "../hooks/useGames";
import type { GameDto } from "../api/gameApi";
import Modal from "../../../shared/components/Modal";
import GameForm from "../components/GameForm";
import GameSlotsView from "./GameSlotView";
 
export default function GamesPage() {
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
 
  const allGames = useAllGames(isHR);
  const activeGames = useActiveGames();
  const { data: interests = [] } = useMyInterests();
  const regInterest = useRegisterInterest();
  const rmInterest = useRemoveInterest();
  const toggleGame = useToggleGame();
 
  const games: GameDto[] = (isHR ? allGames.data : activeGames.data) ?? [];
  const loading = isHR ? allGames.isLoading : activeGames.isLoading;
 
  const [formOpen, setFormOpen] = useState(false);
  const [editGame, setEditGame] = useState<GameDto | undefined>();
  const [selectedGame, setSelectedGame] = useState<GameDto | null>(null);
 
  const interestedIds = new Set(interests.map((g: GameDto) => g.id));
 
  if (loading) return <div className="p-4">Loading...</div>;
 
  // If a game is selected, show its slots view
  if (selectedGame) {
    return (
      <GameSlotsView
        game={selectedGame}
        onBack={() => setSelectedGame(null)}
      />
    );
  }
 
  return (
    <div className="space-y-4">
      {/* HEADER */}
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">
          {isHR ? "Game Management" : "Games"}
        </h1>
        {isHR && (
          <button
            onClick={() => {
              setEditGame(undefined);
              setFormOpen(true);
            }}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
          >
            + Create Game
          </button>
        )}
      </div>
 
      {/* GAME CARDS */}
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
                  <h2 className="text-lg font-semibold text-gray-800">
                    {g.name}
                  </h2>
                  <span
                    className={`text-xs px-2 py-0.5 rounded-full ${
                      g.active
                        ? "bg-green-100 text-green-700"
                        : "bg-red-100 text-red-600"
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
                <p>
                  Hours: {g.startHour} – {g.endHour}
                </p>
                <p>Slot Duration: {g.maxDurationMins} mins</p>
                <p>Max Players/Slot: {g.maxPlayersPerSlot}</p>
                <p>Cancellation Lead: {g.cancellationBeforeMins} mins</p>
              </div>
 
              {/* ACTIONS */}
              <div className="flex flex-wrap gap-2 pt-2">
                {/* View slots */}
                {g.active && (
                  <button
                    onClick={() => setSelectedGame(g)}
                    className="text-sm bg-blue-50 text-blue-700 px-3 py-1 rounded-lg hover:bg-blue-100"
                  >
                    View Slots
                  </button>
                )}
 
                {/* Interest toggle (non-HR) */}
                {!isHR && g.active && (
                  <button
                    onClick={() =>
                      interested
                        ? rmInterest.mutate(g.id)
                        : regInterest.mutate(g.id)
                    }
                    disabled={
                      regInterest.isPending || rmInterest.isPending
                    }
                    className={`text-sm px-3 py-1 rounded-lg ${
                      interested
                        ? "bg-red-50 text-red-700 hover:bg-red-100"
                        : "bg-green-50 text-green-700 hover:bg-green-100"
                    }`}
                  >
                    {interested ? "Remove Interest" : "Register Interest"}
                  </button>
                )}
 
                {/* HR actions */}
                {isHR && (
                  <>
                    <button
                      onClick={() => {
                        setEditGame(g);
                        setFormOpen(true);
                      }}
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
 
      {/* CREATE / EDIT MODAL */}
      <Modal
        title={editGame ? "Edit Game" : "Create Game"}
        open={formOpen}
        onClose={() => setFormOpen(false)}
      >
        <GameForm
          game={editGame}
          onDone={() => setFormOpen(false)}
        />
      </Modal>
    </div>
  );
}