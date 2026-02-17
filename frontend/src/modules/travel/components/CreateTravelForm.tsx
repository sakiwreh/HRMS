import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { createTravel } from "../api/travelApi";
 
type TravelForm = {
  title: string;
  description: string;
  destination: string;
  departureDate: string;
  returnDate: string;
};
 
export default function CreateTravelForm() {
  const qc = useQueryClient();
 
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TravelForm>();
 
  const mutation = useMutation({
    mutationFn: createTravel,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["travels"] });
      reset();
    },
  });
 
  const onSubmit = (data: TravelForm) => {
    mutation.mutate(data);
  };
 
  return (
  <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
    <input
      className="border p-2 w-full rounded"
      placeholder="Title"
      {...register("title", { required: "Title required" })}
    />
    {errors.title && <p className="text-red-500 text-sm">{errors.title.message}</p>}
 
    <textarea
      className="border p-2 w-full rounded"
      placeholder="Description"
      {...register("description", { required: "Description required" })}
    />
    {errors.description && <p className="text-red-500 text-sm">{errors.description.message}</p>}
 
    <input
      className="border p-2 w-full rounded"
      placeholder="Destination"
      {...register("destination", { required: "Destination required" })}
    />
 
    <div className="grid grid-cols-2 gap-3">
      <input
        type="datetime-local"
        className="border p-2 w-full rounded"
        {...register("departureDate", { required: true })}
      />
 
      <input
        type="datetime-local"
        className="border p-2 w-full rounded"
        {...register("returnDate", { required: true })}
      />
    </div>
 
    <button className="bg-blue-600 hover:bg-blue-700 text-white py-2 rounded w-full">
      Create Travel Plan
    </button>
  </form>
);
}