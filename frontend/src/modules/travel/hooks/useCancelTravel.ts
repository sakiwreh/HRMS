import { useMutation, useQueryClient } from "@tanstack/react-query";
import { cancelTravel } from "../api/travelApi";
 
export default function useCancelTravel() {
  const qc = useQueryClient();
 
  return useMutation({
    mutationFn: (id: number) => cancelTravel(id),
    onSuccess: (_data, id) => {
      qc.invalidateQueries({ queryKey: ["travel", String(id)] });
      qc.invalidateQueries({ queryKey: ["travels"] });
      qc.invalidateQueries({ queryKey: ["my-travels"] });
    },
  });
}