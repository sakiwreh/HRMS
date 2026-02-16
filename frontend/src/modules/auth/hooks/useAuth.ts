import { useAppSelector } from "../../../store/hooks"

export const useAuth = () => {
    return useAppSelector((state) => state.auth);
};