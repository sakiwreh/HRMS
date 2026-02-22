import { useMemo, useState } from "react";
import { useInterestedEmployees, useSubmitRequest } from "../hooks/useGames";
import type { EmployeeLookup, GameDto, GameSlotDto } from "../api/gameApi";
import { useAppSelector } from "../../../store/hooks";
 
interface Props {
  slot: GameSlotDto;
  game: GameDto;
  onDone: () => void;
}
 
export default function BookSlotForm({ slot, game, onDone }: Props) {
  const user = useAppSelector((s) => s.auth.user);
  const { data: interestedEmployees = [], isLoading: employeesLoading } =
    useInterestedEmployees(game.id);
  const submit = useSubmitRequest();
 
  const [selected, setSelected] = useState<number[]>([]);
  const [search, setSearch] = useState("");
 
  const maxExtra = game.maxPlayersPerSlot - 1; // requestor is auto-added
 
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
 
  // Only interested employees are fetched from API; remove current user from options.
  const others = useMemo(
    () => interestedEmployees.filter((e: EmployeeLookup) => e.id !== user?.id),
    [interestedEmployees, user?.id],
  );
 
  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return others;
    return others.filter((e: EmployeeLookup) =>
      e.name.toLowerCase().includes(q) ||
      (e.email ?? "").toLowerCase().includes(q) ||
      (e.designation ?? "").toLowerCase().includes(q) ||
      (e.department ?? "").toLowerCase().includes(q),
    );
  }, [others, search]);
 
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
          Add up to {maxExtra} other participant{maxExtra > 1 ? "s" : ""} from
          interested employees (you are included automatically):
        </p>
 
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by name, email, designation..."
          className="w-full border rounded-lg px-3 py-2 text-sm mb-2"
        />
 
        <div className="max-h-48 overflow-y-auto border rounded-lg divide-y">
          {employeesLoading && (
            <p className="p-3 text-gray-400 text-sm">Loading employees...</p>
          )}
          {!employeesLoading && others.length === 0 && (
            <p className="p-3 text-gray-400 text-sm">
              No interested employees found
            </p>
          )}
          {!employeesLoading && others.length > 0 && filtered.length === 0 && (
            <p className="p-3 text-gray-400 text-sm">No matches found</p>
          )}
          {filtered.map((e: EmployeeLookup) => (
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
              <span className="text-sm flex flex-col">
                <span>{e.name}</span>
                {e.email && (
                  <span className="text-gray-400 text-xs">{e.email}</span>
                )}
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