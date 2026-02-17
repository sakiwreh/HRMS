import { useState } from "react";
import useMyExpenses from "../hooks/useMyExpenses";
import CreateExpenseForm from "../components/CreateExpenseForm";
import Modal from "../../../shared/components/Modal";
import type { ExpenseResponse } from "../api/expenseApi";
 
const statusBadge: Record<ExpenseResponse["status"], string> = {
  PENDING: "bg-yellow-100 text-yellow-700",
  APPROVED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};
 
export default function MyExpensesPage() {
  const { data, isLoading } = useMyExpenses();
  const [open, setOpen] = useState(false);
 
  if (isLoading) return <div>Loading...</div>;
 
  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-semibold">My Expenses</h1>
        <button
          onClick={() => setOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
        >
          + Submit Expense
        </button>
      </div>
 
      {/* Expense Table */}
      <div className="bg-white rounded-xl shadow overflow-hidden">
        {data?.length === 0 ? (
          <div className="p-6 text-center text-gray-400">
            No expenses found. Submit your first expense!
          </div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600 text-left">
              <tr>
                <th className="px-4 py-3">Date</th>
                <th className="px-4 py-3">Category</th>
                <th className="px-4 py-3">Description</th>
                <th className="px-4 py-3 text-right">Amount</th>
                <th className="px-4 py-3 text-center">Status</th>
                <th className="px-4 py-3">Remarks</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {data?.map((e: ExpenseResponse) => (
                <tr key={e.id} className="hover:bg-gray-50">
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
                  <td className="px-4 py-3 text-gray-500 text-xs">
                    {e.remarks || "—"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
 
      {/* Create Expense Modal */}
      <Modal
        title="Submit Expense"
        open={open}
        onClose={() => setOpen(false)}
      >
        <CreateExpenseForm />
      </Modal>
    </div>
  );
}
 