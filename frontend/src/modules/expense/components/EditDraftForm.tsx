import { useForm } from "react-hook-form";
import { useUpdateDraft } from "../hooks/useDrafts";
import useCategories from "../hooks/useCategories";
import useMyTravels from "../../travel/hooks/useMyTravels";
import toast from "react-hot-toast";
import type { ExpenseResponse } from "../api/expenseApi";

type DraftForm = {
  travelId: string;
  categoryId: string;
  amount: string;
  description: string;
  expenseDate: string;
};

interface Props {
  draft: ExpenseResponse;
  onDone: () => void;
}

export default function EditDraftForm({ draft, onDone }: Props) {
  const {
    register,
    watch,
    formState: { errors },
  } = useForm<DraftForm>({
    defaultValues: {
      travelId: draft.travelId ? String(draft.travelId) : "",
      categoryId: "",
      amount: String(draft.amount),
      description: draft.description || "",
      expenseDate: draft.expenseDate?.slice(0, 16) ?? "",
    },
  });

  const mutation = useUpdateDraft();
  const { data: categories, isLoading: catLoading } = useCategories();
  const { data: travels, isLoading: travLoading } = useMyTravels();

  // Resolve category ID from name
  const resolvedCategoryId = categories?.find(
    (c) => c.name === draft.category,
  )?.id;

  // Set categoryId default once categories load
  const categoryIdValue = watch("categoryId");
  const effectiveCategoryId =
    categoryIdValue || (resolvedCategoryId ? String(resolvedCategoryId) : "");

  const selectedCategory = categories?.find(
    (c) => String(c.id) === effectiveCategoryId,
  );

  const selectedTravelId = watch("travelId");
  const selectedTravel = travels?.find(
    (t: any) => String(t.id) === selectedTravelId,
  );
  const maxPerDay = selectedTravel?.maxPerDayAmount;

  const handleSave = () => {
    const form = watch();
    const catId = form.categoryId || effectiveCategoryId;
    if (!form.travelId || !catId || !form.amount || !form.expenseDate) {
      toast.error("Please fill all required fields");
      return;
    }

    if (
      selectedCategory?.limit_in_inr &&
      Number(form.amount) > selectedCategory.limit_in_inr
    ) {
      toast.error(
        `Amount exceeds the ${selectedCategory.name} category limit of ₹${selectedCategory.limit_in_inr}`,
      );
      return;
    }

    mutation.mutate(
      {
        id: draft.id,
        data: {
          travelId: Number(form.travelId),
          categoryId: Number(catId),
          amount: Number(form.amount),
          description: form.description || undefined,
          expenseDate: form.expenseDate,
          draft: true,
        },
      },
      { onSuccess: onDone },
    );
  };

  if (catLoading || travLoading)
    return <p className="text-gray-500">Loading...</p>;

  return (
    <form onSubmit={(e) => e.preventDefault()} className="space-y-3">
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
          defaultValue={effectiveCategoryId}
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

      <button
        type="button"
        disabled={mutation.isPending}
        onClick={handleSave}
        className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded disabled:opacity-50"
      >
        {mutation.isPending ? "Saving..." : "Save Draft"}
      </button>
    </form>
  );
}