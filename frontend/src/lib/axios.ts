import axios from "axios";
import toast from "react-hot-toast";
 
const instance = axios.create({
  baseURL: "http://localhost:8080",
});
 
instance.interceptors.request.use(
  (config) => {
    try {
      const stored = localStorage.getItem("auth_user");
 
      if (stored) {
        const user = JSON.parse(stored);
 
        if (user?.token) {
          config.headers = config.headers ?? {};
          config.headers.Authorization = `Bearer ${user.token}`;
        }
      }
 
      return config;
    } catch (error) {
      localStorage.removeItem("auth_user");
      return config;
    }
  },
  (error) => Promise.reject(error)
);
 
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("auth_user");
      window.location.href = "/login";
    }
    else{
      const msg = error.response?.data?.message || error.response?.data?.error || error.message;
      toast.error(msg);
    }
 
    return Promise.reject(error);
  }
);
 
export default instance;
 