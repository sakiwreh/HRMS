import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
 
export interface AuthUser {
  id: number;
  name: string;
  email: string;
  role: string;
  token: string;
}
 
interface AuthState {
  user: AuthUser | null;
  initialized: boolean;
}
 
const initialState: AuthState = {
  user: null,
  initialized: false,
};
 
const slice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<AuthUser>) {
      state.user = action.payload;
      state.initialized = true;
    },
    clearUser(state) {
      state.user = null;
      state.initialized = true;
    },
  },
});
 
export const { setUser, clearUser } = slice.actions;
export default slice.reducer;