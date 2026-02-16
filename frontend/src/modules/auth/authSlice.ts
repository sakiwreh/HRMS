import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
 
interface User {
  id: number;
  name: string;
  email: string;
  role: "HR" | "MANAGER" | "EMPLOYEE";
}
 
interface AuthState {
  user: User | null;
}
 
const initialState: AuthState = {
  user: JSON.parse(localStorage.getItem("auth_user") || "null"),
};
 
const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<User>) {
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