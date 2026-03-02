import { useMemo, useState } from "react";
import { useInterestedEmployeesV2, useSubmitRequestV2 } from "../hooks/useGames";
import type { GameV2Dto, SlotV2Dto, EmployeeLookup } from "../api/gameApi";
import { useAppSelector } from "../../../store/hooks";

interface Props {
  slot: SlotV2Dto;
  game: GameV2Dto;
  onDone: () => void;
}

export default function BookSlotFormV2({ slot, game, onDone }: Props) {
  const user = useAppSelector((s) => s.auth.user);
  const { data: interestedEmployees = [], isLoading: employeesLoading } =
    useInterestedEmployeesV2(game.id);
  const submit = useSubmitRequestV2();

  const [selected, setSelected] = useState<number[]>([]);
  const [search, setSearch] = useState("");

  const maxExtra = game.maxPlayersPerSlot - 1;

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
      { gameId: game.id, data: { slotStart: slot.slotStart, participantIds: selected } },
      { onSuccess: onDone },
    );
  };

  // Remove current user from the list of pickable participants
  const others = useMemo(
    () => interestedEmployees.filter((e: EmployeeLookup) => e.id !== user?.id),
    [interestedEmployees, user?.id],
  );

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return others;
    return others.filter(
      (e: EmployeeLookup) =>
        e.name.toLowerCase().includes(q) ||
        (e.email ?? "").toLowerCase().includes(q) ||
        (e.designation ?? "").toLowerCase().includes(q) ||
        (e.department ?? "").toLowerCase().includes(q),
    );
  }, [others, search]);

  return (
    <div className="space-y-4">
      {/* Slot summary */}
      <div className="text-sm text-gray-600">
        <p><strong>Game:</strong> {slot.gameName}</p>
        <p><strong>Time:</strong> {slot.slotStart?.split("T")[1]?.slice(0, 5)} – {slot.slotEnd?.split("T")[1]?.slice(0, 5)}</p>
        <p><strong>Status:</strong>{" "}
          <span className={slot.status === "AVAILABLE" ? "text-green-600" : slot.status === "REQUESTED" ? "text-yellow-600" : "text-red-600"}>
            {slot.status}
          </span>
          {slot.pendingCount > 0 && <span className="text-gray-400 ml-1">({slot.pendingCount} pending)</span>}
        </p>
      </div>

      {/* Participant picker */}
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
            <p className="p-3 text-gray-400 text-sm">No interested employees found</p>
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
                {e.email && <span className="text-gray-400 text-xs">{e.email}</span>}
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