import type { AuthUser } from "../modules/auth/authSlice"

const KEY = "auth_user"

export const saveUser = (user:AuthUser)=>
    localStorage.setItem(KEY,JSON.stringify(user));

export const getUser = (): AuthUser|null => {
    const data = localStorage.getItem(KEY);
    return data?JSON.parse(data):null;
}

export const removeUser = () => localStorage.removeItem(KEY);