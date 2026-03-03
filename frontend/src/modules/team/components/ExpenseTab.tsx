import { useState } from "react";
import type { ExpenseResponse } from "../../expense/api/expenseApi";

const statusBadge: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-700",
  APPROVED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};

export default function ExpensesTab({
  expenses,
  isLoading,
}: {
  expenses: ExpenseResponse[];
  isLoading: boolean;
}) {

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  if (isLoading) return <div>Loading team expenses...</div>;

  let filtered = expenses;
  if (search) {
    const q = search.toLowerCase();
    filtered = filtered.filter(
      (e) =>
        (e.employeeName ?? "").toLowerCase().includes(q) ||
        e.category.toLowerCase().includes(q) ||
        (e.travelTitle ?? "").toLowerCase().includes(q) ||
        (e.description ?? "").toLowerCase().includes(q)
    );
  }
  if (statusFilter) {
    filtered = filtered.filter((e) => e.status === statusFilter);
  }

  // Group by employee
  const grouped: Record<string, ExpenseResponse[]> = {};
  filtered.forEach((e) => {
    const key = e.employeeName || "Unknown";
    if (!grouped[key]) grouped[key] = [];
    grouped[key].push(e);
  });

  return (
    <div className="space-y-4">
      {/* Search & Filter */}
      <div className="flex gap-3 items-center">
        <input
          type="text"
          placeholder="Search by employee, category, travel..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="border rounded-md px-3 py-2 text-sm flex-1"
        />
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="border rounded-md px-3 py-2 text-sm"
        >
          <option value="">All Statuses</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
        </select>
      </div>

      {/* Grouped by employee */}
      <div className="space-y-6 max-h-[calc(100vh-350px)] overflow-y-auto">
        {Object.keys(grouped).length === 0 && (
          <div className="bg-white rounded-xl shadow p-6 text-center text-gray-400">
            No expenses found
          </div>
        )}
        {Object.entries(grouped).map(([empName, items]) => (
          <div
            key={empName}
            className="bg-white rounded-xl shadow overflow-hidden"
          >
            <div className="bg-gray-50 px-4 py-2 text-sm font-semibold text-gray-700 border-b">
              {empName}
              <span className="text-xs text-gray-400 ml-2">
                ({items.length} expense{items.length !== 1 ? "s" : ""})
              </span>
            </div>
            <table className="w-full text-sm">
              <thead className="text-gray-500 text-left text-xs">
                <tr>
                  <th className="px-4 py-2">Date</th>
                  <th className="px-4 py-2">Travel</th>
                  <th className="px-4 py-2">Category</th>
                  <th className="px-4 py-2">Description</th>
                  <th className="px-4 py-2 text-right">Amount</th>
                  <th className="px-4 py-2 text-center">Status</th>
                  <th className="px-4 py-2">Remarks</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {items.map((e) => (
                  <tr key={e.id} className="hover:bg-gray-50">
                    <td className="px-4 py-2 whitespace-nowrap">
                      {new Date(e.expenseDate).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-2">{e.travelTitle || "—"}</td>
                    <td className="px-4 py-2">{e.category}</td>
                    <td className="px-4 py-2 text-gray-600">
                      {e.description || "-"}
                    </td>
                    <td className="px-4 py-2 text-right font-medium">
                      ₹{e.amount}
                    </td>
                    <td className="px-4 py-2 text-center">
                      <span
                        className={`text-xs px-2 py-0.5 rounded-full ${statusBadge[e.status] || "bg-gray-100 text-gray-600"}`}
                      >
                        {e.status}
                      </span>
                    </td>
                    <td className="px-4 py-2 text-gray-500 text-xs">
                      {e.remarks || "-"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ))}
      </div>
    </div>
  );
}