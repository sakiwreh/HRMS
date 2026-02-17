import { useMutation, useQueryClient } from "@tanstack/react-query";
import { assignParticipants } from "../api/travelApi";
 
type Payload = {
  travelId: number;
  employeeIds: number[];
};
 
export const useAssignParticipants = () => {
  const qc = useQueryClient();
 
  return useMutation({
    mutationFn: ({ travelId, employeeIds }: Payload) =>
      assignParticipants(travelId, { employeeIds }),
 
    onSuccess: (_, variables) => {
      qc.invalidateQueries({ queryKey: ["participants", variables.travelId] });
      qc.invalidateQueries({ queryKey: ["travel", variables.travelId] });
    }
  });
};
 