import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import useEmployees from "../hooks/useEmployees";
import useParticipants from "../hooks/useParticipants";
import { useAssignParticipants } from "../hooks/useAssignParticipants";
import toast from "react-hot-toast";
 
type Props = {
  travelId: number;
};
 
type FormData = {
  employeeIds: string[];
};
 
export default function AssignParticipants({ travelId }: Props) {
  const { data: employees = [], isLoading: empLoading } = useEmployees();
  const { data: existingParticipants = [], isLoading: partLoading } =
    useParticipants(travelId);
  const { mutateAsync, isPending } = useAssignParticipants();
  const [search, setSearch] = useState("");
 
  const { register, handleSubmit, setValue, watch } = useForm<FormData>({
    defaultValues: { employeeIds: [] },
  });
 
  const selected = watch("employeeIds");
 
  useEffect(() => {
    if (!employees.length || !existingParticipants.length) return;
    const ids = existingParticipants.map((p: any) =>
      String(p.employee?.id ?? p.employeeId ?? p.user?.id ?? p.id),
    );
    setValue("employeeIds", ids, { shouldValidate: true });
  }, [existingParticipants, employees, setValue]);
 
  const onSubmit = async (data: FormData) => {
    try {
      await mutateAsync({
        travelId,
        employeeIds: data.employeeIds.map(Number),
      });
      toast.success("Participants saved successfully");
    } catch {
      // error toast handled by axios interceptor
    }
  };
 
  if (empLoading || partLoading) return <div>Loading employees...</div>;
 
  const filtered = employees.filter((emp: any) => {
    const name = (emp.name ?? "").toLowerCase();
    const email = (emp.email ?? "").toLowerCase();
    return (
      name.includes(search.toLowerCase()) ||
      email.includes(search.toLowerCase())
    );
  });
 
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <h2 className="text-lg font-semibold mb-1">Assign Employees</h2>
 
      <input
        type="text"
        placeholder="Search employees by name or email..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className="border rounded-md px-3 py-2 w-full text-sm"
      />
 
      <div className="border rounded-lg divide-y max-h-[420px] overflow-y-auto">
        {filtered.map((emp: any) => {
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
        {filtered.length === 0 && (
          <div className="p-4 text-center text-sm text-gray-400">
            No employees match "{search}"
          </div>
        )}
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