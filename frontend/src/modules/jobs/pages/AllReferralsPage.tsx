import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import useAllReferrals from "../hooks/useAllReferrals";
import { updateReferralStatus } from "../api/jobApi";
 
const STATUSES = ["REFERRED", "SHORTLISTED", "INTERVIEW", "SELECTED", "REJECTED"];
 
const statusColor: Record<string, string> = {
  REFERRED: "bg-yellow-100 text-yellow-700",
  SHORTLISTED: "bg-blue-100 text-blue-700",
  INTERVIEW: "bg-purple-100 text-purple-700",
  SELECTED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};
 
export default function AllReferralsPage() {
  const { data: referrals, isLoading } = useAllReferrals();
  const qc = useQueryClient();
  const [editingId, setEditingId] = useState<number | null>(null);
 
  const mutation = useMutation({
    mutationFn: ({ id, status }: { id: number; status: string }) =>
      updateReferralStatus(id, status),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["referrals"] });
      setEditingId(null);
    },
  });
 
  if (isLoading) return <div>Loading referrals...</div>;
 
  return (
    <div className="space-y-4">
      <h1 className="text-xl font-semibold">All Referrals</h1>
 
      <div className="bg-white rounded-xl shadow overflow-hidden">
        {referrals?.length === 0 ? (
          <div className="p-6 text-center text-gray-400">No referrals yet</div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600 text-left">
              <tr>
                <th className="px-4 py-3 font-medium">Candidate</th>
                <th className="px-4 py-3 font-medium">Email</th>
                <th className="px-4 py-3 font-medium">Phone</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {referrals?.map((r: any) => (
                <tr key={r.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium text-gray-800">
                    {r.candidateFullName}
                  </td>
                  <td className="px-4 py-3 text-gray-500">{r.email || "—"}</td>
                  <td className="px-4 py-3 text-gray-500">{r.candidatePhoneNumber || "—"}</td>
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded-full ${statusColor[r.status] || "bg-gray-100 text-gray-600"}`}>
                      {r.status}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    {editingId === r.id ? (
                      <div className="flex gap-1 items-center">
                        <select
                          defaultValue={r.status}
                          onChange={(e) =>
                            mutation.mutate({ id: r.id, status: e.target.value })
                          }
                          className="border rounded p-1 text-sm"
                        >
                          {STATUSES.map((s) => (
                            <option key={s} value={s}>{s}</option>
                          ))}
                        </select>
                        <button
                          onClick={() => setEditingId(null)}
                          className="text-gray-400 hover:text-gray-600 text-xs ml-1"
                        >
                          Cancel
                        </button>
                      </div>
                    ) : (
                      <button
                        onClick={() => setEditingId(r.id)}
                        className="text-blue-600 hover:text-blue-800 text-sm"
                      >
                        Change
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}