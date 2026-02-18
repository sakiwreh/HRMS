import { useForm } from "react-hook-form";
import { useState } from "react";
import useLogin from "../hooks/useLogin";
 
type LoginForm = {
  email: string;
  password: string;
};
 
export default function LoginPage() {
  const login = useLogin();
  const [serverError, setServerError] = useState("");
 
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm<LoginForm>();
 
  const onSubmit = async (data: LoginForm) => {
    setServerError("");
 
    const result = await login(data.email, data.password);
 
    if (!result.success)
      setServerError("Login failed. Try again");
  };
 
  return (
    <div className="h-screen flex items-center justify-center bg-gray-100">
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="bg-white p-6 rounded shadow w-80"
      >
        <h2 className="text-xl font-semibold mb-4">Login</h2>
 
        {/* server error */}
        {serverError && (
          <div className="bg-red-100 text-red-600 text-sm p-2 mb-3 rounded">
            {serverError}
          </div>
        )}
 
        {/* email */}
        <input
          type="email"
          placeholder="Email"
          className="border p-2 w-full rounded mb-1"
          {...register("email", {
            required: "Email is required",
            pattern: {
              value: /\S+@\S+\.\S+/,
              message: "Invalid email format"
            }
          })}
        />
        {errors.email && (
          <p className="text-red-500 text-xs mb-2">
            {errors.email.message}
          </p>
        )}
 
        {/* password */}
        <input
          type="password"
          placeholder="Password"
          className="border p-2 w-full rounded mb-1"
          {...register("password", {
            required: "Password is required",
            minLength: {
              value: 4,
              message: "Password must be at least 4 characters"
            }
          })}
        />
        {errors.password && (
          <p className="text-red-500 text-xs mb-3">
            {errors.password.message}
          </p>
        )}
 
        <button
          disabled={isSubmitting}
          className="bg-blue-500 text-white w-full py-2 rounded mt-2 disabled:opacity-50"
        >
          {isSubmitting ? "Logging in..." : "Login"}
        </button>
      </form>
    </div>
  );
}