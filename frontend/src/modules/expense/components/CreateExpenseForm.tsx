import { useForm } from "react-hook-form";
import useCreateExpense from "../hooks/useCreateExpense";
import useCategories from "../hooks/useCategories";
import useMyTravels from "../../travel/hooks/useMyTravels";
 
type ExpenseForm = {
  travelId: string;
  categoryId: string;
  amount: string;
  description: string;
  expenseDate: string;
};
 
export default function CreateExpenseForm() {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ExpenseForm>();
 
  const mutation = useCreateExpense();
  const { data: categories, isLoading: catLoading } = useCategories();
  const { data: travels, isLoading: travLoading } = useMyTravels();
 
  const onSubmit = (data: ExpenseForm) => {
    mutation.mutate(
      {
        travelId: Number(data.travelId),
        categoryId: Number(data.categoryId),
        amount: Number(data.amount),
        description: data.description || undefined,
        expenseDate: data.expenseDate,
      },
      { onSuccess: () => reset() },
    );
  };
 
  if (catLoading || travLoading)
    return <p className="text-gray-500">Loading...</p>;
 
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
      {/* Travel Plan */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Travel Plan
        </label>
        <select
          className="border p-2 w-full rounded"
          {...register("travelId", { required: "Select a travel plan" })}
        >
          <option value="">Select travel...</option>
          {travels?.map((t: any) => (
            <option key={t.id} value={t.id}>
              {t.title} — {t.destination}
            </option>
          ))}
        </select>
        {errors.travelId && (
          <p className="text-red-500 text-sm">{errors.travelId.message}</p>
        )}
      </div>
 
      {/* Category */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Category
        </label>
        <select
          className="border p-2 w-full rounded"
          {...register("categoryId", { required: "Select a category" })}
        >
          <option value="">Select category...</option>
          {categories?.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
              {c.limit_in_inr != null ? ` (limit: ₹${c.limit_in_inr})` : ""}
            </option>
          ))}
        </select>
        {errors.categoryId && (
          <p className="text-red-500 text-sm">{errors.categoryId.message}</p>
        )}
      </div>
 
      {/* Amount */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Amount (₹)
        </label>
        <input
          type="number"
          step="0.01"
          className="border p-2 w-full rounded"
          placeholder="0.00"
          {...register("amount", {
            required: "Amount required",
            min: { value: 0.01, message: "Must be greater than 0" },
          })}
        />
        {errors.amount && (
          <p className="text-red-500 text-sm">{errors.amount.message}</p>
        )}
      </div>
 
      {/* Description */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Description
        </label>
        <textarea
          className="border p-2 w-full rounded"
          placeholder="What was this expense for?"
          rows={2}
          {...register("description")}
        />
      </div>
 
      {/* Expense Date */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Expense Date
        </label>
        <input
          type="datetime-local"
          className="border p-2 w-full rounded"
          {...register("expenseDate", { required: "Date required" })}
        />
        {errors.expenseDate && (
          <p className="text-red-500 text-sm">{errors.expenseDate.message}</p>
        )}
      </div>
 
      {/* Error message */}
      {mutation.isError && (
        <p className="text-red-500 text-sm">
          {(mutation.error as any)?.response?.data?.message ??
            "Failed to submit expense"}
        </p>
      )}
 
      {mutation.isSuccess && (
        <p className="text-green-600 text-sm">Expense submitted!</p>
      )}
 
      <button
        type="submit"
        disabled={mutation.isPending}
        className="bg-blue-600 hover:bg-blue-700 text-white py-2 rounded w-full disabled:opacity-50"
      >
        {mutation.isPending ? "Submitting..." : "Submit Expense"}
      </button>
    </form>
  );
}
 