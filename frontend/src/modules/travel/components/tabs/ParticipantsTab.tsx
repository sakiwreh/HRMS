import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import useParticipants from "../../hooks/useParticipants";
import AssignParticipants from "../AssignParticipants";
import { removeParticipant } from "../../api/travelApi";
import toast from "react-hot-toast";
 
export default function ParticipantsTab({ travel }: any) {
  const travelId = travel.id;
  const isCancelled = travel?.cancelled;
  const { data: participants = [], isLoading } = useParticipants(travelId);
  const qc = useQueryClient();
  const [search, setSearch] = useState("");
 
  const removeMutation = useMutation({
    mutationFn: (empId: number) => removeParticipant(travelId, empId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["participants", travelId] });
      toast.success("Participant removed");
    },
  });
 
  const handleRemove = (empId: number, name: string) => {
    if (!confirm(`Remove ${name} from this travel plan?`)) return;
    removeMutation.mutate(empId);
  };
 
  const filtered = participants.filter((p: any) => {
    const name = (p.name ?? "").toLowerCase();
    const email = (p.email ?? "").toLowerCase();
    return (
      name.includes(search.toLowerCase()) ||
      email.includes(search.toLowerCase())
    );
  });
 
  return (
    <div className="space-y-6">
      {/* Current participants */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-lg font-semibold">
            Current Participants ({participants.length})
          </h2>
          {participants.length > 5 && (
            <input
              type="text"
              placeholder="Search participants..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="border rounded-md px-3 py-1.5 text-sm w-60"
            />
          )}
        </div>
 
        {isLoading && <div className="text-gray-500">Loading...</div>}
        {!isLoading && participants.length === 0 && (
          <div className="text-gray-400 border rounded-md p-4 text-center">
            No participants assigned yet
          </div>
        )}
        {!isLoading && filtered.length > 0 && (
          <div className="border rounded-lg divide-y max-h-[400px] overflow-y-auto">
            {filtered.map((p: any) => {
              const empId = p.id;
              const name = p.name ?? "Unknown";
              const email = p.email ?? "";
 
              return (
                <div
                  key={empId}
                  className="flex items-center justify-between p-3"
                >
                  <div>
                    <span className="font-medium">{name}</span>
                    {email && (
                      <span className="text-xs text-gray-500 ml-2">
                        {email}
                      </span>
                    )}
                  </div>
                  {!isCancelled && (
                    <button
                      type="button"
                      onClick={() => handleRemove(empId, name)}
                      disabled={removeMutation.isPending}
                      className="text-red-500 text-sm hover:underline disabled:opacity-50"
                    >
                      Remove
                    </button>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
 
      {/* Assign form — only show for non-cancelled */}
      {!isCancelled && <AssignParticipants travelId={travelId} />}
    </div>
  );
}