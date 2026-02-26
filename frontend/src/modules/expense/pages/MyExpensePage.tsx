import { useState, useMemo } from "react";
import { NavLink } from "react-router-dom";
import useMyExpenses from "../hooks/useMyExpenses";
import useDrafts from "../hooks/useDrafts";
import CreateExpenseForm from "../components/CreateExpenseForm";
import Modal from "../../../shared/components/Modal";
import type { ExpenseResponse } from "../api/expenseApi";
import { submitDraft } from "../api/expenseApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";
 
const statusBadge: Record<string, string> = {
  DRAFT: "bg-gray-100 text-gray-600",
  PENDING: "bg-yellow-100 text-yellow-700",
  APPROVED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};
 
export default function MyExpensesPage() {
  const { data: expenses = [], isLoading } = useMyExpenses();
  const { data: drafts = [], isLoading: draftsLoading } = useDrafts();
  const [open, setOpen] = useState(false);
  const [tab, setTab] = useState<"expenses" | "drafts">("expenses");
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const qc = useQueryClient();
 
  const submitMutation = useMutation({
    mutationFn: (id: number) => submitDraft(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["my-expenses"] });
      qc.invalidateQueries({ queryKey: ["my-drafts"] });
      toast.success("Draft submitted for review");
    },
  });
 
  // Group expenses by travel plan
  const grouped = useMemo(() => {
    let filtered = expenses;
    if (search) {
      const q = search.toLowerCase();
      filtered = filtered.filter(
        (e: ExpenseResponse) =>
          e.category?.toLowerCase().includes(q) ||
          e.description?.toLowerCase().includes(q) ||
          e.travelTitle?.toLowerCase().includes(q),
      );
    }
    if (statusFilter) {
      filtered = filtered.filter(
        (e: ExpenseResponse) => e.status === statusFilter,
      );
    }
 
    const groups: Record<string, ExpenseResponse[]> = {};
    filtered.forEach((e: ExpenseResponse) => {
      const key = e.travelTitle || "Unassigned";
      if (!groups[key]) groups[key] = [];
      groups[key].push(e);
    });
    return groups;
  }, [expenses, search, statusFilter]);
 
  if (isLoading || draftsLoading) return <div>Loading...</div>;
 
  const tabClass = (t: string) =>
    `px-4 py-2 border-b-2 text-sm font-medium transition ${
      tab === t
        ? "border-blue-600 text-blue-600"
        : "border-transparent text-gray-500 hover:text-blue-600"
    }`;
 
  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">My Expenses</h1>
        <button
          onClick={() => setOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
        >
          + Submit Expense
        </button>
      </div>
 
      {/* Tabs */}
      <div className="flex gap-4 border-b">
        <button
          className={tabClass("expenses")}
          onClick={() => setTab("expenses")}
        >
          Submitted ({expenses.length})
        </button>
        <button className={tabClass("drafts")} onClick={() => setTab("drafts")}>
          Drafts ({drafts.length})
        </button>
      </div>
 
      {tab === "expenses" && (
        <>
          {/* Search & Filter */}
          <div className="flex gap-3 items-center">
            <input
              type="text"
              placeholder="Search by category, description, travel..."
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
 
          {/* Grouped by travel */}
          <div className="space-y-6 max-h-[calc(100vh-300px)] overflow-y-auto">
            {Object.keys(grouped).length === 0 && (
              <div className="bg-white rounded-xl shadow p-6 text-center text-gray-400">
                No expenses found
              </div>
            )}
            {Object.entries(grouped).map(([travelTitle, items]) => (
              <div
                key={travelTitle}
                className="bg-white rounded-xl shadow overflow-hidden"
              >
                <div className="bg-gray-50 px-4 py-2 text-sm font-semibold text-gray-700 border-b">
                  {travelTitle}
                  <span className="text-xs text-gray-400 ml-2">
                    ({items.length} expense{items.length !== 1 ? "s" : ""})
                  </span>
                </div>
                <table className="w-full text-sm">
                  <thead className="text-gray-500 text-left text-xs">
                    <tr>
                      <th className="px-4 py-2">Date</th>
                      <th className="px-4 py-2">Category</th>
                      <th className="px-4 py-2">Description</th>
                      <th className="px-4 py-2 text-right">Amount</th>
                      <th className="px-4 py-2 text-center">Status</th>
                      <th className="px-4 py-2 text-center">Proofs</th>
                      <th className="px-4 py-2">Remarks</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y">
                    {items.map((e) => (
                      <tr key={e.id} className="hover:bg-gray-50">
                        <td className="px-4 py-2 whitespace-nowrap">
                          {new Date(e.expenseDate).toLocaleDateString()}
                        </td>
                        <td className="px-4 py-2">{e.category}</td>
                        <td className="px-4 py-2 text-gray-600">
                          {e.description || "—"}
                        </td>
                        <td className="px-4 py-2 text-right font-medium">
                          ₹{e.amount}
                        </td>
                        <td className="px-4 py-2 text-center">
                          <span
                            className={`text-xs px-2 py-0.5 rounded-full ${statusBadge[e.status]}`}
                          >
                            {e.status}
                          </span>
                        </td>
                        <td className="px-4 py-2 text-center">
                          <NavLink
                            to={`/dashboard/expenses/${e.id}`}
                            className="text-blue-600 hover:underline text-xs"
                          >
                            {e.proofCount > 0
                              ? `${e.proofCount} file${e.proofCount !== 1 ? "s" : ""}`
                              : "View"}
                          </NavLink>
                        </td>
                        <td className="px-4 py-2 text-gray-500 text-xs">
                          {e.remarks || "—"}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ))}
          </div>
        </>
      )}
 
      {tab === "drafts" && (
        <div className="bg-white rounded-xl shadow overflow-hidden">
          {drafts.length === 0 ? (
            <div className="p-6 text-center text-gray-400">No drafts</div>
          ) : (
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-600 text-left">
                <tr>
                  <th className="px-4 py-3">Date</th>
                  <th className="px-4 py-3">Travel</th>
                  <th className="px-4 py-3">Category</th>
                  <th className="px-4 py-3 text-right">Amount</th>
                  <th className="px-4 py-3">Description</th>
                  <th className="px-4 py-3">Edit</th>
                  <th className="px-4 py-3 text-center">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {drafts.map((d: ExpenseResponse) => (
                  <tr key={d.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 whitespace-nowrap">
                      {new Date(d.expenseDate).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3">{d.travelTitle || "—"}</td>
                    <td className="px-4 py-3">{d.category}</td>
                    <td className="px-4 py-3 text-right font-medium">
                      ₹{d.amount}
                    </td>
                    <td className="px-4 py-3 text-gray-600">
                      {d.description || "—"}
                    </td>
                    <td className="px-4 py-2 text-center">
                          <NavLink
                            to={`/dashboard/expenses/${d.id}`}
                            className="text-blue-600 hover:underline text-xs"
                          >Edit
                          </NavLink>
                        </td>
                    <td className="px-4 py-3 text-center">
                      <button
                        onClick={() => submitMutation.mutate(d.id)}
                        disabled={submitMutation.isPending}
                        className="text-blue-600 hover:underline text-sm disabled:opacity-50"
                      >
                        Submit
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
 
      <Modal title="Submit Expense" open={open} onClose={() => setOpen(false)}>
        <CreateExpenseForm onDone={() => setOpen(false)} />
      </Modal>
    </div>
  );
}