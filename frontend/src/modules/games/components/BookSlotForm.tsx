import { useState } from "react";
import { useEmployeeLookup, useSubmitRequest } from "../hooks/useGames";
import type { GameDto, GameSlotDto } from "../api/gameApi";
import { useAppSelector } from "../../../store/hooks";
 
interface Props {
  slot: GameSlotDto;
  game: GameDto;
  onDone: () => void;
}
 
export default function BookSlotForm({ slot, game, onDone }: Props) {
  const user = useAppSelector((s) => s.auth.user);
  const { data: employees = [] } = useEmployeeLookup();
  const submit = useSubmitRequest();
 
  const [selected, setSelected] = useState<number[]>([]);
 
  const maxExtra = game.maxParticipantsPerBooking - 1; // requestor is auto-added
 
  const toggle = (id: number) => {
    setSelected((prev) =>
      prev.includes(id)
        ? prev.filter((x) => x !== id)
        : prev.length < maxExtra
          ? [...prev, id]
          : prev,
    );
  };
 
  const handleSubmit = () => {
    submit.mutate(
      { slotId: slot.id, participantIds: selected },
      { onSuccess: onDone },
    );
  };
 
  // filter out the current user from the employee list
  const others = employees.filter((e: { id: number }) => e.id !== user?.id);
 
  return (
    <div className="space-y-4">
      <div className="text-sm text-gray-600">
        <p>
          <strong>Game:</strong> {slot.gameName}
        </p>
        <p>
          <strong>Date:</strong> {slot.slotDate}
        </p>
        <p>
          <strong>Time:</strong> {slot.slotStart?.split("T")[1]?.slice(0, 5)} –{" "}
          {slot.slotEnd?.split("T")[1]?.slice(0, 5)}
        </p>
        <p>
          <strong>Available:</strong> {slot.capacity - slot.bookedCount} /{" "}
          {slot.capacity}
        </p>
      </div>
 
      <div>
        <p className="text-sm font-medium text-gray-700 mb-2">
          Add up to {maxExtra} other participant{maxExtra > 1 ? "s" : ""} (you
          are included automatically):
        </p>
 
        <div className="max-h-48 overflow-y-auto border rounded-lg divide-y">
          {others.length === 0 && (
            <p className="p-3 text-gray-400 text-sm">No employees found</p>
          )}
          {others.map((e: { id: number; name: string; empId: string }) => (
            <label
              key={e.id}
              className="flex items-center gap-3 p-2 hover:bg-gray-50 cursor-pointer"
            >
              <input
                type="checkbox"
                checked={selected.includes(e.id)}
                onChange={() => toggle(e.id)}
                className="accent-blue-600"
              />
              <span className="text-sm">
                {e.name}{" "}
                <span className="text-gray-400 text-xs">({e.empId})</span>
              </span>
            </label>
          ))}
        </div>
      </div>
 
      <button
        onClick={handleSubmit}
        disabled={submit.isPending}
        className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg disabled:opacity-50"
      >
        {submit.isPending ? "Submitting..." : "Submit Request"}
      </button>
    </div>
  );
}