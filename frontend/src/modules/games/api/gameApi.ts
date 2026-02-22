import api from "../../../lib/axios";
 
//types
 
export interface GameDto {
  id: number;
  name: string;
  active: boolean;
  startHour: string;
  endHour: string;
  maxDurationMins: number;
  maxPlayersPerSlot: number;
  maxParticipantsPerBooking: number;
  cancellationBeforeMins: number;
  slotGenerationDays: number;
}
 
export interface GameSlotDto {
  id: number;
  gameId: number;
  gameName: string;
  slotDate: string;
  slotStart: string;
  slotEnd: string;
  capacity: number;
  bookedCount: number;
  allocated: boolean;
  status: string;
}
 
export interface WaitlistDto {
  id: number;
  gameId: number;
  slotId: number;
  gameName: string;
  slotStart: string;
  slotEnd: string;
  requestedByName: string;
  appliedDateTime: string;
  status: string;
  priorityScore: number;
  participantNames: string[];
}
 
export interface BookingDto {
  id: number;
  gameId: number;
  gameName: string;
  slotId: number;
  slotStart: string;
  slotEnd: string;
  bookedByName: string;
  bookingDateTime: string;
  status: string;
  participantNames: string[];
}
 
export interface EmployeeLookup {
  id: number;
  empId: string;
  name: string;
}
 
//games
 
export const fetchAllGames = () => api.get<GameDto[]>("/games").then((r) => r.data);
 
export const fetchActiveGames = () =>
  api.get<GameDto[]>("/games/active").then((r) => r.data);
 
export const createGame = (data: {
  name: string;
  startHour: string;
  endHour: string;
  maxDurationMins: number;
  maxPlayersPerSlot: number;
  maxParticipantsPerBooking: number;
  cancellationBeforeMins: number;
  slotGenerationDays: number;
}) => api.post<GameDto>("/games", data).then((r) => r.data);
 
export const updateGame = (
  id: number,
  data: {
    name: string;
    startHour: string;
    endHour: string;
    maxDurationMins: number;
    maxPlayersPerSlot: number;
    maxParticipantsPerBooking: number;
    cancellationBeforeMins: number;
    slotGenerationDays: number;
  }
) => api.put<GameDto>(`/games/${id}`, data).then((r) => r.data);
 
export const toggleGameActive = (id: number) =>
  api.patch<GameDto>(`/games/${id}/toggle`).then((r) => r.data);
 
//slots
 
export const fetchSlots = (gameId: number, from: string, to: string) =>
  api
    .get<GameSlotDto[]>(`/games/${gameId}/slots`, { params: { from, to } })
    .then((r) => r.data);
 
export const generateSlots = (gameId: number, date: string) =>
  api
    .post<string>(`/games/${gameId}/slots/generate`, null, { params: { date } })
    .then((r) => r.data);
 
//interests
 
export const registerInterest = (gameId: number) =>
  api.post<string>(`/games/${gameId}/interest`).then((r) => r.data);
 
export const removeInterest = (gameId: number) =>
  api.delete<string>(`/games/${gameId}/interest`).then((r) => r.data);
 
export const fetchMyInterests = () =>
  api.get<GameDto[]>("/games/interests/me").then((r) => r.data);

//waitlist
 
export const submitSlotRequest = (data: {
  slotId: number;
  participantIds: number[];
}) => api.post<WaitlistDto>("/games/waitlist", data).then((r) => r.data);
 
export const cancelRequest = (id: number) =>
  api.patch<string>(`/games/waitlist/${id}/cancel`).then((r) => r.data);
 
export const fetchMyRequests = () =>
  api.get<WaitlistDto[]>("/games/waitlist/me").then((r) => r.data);

//bookings
 
export const cancelBooking = (id: number) =>
  api.patch<string>(`/games/bookings/${id}/cancel`).then((r) => r.data);
 
export const fetchMyBookings = () =>
  api.get<BookingDto[]>("/games/bookings/me").then((r) => r.data);
 
export const fetchSlotBookings = (slotId: number) =>
  api.get<BookingDto[]>(`/games/slots/${slotId}/bookings`).then((r) => r.data);

//employees
 
export const fetchEmployeeLookup = () =>
  api.get<EmployeeLookup[]>("/employees/lookup").then((r) => r.data);