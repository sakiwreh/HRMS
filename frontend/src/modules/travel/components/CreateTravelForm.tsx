import { useForm } from "react-hook-form";
import { createTravel } from "../api/travelApi";
import { useQueryClient } from "@tanstack/react-query";
 
export default function CreateTravelForm() {
  const { register, handleSubmit, reset } = useForm();
  const qc = useQueryClient();
 
  const onSubmit = async (data: any) => {
    await createTravel(data);
    qc.invalidateQueries({ queryKey: ["travels"] });
    reset();
  };
 
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex gap-2">
      <input {...register("title")} placeholder="Title" className="border p-1"/>
      <input {...register("destination")} placeholder="Destination" className="border p-1"/>
      <input type="date" {...register("departureDate")} className="border p-1"/>
      <input type="date" {...register("returnDate")} className="border p-1"/>
 
      <button className="bg-blue-500 text-white px-3">Create</button>
    </form>
  );
}
 