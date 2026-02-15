import { useForm } from "react-hook-form";
import { useLogin } from "../hooks/useLogin";
 
interface FormData {
  email: string;
  password: string;
}
 
export default function LoginPage() {
  const { register, handleSubmit } = useForm<FormData>();
  const { login } = useLogin();
 
  return (
    <div className="flex items-center justify-center h-screen">
      <form onSubmit={handleSubmit(login)} className="border p-6 w-80 space-y-4">
        <h2 className="text-xl font-bold">Login</h2>
 
        <input {...register("email")} placeholder="Email" className="border p-2 w-full" />
        <input {...register("password")} type="password" placeholder="Password" className="border p-2 w-full" />
 
        <button className="bg-blue-500 text-white p-2 w-full">Login</button>
      </form>
    </div>
  );
}
 