import { useEffect } from "react";
import { useAppDispatch } from "../../../store/hooks";
import { clearUser, setUser } from "../authSlice";

 
export const useAuthInit = () => {
  const dispatch = useAppDispatch();
 
  useEffect(() => {
    const data = localStorage.getItem("auth_user");
    if (data) dispatch(setUser(JSON.parse(data)));
    else dispatch(clearUser());
  }, []);
};