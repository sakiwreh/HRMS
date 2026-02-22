import { useState, useMemo } from "react";
import { useAppSelector } from "../../../store/hooks";
import { useSlots, useGenerateSlots } from "../hooks/useGames";
import type { GameDto, GameSlotDto } from "../api/gameApi";
import Modal from "../../../shared/components/Modal";
import BookSlotForm from "../components/BookSlotForm";
 
interface Props {
  game: GameDto;
  onBack: () => void;
}
 
function formatDate(d: Date) {
  return d.toISOString().split("T")[0];
}
 
function formatTime(dt: string) {
  return dt?.split("T")[1]?.slice(0, 5) ?? "";
}
 
const statusColor: Record<string, string> = {
  AVAILABLE: "bg-green-100 text-green-700",
  LOCKED: "bg-yellow-100 text-yellow-700",
  COMPLETED: "bg-gray-100 text-gray-500",
  CANCELLED: "bg-red-100 text-red-600",
};
 
export default function GameSlotsView({ game, onBack }: Props) {
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
 
  const today = useMemo(() => new Date(), []);
  const [from, setFrom] = useState(formatDate(today));
  const futureDate = useMemo(() => {
    const d = new Date(today);
    d.setDate(d.getDate() + game.slotGenerationDays);
    return formatDate(d);
  }, [today, game.slotGenerationDays]);
  const [to, setTo] = useState(futureDate);
 
  const { data: slots = [], isLoading } = useSlots(game.id, from, to);
  const genSlots = useGenerateSlots();
 
  const [bookSlot, setBookSlot] = useState<GameSlotDto | null>(null);
  const [genDate, setGenDate] = useState(formatDate(today));
 
  // Group slots by date
  const byDate = useMemo(() => {
    const map: Record<string, GameSlotDto[]> = {};
    slots.forEach((s: GameSlotDto) => {
      const d = s.slotDate;
      if (!map[d]) map[d] = [];
      map[d].push(s);
    });
    return Object.entries(map).sort(([a], [b]) => a.localeCompare(b));
  }, [slots]);
 
  return (
    <div className="space-y-4">
      {/* HEADER */}
      <div className="flex items-center gap-3">
        <button
          onClick={onBack}
          className="text-blue-600 hover:text-blue-800 text-sm"
        >
          &larr; Back to Games
        </button>
        <h1 className="text-xl font-semibold">{game.name} — Slots</h1>
      </div>
 
      {/* FILTERS */}
      <div className="flex flex-wrap items-end gap-4 bg-white rounded-xl shadow p-4">
        <div>
          <label className="block text-xs text-gray-500 mb-1">From</label>
          <input
            type="date"
            value={from}
            onChange={(e) => setFrom(e.target.value)}
            className="border rounded-lg px-3 py-1.5 text-sm"
          />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">To</label>
          <input
            type="date"
            value={to}
            onChange={(e) => setTo(e.target.value)}
            className="border rounded-lg px-3 py-1.5 text-sm"
          />
        </div>
 
        {isHR && (
          <div className="flex items-end gap-2 ml-auto">
            <div>
              <label className="block text-xs text-gray-500 mb-1">
                Generate for
              </label>
              <input
                type="date"
                value={genDate}
                onChange={(e) => setGenDate(e.target.value)}
                className="border rounded-lg px-3 py-1.5 text-sm"
              />
            </div>
            <button
              onClick={() =>
                genSlots.mutate({ gameId: game.id, date: genDate })
              }
              disabled={genSlots.isPending}
              className="bg-green-600 hover:bg-green-700 text-white text-sm px-4 py-1.5 rounded-lg disabled:opacity-50"
            >
              Generate
            </button>
          </div>
        )}
      </div>
 
      {/* SLOTS */}
      {isLoading ? (
        <p className="text-gray-400">Loading slots...</p>
      ) : byDate.length === 0 ? (
        <p className="text-gray-400 text-center py-8">
          No slots found for this range
        </p>
      ) : (
        byDate.map(([date, daySlots]) => (
          <div key={date} className="space-y-2">
            <h3 className="text-sm font-semibold text-gray-600">{date}</h3>
            <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
              {daySlots.map((s) => (
                <div
                  key={s.id}
                  className="bg-white rounded-lg shadow-sm border p-3 flex justify-between items-center"
                >
                  <div>
                    <p className="text-sm font-medium">
                      {formatTime(s.slotStart)} – {formatTime(s.slotEnd)}
                    </p>
                    <p className="text-xs text-gray-500">
                      {s.bookedCount}/{s.capacity} booked
                    </p>
                    <span
                      className={`text-xs px-2 py-0.5 rounded-full ${
                        statusColor[s.status] ?? "bg-gray-100 text-gray-500"
                      }`}
                    >
                      {s.status}
                      {s.allocated ? " (Allocated)" : ""}
                    </span>
                  </div>
 
                  {/* Book button for non-HR when slot is available */}
                  {!isHR && s.status === "AVAILABLE" && !s.allocated && (
                    <button
                      onClick={() => setBookSlot(s)}
                      className="text-sm bg-blue-600 text-white px-3 py-1 rounded-lg hover:bg-blue-700"
                    >
                      Book
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))
      )}
 
      {/* BOOK SLOT MODAL */}
      {bookSlot && (
        <Modal
          title="Book a Slot"
          open={!!bookSlot}
          onClose={() => setBookSlot(null)}
        >
          <BookSlotForm
            slot={bookSlot}
            game={game}
            onDone={() => setBookSlot(null)}
          />
        </Modal>
      )}
    </div>
  );
}