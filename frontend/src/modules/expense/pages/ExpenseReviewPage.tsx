import { useState } from "react";
import { useAppSelector } from "../../../store/hooks";
import usePendingExpenses from "../hooks/usePendingExpenses";
import useFilteredExpenses from "../hooks/useFilteredExpense";
import useReviewExpense from "../hooks/useReviewExpense";
import type { ExpenseFilterParams, ExpenseResponse } from "../api/expenseApi";
 
const statusBadge: Record<ExpenseResponse["status"], string> = {
  PENDING: "bg-yellow-100 text-yellow-700",
  APPROVED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};
 
export default function ExpenseReviewPage() {
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role === "HR";
 
  /* ---- Filter state ---- */
  const [showFilter, setShowFilter] = useState(false);
  const [filters, setFilters] = useState<ExpenseFilterParams>({});
  const [activeFilters, setActiveFilters] = useState<ExpenseFilterParams>({});
  const hasActiveFilters = Object.values(activeFilters).some(
    (v) => v !== undefined && v !== ""
  );
 
  /* ---- Data ---- */
  const pending = usePendingExpenses(!hasActiveFilters);
  const filtered = useFilteredExpenses(activeFilters, hasActiveFilters && isHR);
  const { data, isLoading } = hasActiveFilters && isHR ? filtered : pending;
 
  /* ---- Review ---- */
  const reviewMutation = useReviewExpense();
  const [reviewingId, setReviewingId] = useState<number | null>(null);
  const [remarks, setRemarks] = useState("");
 
  const handleReview = (id: number, approved: boolean) => {
    reviewMutation.mutate(
      { id, data: { approved, remarks: remarks || undefined } },
      {
        onSuccess: () => {
          setReviewingId(null);
          setRemarks("");
        },
      }
    );
  };
 
  const applyFilters = () => {
    const clean: ExpenseFilterParams = {};
    if (filters.employeeId) clean.employeeId = filters.employeeId;
    if (filters.status) clean.status = filters.status;
    if (filters.travelId) clean.travelId = filters.travelId;
    if (filters.fromDate) clean.fromDate = filters.fromDate;
    if (filters.toDate) clean.toDate = filters.toDate;
    setActiveFilters(clean);
  };
 
  const clearFilters = () => {
    setFilters({});
    setActiveFilters({});
  };
 
  if (isLoading) return <div>Loading...</div>;
 
  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">Expense Review</h1>
        {isHR && (
          <button
            onClick={() => setShowFilter(!showFilter)}
            className="text-sm text-blue-600 hover:underline"
          >
            {showFilter ? "Hide Filters" : "Show Filters"}
          </button>
        )}
      </div>
 
      {/* Filter panel — HR only */}
      {showFilter && isHR && (
        <div className="bg-white rounded-xl shadow p-4 space-y-3">
          <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
            <input
              type="number"
              placeholder="Employee ID"
              className="border p-2 rounded"
              value={filters.employeeId ?? ""}
              onChange={(e) =>
                setFilters((f) => ({
                  ...f,
                  employeeId: e.target.value ? Number(e.target.value) : undefined,
                }))
              }
            />
            <select
              className="border p-2 rounded"
              value={filters.status ?? ""}
              onChange={(e) =>
                setFilters((f) => ({
                  ...f,
                  status: (e.target.value || undefined) as any,
                }))
              }
            >
              <option value="">All Statuses</option>
              <option value="PENDING">Pending</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
            </select>
            <input
              type="number"
              placeholder="Travel ID"
              className="border p-2 rounded"
              value={filters.travelId ?? ""}
              onChange={(e) =>
                setFilters((f) => ({
                  ...f,
                  travelId: e.target.value ? Number(e.target.value) : undefined,
                }))
              }
            />
            <input
              type="datetime-local"
              className="border p-2 rounded"
              value={filters.fromDate ?? ""}
              onChange={(e) =>
                setFilters((f) => ({ ...f, fromDate: e.target.value || undefined }))
              }
            />
            <input
              type="datetime-local"
              className="border p-2 rounded"
              value={filters.toDate ?? ""}
              onChange={(e) =>
                setFilters((f) => ({ ...f, toDate: e.target.value || undefined }))
              }
            />
          </div>
          <div className="flex gap-2">
            <button
              onClick={applyFilters}
              className="bg-blue-600 text-white px-4 py-1.5 rounded text-sm hover:bg-blue-700"
            >
              Apply
            </button>
            <button
              onClick={clearFilters}
              className="text-gray-600 px-4 py-1.5 rounded text-sm hover:bg-gray-100"
            >
              Clear
            </button>
          </div>
        </div>
      )}
 
      {/* Expense Table */}
      <div className="bg-white rounded-xl shadow overflow-hidden">
        {data?.length === 0 ? (
          <div className="p-6 text-center text-gray-400">
            {hasActiveFilters
              ? "No expenses match the filters"
              : "No pending expenses"}
          </div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600 text-left">
              <tr>
                <th className="px-4 py-3">ID</th>
                <th className="px-4 py-3">Employee</th>
                <th className="px-4 py-3">Date</th>
                <th className="px-4 py-3">Category</th>
                <th className="px-4 py-3">Description</th>
                <th className="px-4 py-3 text-right">Amount</th>
                <th className="px-4 py-3 text-center">Status</th>
                <th className="px-4 py-3 text-center">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {data?.map((e: ExpenseResponse) => (
                <tr key={e.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 text-gray-400">#{e.id}</td>
                  <td className="px-4 py-3">{e.employeeId}</td>
                  <td className="px-4 py-3 whitespace-nowrap">
                    {new Date(e.expenseDate).toLocaleDateString()}
                  </td>
                  <td className="px-4 py-3">{e.category}</td>
                  <td className="px-4 py-3 text-gray-600">
                    {e.description || "—"}
                  </td>
                  <td className="px-4 py-3 text-right font-medium">
                    ₹{e.amount}
                  </td>
                  <td className="px-4 py-3 text-center">
                    <span
                      className={`text-xs px-2 py-0.5 rounded-full ${statusBadge[e.status]}`}
                    >
                      {e.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-center">
                    {e.status === "PENDING" && isHR ? (
                      reviewingId === e.id ? (
                        <div className="space-y-2 text-left">
                          <input
                            type="text"
                            placeholder="Remarks (optional)"
                            className="border p-1 rounded w-full text-xs"
                            value={remarks}
                            onChange={(ev) => setRemarks(ev.target.value)}
                          />
                          <div className="flex gap-1">
                            <button
                              onClick={() => handleReview(e.id, true)}
                              disabled={reviewMutation.isPending}
                              className="bg-green-600 text-white px-2 py-1 rounded text-xs hover:bg-green-700 disabled:opacity-50"
                            >
                              Approve
                            </button>
                            <button
                              onClick={() => handleReview(e.id, false)}
                              disabled={reviewMutation.isPending}
                              className="bg-red-600 text-white px-2 py-1 rounded text-xs hover:bg-red-700 disabled:opacity-50"
                            >
                              Reject
                            </button>
                            <button
                              onClick={() => {
                                setReviewingId(null);
                                setRemarks("");
                              }}
                              className="text-gray-500 px-2 py-1 rounded text-xs hover:bg-gray-100"
                            >
                              Cancel
                            </button>
                          </div>
                        </div>
                      ) : (
                        <button
                          onClick={() => setReviewingId(e.id)}
                          className="text-blue-600 hover:underline text-xs"
                        >
                          Review
                        </button>
                      )
                    ) : e.status !== "PENDING" ? (
                      <span className="text-xs text-gray-400">
                        {e.reviewedBy ? `by ${e.reviewedBy}` : "—"}
                      </span>
                    ) : (
                      <span className="text-xs text-gray-400">—</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
 
      {reviewMutation.isError && (
        <p className="text-red-500 text-sm">
          {(reviewMutation.error as any)?.response?.data?.message ??
            "Review failed"}
        </p>
      )}
    </div>
  );
}
 