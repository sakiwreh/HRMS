import axios from '../../../lib/axios'
 
export interface LoginRequest {
  email: string;
  password: string;
}
 
export const loginApi = async (data: LoginRequest) => {
  const res = await axios.post("/auth/login", data);
  return res.data;
};