import { useMutation, useQueryClient } from "@tanstack/react-query";
import useParticipants from "../../hooks/useParticipants";
import AssignParticipants from "../AssignParticipants";
import { removeParticipant } from "../../api/travelApi";
 
export default function ParticipantsTab({ travel }: any) {
  const travelId = travel.id;
  const { data: participants = [], isLoading } = useParticipants(travelId);
  const qc = useQueryClient();
 
  const removeMutation = useMutation({
    mutationFn: (empId: number) => removeParticipant(travelId, empId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["participants", travelId] });
    },
  });
 
  const handleRemove = (empId: number, name: string) => {
    if (!confirm(`Remove ${name} from this travel plan?`)) return;
    removeMutation.mutate(empId);
  };
 
  return (
    <div className="space-y-6">
      {/* Current participants */}
      <div>
        <h2 className="text-lg font-semibold mb-3">Current Participants</h2>
        {isLoading && <div className="text-gray-500">Loading...</div>}
        {!isLoading && participants.length === 0 && (
          <div className="text-gray-400 border rounded-md p-4 text-center">
            No participants assigned yet
          </div>
        )}
        {!isLoading && participants.length > 0 && (
          <div className="border rounded-lg divide-y">
            {participants.map((p: any) => {
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
                  <button
                    type="button"
                    onClick={() => handleRemove(empId, name)}
                    disabled={removeMutation.isPending}
                    className="text-red-500 text-sm hover:underline disabled:opacity-50"
                  >
                    Remove
                  </button>
                </div>
              );
            })}
          </div>
        )}
      </div>
 
      {/* Assign form */}
      <AssignParticipants travelId={travelId} />
    </div>
  );
}