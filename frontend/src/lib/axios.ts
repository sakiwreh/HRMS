import axios from "axios";
 
const instance = axios.create({
  baseURL: "http://localhost:8080",
});
 
instance.interceptors.request.use((config) => {
  const data = localStorage.getItem("auth_user");
  if (data) {
    const token = JSON.parse(data).token;
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
 
export default instance;