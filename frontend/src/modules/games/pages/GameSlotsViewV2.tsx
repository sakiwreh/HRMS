import { useState, useMemo } from "react";
import { useSlotsV2 } from "../hooks/useGames";
import type { GameV2Dto, SlotV2Dto } from "../api/gameApi";
import Modal from "../../../shared/components/Modal";
import BookSlotFormV2 from "../components/BookSlotFormV2";

interface Props {
  game: GameV2Dto;
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
  REQUESTED: "bg-yellow-100 text-yellow-700",
  BOOKED: "bg-blue-100 text-blue-700",
};

export default function GameSlotsViewV2({ game, onBack }: Props) {
  const today = useMemo(() => new Date(), []);
  const [date, setDate] = useState(formatDate(today));

  const { data: slots = [], isLoading } = useSlotsV2(game.id, date);
  const [bookSlot, setBookSlot] = useState<SlotV2Dto | null>(null);

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

      {/* DATE PICKER */}
      <div className="flex items-end gap-4 bg-white rounded-xl shadow p-4">
        <div>
          <label className="block text-xs text-gray-500 mb-1">Date</label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="border rounded-lg px-3 py-1.5 text-sm"
          />
        </div>
      </div>

      {/* SLOTS GRID */}
      {isLoading ? (
        <p className="text-gray-400">Loading slots...</p>
      ) : slots.length === 0 ? (
        <p className="text-gray-400 text-center py-8">
          No slots available for this date
        </p>
      ) : (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {slots.map((s: SlotV2Dto) => (
            <div
              key={s.slotStart}
              className="bg-white rounded-lg shadow-sm border p-4 flex justify-between items-center"
            >
              <div className="space-y-1">
                <p className="text-sm font-medium">
                  {formatTime(s.slotStart)} – {formatTime(s.slotEnd)}
                </p>
                <span
                  className={`text-xs px-2 py-0.5 rounded-full ${
                    statusColor[s.status] ?? "bg-gray-100 text-gray-500"
                  }`}
                >
                  {s.status}
                </span>
                {s.pendingCount > 0 && (
                  <p className="text-xs text-gray-400">
                    {s.pendingCount} pending request{s.pendingCount > 1 ? "s" : ""}
                  </p>
                )}
              </div>

              {/* Book button — only for AVAILABLE or REQUESTED (user can queue) */}
              {(s.status === "AVAILABLE" || s.status === "REQUESTED") && (
                <button
                  onClick={() => setBookSlot(s)}
                  className="text-sm bg-blue-600 text-white px-3 py-1 rounded-lg hover:bg-blue-700"
                >
                  Request
                </button>
              )}
            </div>
          ))}
        </div>
      )}

      {/* BOOK SLOT MODAL */}
      {bookSlot && (
        <Modal
          title="Request a Slot"
          open={!!bookSlot}
          onClose={() => setBookSlot(null)}
        >
          <BookSlotFormV2
            slot={bookSlot}
            game={game}
            onDone={() => setBookSlot(null)}
          />
        </Modal>
      )}
    </div>
  );
}