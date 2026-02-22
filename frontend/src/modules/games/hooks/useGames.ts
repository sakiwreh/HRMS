import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";
import {
  fetchAllGames,
  fetchActiveGames,
  createGame,
  updateGame,
  toggleGameActive,
  fetchSlots,
  generateSlots,
  registerInterest,
  removeInterest,
  fetchMyInterests,
  submitSlotRequest,
  cancelRequest,
  fetchMyRequests,
  cancelBooking,
  fetchMyBookings,
  fetchSlotBookings,
  fetchEmployeeLookup,
  fetchInterestedEmployees,
} from "../api/gameApi";
 
//Games
 
export function useAllGames(enabled = true) {
  return useQuery({ queryKey: ["games", "all"], queryFn: fetchAllGames, enabled });
}
 
export function useActiveGames() {
  return useQuery({ queryKey: ["games", "active"], queryFn: fetchActiveGames });
}
 
export function useCreateGame() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: createGame,
    onSuccess: () => {
      toast.success("Game created");
      qc.invalidateQueries({ queryKey: ["games"] });
    },
  });
}
 
export function useUpdateGame() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: Parameters<typeof updateGame>[1] }) =>
      updateGame(id, data),
    onSuccess: () => {
      toast.success("Game updated");
      qc.invalidateQueries({ queryKey: ["games"] });
    },
  });
}
 
export function useToggleGame() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: toggleGameActive,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["games"] });
    },
  });
}
 
//Slots
 
export function useSlots(gameId: number, from: string, to: string, enabled = true) {
  return useQuery({
    queryKey: ["game-slots", gameId, from, to],
    queryFn: () => fetchSlots(gameId, from, to),
    enabled: enabled && !!gameId,
  });
}
 
export function useGenerateSlots() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ gameId, date }: { gameId: number; date: string }) =>
      generateSlots(gameId, date),
    onSuccess: () => {
      toast.success("Slots generated");
      qc.invalidateQueries({ queryKey: ["game-slots"] });
    },
  });
}
 
//Interests
 
export function useMyInterests() {
  return useQuery({ queryKey: ["game-interests"], queryFn: fetchMyInterests });
}
 
export function useRegisterInterest() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: registerInterest,
    onSuccess: () => {
      toast.success("Interest registered");
      qc.invalidateQueries({ queryKey: ["game-interests"] });
      qc.invalidateQueries({ queryKey: ["games"] });
    },
  });
}
 
export function useRemoveInterest() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: removeInterest,
    onSuccess: () => {
      toast.success("Interest removed");
      qc.invalidateQueries({ queryKey: ["game-interests"] });
      qc.invalidateQueries({ queryKey: ["games"] });
    },
  });
}
 
//Waitlist
 
export function useMyRequests() {
  return useQuery({ queryKey: ["game-requests"], queryFn: fetchMyRequests });
}
 
export function useSubmitRequest() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: submitSlotRequest,
    onSuccess: () => {
      toast.success("Slot request submitted");
      qc.invalidateQueries({ queryKey: ["game-requests"] });
      qc.invalidateQueries({ queryKey: ["game-slots"] });
    },
  });
}
 
export function useCancelRequest() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: cancelRequest,
    onSuccess: () => {
      toast.success("Request cancelled");
      qc.invalidateQueries({ queryKey: ["game-requests"] });
      qc.invalidateQueries({ queryKey: ["game-slots"] });
    },
  });
}
 
//Booking
 
export function useMyBookings() {
  return useQuery({ queryKey: ["game-bookings"], queryFn: fetchMyBookings });
}
 
export function useSlotBookings(slotId: number, enabled = true) {
  return useQuery({
    queryKey: ["slot-bookings", slotId],
    queryFn: () => fetchSlotBookings(slotId),
    enabled: enabled && !!slotId,
  });
}
 
export function useCancelBooking() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: cancelBooking,
    onSuccess: () => {
      toast.success("Booking cancelled");
      qc.invalidateQueries({ queryKey: ["game-bookings"] });
      qc.invalidateQueries({ queryKey: ["game-slots"] });
      qc.invalidateQueries({ queryKey: ["game-requests"] });
    },
  });
}
 
//Employee
 
export function useEmployeeLookup(enabled = true) {
  return useQuery({
    queryKey: ["employee-lookup"],
    queryFn: fetchEmployeeLookup,
    enabled,
  });
}

export function useInterestedEmployees(gameId: number, enabled = true) {
  return useQuery({
    queryKey: ["game-interested-employees", gameId],
    queryFn: () => fetchInterestedEmployees(gameId),
    enabled: enabled && !!gameId,
  });
}