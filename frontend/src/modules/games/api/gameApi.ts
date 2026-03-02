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
  name: string;
  email: string | null;
  designation: string | null;
  department: string | null;
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

export const fetchInterestedEmployees = (gameId: number) =>
  api.get<EmployeeLookup[]>(`/games/${gameId}/interested-employees`).then((r) => r.data);

//---V2 Endpoints:

export interface GameV2Dto {
  id: number;
  name: string;
  active: boolean;
  startHour: string;
  endHour: string;
  maxDurationMins: number;
  maxPlayersPerSlot: number;
  cancellationBeforeMins: number;
}

export interface SlotV2Dto {
  gameId: number;
  gameName: string;
  slotStart: string;
  slotEnd: string;
  status: "AVAILABLE" | "REQUESTED" | "BOOKED";
  pendingCount: number;
}

export interface BookingV2Dto {
  id: number;
  gameId: number;
  gameName: string;
  slotStart: string;
  slotEnd: string;
  bookedByName: string;
  bookingDateTime: string;
  priorityScore: number;
  status: string;
  participantNames: string[];
}

// Admin
export const fetchAllGamesV2 = () => api.get<GameV2Dto[]>("/games/v2").then((r) => r.data);
export const fetchActiveGamesV2 = () => api.get<GameV2Dto[]>("/games/v2/active").then((r) => r.data);
export const createGameV2 = (data: Omit<GameV2Dto, "id" | "active">) =>
  api.post<GameV2Dto>("/games/v2", data).then((r) => r.data);
export const updateGameV2 = (id: number, data: Omit<GameV2Dto, "id" | "active">) =>
  api.put<GameV2Dto>(`/games/v2/${id}`, data).then((r) => r.data);
export const toggleGameV2 = (id: number) =>
  api.patch<GameV2Dto>(`/games/v2/${id}/toggle`).then((r) => r.data);

// Computed slots
export const fetchSlotsV2 = (gameId: number, date: string) =>
  api.get<SlotV2Dto[]>(`/games/v2/${gameId}/slots`, { params: { date } }).then((r) => r.data);

// Interest
export const registerInterestV2 = (gameId: number) =>
  api.post<string>(`/games/v2/${gameId}/interest`).then((r) => r.data);
export const removeInterestV2 = (gameId: number) =>
  api.delete<string>(`/games/v2/${gameId}/interest`).then((r) => r.data);
export const fetchMyInterestsV2 = () =>
  api.get<GameV2Dto[]>("/games/v2/interests/me").then((r) => r.data);
export const fetchInterestedEmployeesV2 = (gameId: number) =>
  api.get<EmployeeLookup[]>(`/games/v2/${gameId}/interested-employees`).then((r) => r.data);

// Booking requests
export const submitRequestV2 = (gameId: number, data: { slotStart: string; participantIds: number[] }) =>
  api.post<BookingV2Dto>(`/games/v2/${gameId}/requests`, data).then((r) => r.data);
export const cancelRequestV2 = (bookingId: number) =>
  api.patch<string>(`/games/v2/requests/${bookingId}/cancel`).then((r) => r.data);
export const fetchMyRequestsV2 = () =>
  api.get<BookingV2Dto[]>("/games/v2/requests/me").then((r) => r.data);
export const fetchMyBookingsV2 = () =>
  api.get<BookingV2Dto[]>("/games/v2/bookings/me").then((r) => r.data);