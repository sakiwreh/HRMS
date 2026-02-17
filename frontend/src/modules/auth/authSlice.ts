import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
 
export interface AuthUser {
  id: number;
  name: string;
  email: string;
  role: "HR" | "MANAGER" | "EMPLOYEE";
  token?: string;
}
 
interface AuthState {
  user: AuthUser | null;
}
 
const initialState: AuthState = {
  user: JSON.parse(localStorage.getItem("auth_user") || "null"),
};
 
const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<AuthUser>) {
      state.user = action.payload;
    },
    clearUser(state) {
      state.user = null;
      localStorage.removeItem("auth_user");
    },
  },
});
 
export const { setUser, clearUser } = authSlice.actions;
export default authSlice.reducer;