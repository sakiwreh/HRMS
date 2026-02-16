import { useNavigate } from "react-router-dom";
import { useAppDispatch } from "../../../store/hooks";
import { setUser } from "../authSlice";
import { loginApi } from "../api/api";
 
interface LoginResult {
  success: boolean;
  message?: string;
}
 
export default function useLogin() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
 
  const login = async (email: string, password: string): Promise<LoginResult> => {
    try {
      const user = await loginApi({email, password});
 
      // store session
      localStorage.setItem("auth_user", JSON.stringify(user));
      dispatch(setUser(user));
 
      // go dashboard
      navigate("/dashboard", { replace: true });
 
      return { success: true };
 
    } catch (err: any) {
      if (err.response?.status === 401)
        return { success: false, message: "Invalid email or password" };
 
      return { success: false, message: "Server error. Try again." };
    }
  };
 
  return login;
}