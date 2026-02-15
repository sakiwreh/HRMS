import { useNavigate } from "react-router-dom";
import { useAppDispatch } from "../../../store/hooks";
import { setUser } from "../authSlice";
import { loginApi, type LoginRequest } from "../api/api";
 
export const useLogin = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
 
  const login = async (data: LoginRequest) => {
    const user = await loginApi(data);
 
    localStorage.setItem("auth_user", JSON.stringify(user));
    dispatch(setUser(user));
 
    if (user.role === "HR") navigate("/hr");
    else if (user.role === "MANAGER") navigate("/manager");
    else navigate("/employee");
  };
 
  return { login };
};