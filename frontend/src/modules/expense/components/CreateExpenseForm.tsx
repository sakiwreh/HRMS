import { useForm } from "react-hook-form";
import useCreateExpense from "../hooks/useCreateExpense";
import useCategories from "../hooks/useCategories";
import useMyTravels from "../../travel/hooks/useMyTravels";
import toast from "react-hot-toast";
import { useState } from "react";
import { uploadExpenseProof } from "../api/expenseApi";
 
type ExpenseForm = {
  travelId: string;
  categoryId: string;
  amount: string;
  description: string;
  expenseDate: string;
};
 
export default function CreateExpenseForm({ onDone }: { onDone?: () => void }) {
  const {
    register,
    reset,
    watch,
    formState: { errors },
  } = useForm<ExpenseForm>();
 
  const [proofFile, setProofFile] = useState<File | null>(null);
  const mutation = useCreateExpense();
  const { data: categories, isLoading: catLoading } = useCategories();
  const { data: travels, isLoading: travLoading } = useMyTravels();
 
  const selectedCategoryId = watch("categoryId");
  const selectedCategory = categories?.find(
    (c) => String(c.id) === selectedCategoryId,
  );
 
  const selectedTravelId = watch("travelId");
  const selectedTravel = travels?.find(
    (t: any) => String(t.id) === selectedTravelId,
  );
  const maxPerDay = selectedTravel?.maxPerDayAmount;
 
  const doSubmit = (data: ExpenseForm, draft: boolean) => {
    const amount = Number(data.amount);
 
    // Frontend category limit validation
    if (
      selectedCategory?.limit_in_inr &&
      amount > selectedCategory.limit_in_inr
    ) {
      toast.error(
        `Amount exceeds the ${selectedCategory.name} category limit of ₹${selectedCategory.limit_in_inr}`,
      );
      return;
    }
 
    // Frontend daily budget hint
    if (!draft && maxPerDay && amount > maxPerDay) {
      toast.error(
        `Amount exceeds the max per-day budget of ₹${maxPerDay} for this travel plan`,
      );
      return;
    }
 
    mutation.mutate(
      {
        travelId: Number(data.travelId),
        categoryId: Number(data.categoryId),
        amount,
        description: data.description || undefined,
        expenseDate: data.expenseDate,
        draft,
      },
      {
        onSuccess: async (created) => {
          // Upload proof if file was selected
          if (proofFile && created?.id) {
            try {
              const fd = new FormData();
              fd.append("file", proofFile);
              fd.append("description", proofFile.name);
              await uploadExpenseProof(created.id, fd);
              toast.success(
                (draft ? "Draft saved" : "Expense submitted") +
                  " with proof uploaded",
              );
            } catch {
              toast.success(draft ? "Draft saved" : "Expense submitted");
              toast.error("Proof upload failed — upload it from expense detail");
            }
          } else {
            toast.success(draft ? "Draft saved" : "Expense submitted");
          }
          setProofFile(null);
          reset();
          onDone?.();
        },
      },
    );
  };
 
  if (catLoading || travLoading)
    return <p className="text-gray-500">Loading...</p>;
 
  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
      }}
      className="space-y-3"
    >
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
          {travels
            ?.filter((t: any) => !t.cancelled)
            .map((t: any) => (
              <option key={t.id} value={t.id}>
                {t.title} — {t.destination}
              </option>
            ))}
        </select>
        {errors.travelId && (
          <p className="text-red-500 text-sm">{errors.travelId.message}</p>
        )}
        {maxPerDay != null && (
          <p className="text-xs text-gray-500 mt-1">
            Max per-day budget: ₹{maxPerDay}
          </p>
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
        {selectedCategory?.limit_in_inr != null && (
          <p className="text-xs text-gray-500 mt-1">
            Category limit: ₹{selectedCategory.limit_in_inr}
          </p>
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
 
      {/* Proof Document */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Proof Document (optional)
        </label>
        <input
          type="file"
          onChange={(e) => setProofFile(e.target.files?.[0] || null)}
          className="border p-2 w-full rounded text-sm"
        />
        {proofFile && (
          <p className="text-xs text-gray-500 mt-1">
            Selected: {proofFile.name} ({(proofFile.size / 1024).toFixed(1)} KB)
          </p>
        )}
      </div>
      <div className="flex gap-2">
        <button
          type="button"
          disabled={mutation.isPending}
          onClick={() => {
            const form = watch();
            if (
              !form.travelId ||
              !form.categoryId ||
              !form.amount ||
              !form.expenseDate
            ) {
              toast.error("Please fill all required fields");
              return;
            }
            doSubmit(form as ExpenseForm, false);
          }}
          className="bg-blue-600 hover:bg-blue-700 text-white py-2 rounded flex-1 disabled:opacity-50"
        >
          {mutation.isPending ? "Submitting..." : "Submit Expense"}
        </button>
        <button
          type="button"
          disabled={mutation.isPending}
          onClick={() => {
            const form = watch();
            if (
              !form.travelId ||
              !form.categoryId ||
              !form.amount ||
              !form.expenseDate
            ) {
              toast.error("Please fill all required fields");
              return;
            }
            doSubmit(form as ExpenseForm, true);
          }}
          className="border border-gray-300 text-gray-700 hover:bg-gray-50 py-2 rounded px-4 disabled:opacity-50"
        >
          Save Draft
        </button>
      </div>
    </form>
  );
}
 