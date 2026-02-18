import { useEffect } from "react";
import { useForm } from "react-hook-form";
import useEmployees from "../hooks/useEmployees";
import useParticipants from "../hooks/useParticipants";
import { useAssignParticipants } from "../hooks/useAssignParticipants";
 
type Props = {
  travelId: number;
};
 
type FormData = {
  employeeIds: string[];
};
 
export default function AssignParticipants({ travelId }: Props) {
 
  const { data: employees = [], isLoading: empLoading } = useEmployees();
  const { data: existingParticipants = [], isLoading: partLoading } = useParticipants(travelId);
  const { mutateAsync, isPending } = useAssignParticipants();
 
  const {
    register,
    handleSubmit,
    setValue,
    watch,
  } = useForm<FormData>({
    defaultValues: { employeeIds: [] },
  });
 
  const selected = watch("employeeIds");

  //Existing participants
  useEffect(() => {
    if (!employees.length || !existingParticipants.length) return;
 
    const ids = existingParticipants.map((p: any) =>
      String(p.employee?.id ?? p.employeeId ?? p.user?.id ?? p.id)
    );
 
    setValue("employeeIds", ids, { shouldValidate: true });
 
  }, [existingParticipants, employees, setValue]);
  const onSubmit = async (data: FormData) => {
    try {
      await mutateAsync({
        travelId,
        employeeIds: data.employeeIds.map(Number),
      });
 
      alert("Participants saved successfully");
    } catch (e) {
      console.error(e);
      alert("Failed to save participants");
    }
  };
 
  if (empLoading || partLoading) return <div>Loading employees...</div>;
 
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <h2 className="text-lg font-semibold mb-3">Assign Employees</h2>
 
      <div className="border rounded-lg divide-y max-h-[420px] overflow-y-auto">
        {employees.map((emp: any) => {
          const checked = selected?.includes(String(emp.id));
 
          return (
            <label
              key={emp.id}
              className={`flex items-center gap-3 p-3 cursor-pointer transition
                ${checked ? "bg-blue-50" : "hover:bg-gray-50"}
              `}
            >
              <input
                type="checkbox"
                value={String(emp.id)}
                {...register("employeeIds")}
                className="w-4 h-4 accent-blue-600"
              />
 
              <div className="flex flex-col">
                <span className="font-medium">{emp.name}</span>
                <span className="text-xs text-gray-500">{emp.email}</span>
              </div>
            </label>
          );
        })}
      </div>
 
      <button
        type="submit"
        disabled={isPending}
        className="bg-blue-600 text-white px-5 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
      >
        {isPending ? "Saving..." : "Save Participants"}
      </button>
    </form>
  );
}