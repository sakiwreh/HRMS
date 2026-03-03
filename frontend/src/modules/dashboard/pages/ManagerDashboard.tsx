import { NavLink } from "react-router-dom";
import { useTeamExpenses, useTeamMembers, useTeamTravels } from "../hooks/useTeam";
import NavCard from "../components/NavCard";
import { getInitials } from "../../org/components/OrgChartView";

export default function ManagerDashboard() {
  const { data: members = [] } = useTeamMembers();
  const { data: travels = [] } = useTeamTravels();
  const { data: expenses = [] } = useTeamExpenses();

  const activeTravels = travels.filter((t: any) => !t.cancelled);

  const pendingExpenses = expenses.filter((e: any) => e.status === "PENDING");
  
  const totalExpenseAmount = expenses.reduce(
    (sum: number, e: any) => sum + (e.amount || 0),
    0
  );

  const cards = [
    {
      label: "Team Members",
      value: members.length,
      link: "/dashboard/team",
      color: "bg-blue-50 text-blue-700",
    },
    {
      label: "Active Travels",
      value: activeTravels.length,
      link: "/dashboard/team",
      color: "bg-green-50 text-green-700",
    },
    {
      label: "Pending Expenses",
      value: pendingExpenses.length,
      link: "/dashboard/team",
      color: "bg-yellow-50 text-yellow-700",
    },
    {
      label: "Total Expenses",
      value: `₹${totalExpenseAmount.toLocaleString()}`,
      // value: totalExpenseAmount,
      link: "/dashboard/team",
      color: "bg-purple-50 text-purple-700",
    },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-xl font-semibold">Manager Dashboard</h1>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {cards.map((c) => (
          <NavCard label={c.label} value={c.value} link={c.link} color={c.color} />
        ))}
      </div>

      <div className="bg-white rounded-xl shadow-lg">
        <div className="px-5 py-3 border-b flex justify-between items-center">
          <h2 className="font-semibold text-gray-700">Team Members</h2>
          <NavLink
            to="/dashboard/team"
            className="text-sm text-blue-600 hover:underline"
          >
            View All &rarr;
          </NavLink>
        </div>

        <div className="divide-y">
          {members.length === 0 ? (
            <div className="p-5 text-center text-gray-400">
              No team members
            </div>
          ) : (
            // Show 3 members
            members.slice(0, 3).map((m: any) => (
              <div key={m.id} className="px-5 py-3 flex items-center gap-3">
                {/* Initial or image */}
                {m.profilePath ? (
                    <img src={`${(window as any).API_BASE || "http://localhost:8080"}/employees/photo/${m.id}`} alt="avatar" className="w-10 h-10 rounded-full object-cover bg-gray-100" />
                ) : (
                    <div className="w-10 h-10 rounded-full object-cover bg-gray-100 flex items-center justify center">{getInitials(m.name)}</div>
                )}

                <div className="flex-1 min-w-0">
                  <p className="font-medium text-gray-800 text-sm truncate">
                    {m.name}
                  </p>
                  <p className="text-xs text-gray-500">
                    {m.designation} | {m.department}
                  </p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Pending Expenses */}
      {pendingExpenses.length > 0 && (
        <div className="bg-white rounded-xl shadow-lg">
          <div className="px-5 py-3 border-b flex justify-between items-center">
            <h2 className="font-semibold text-gray-700">
              Pending Team Expenses
            </h2>
            <NavLink
              to="/dashboard/team"
              className="text-sm text-blue-600 hover:underline"
            >
              View All &rarr;
            </NavLink>
          </div>
          <table className="w-full text-sm">
            <thead className="text-gray-500 text-left text-xs">
              <tr>
                <th className="px-5 py-2">Employee</th>
                <th className="px-5 py-2">Travel</th>
                <th className="px-5 py-2">Category</th>
                <th className="px-5 py-2 text-right">Amount</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {pendingExpenses.slice(0, 5).map((e: any) => (
                <tr key={e.id} className="hover:bg-gray-50">
                  <td className="px-5 py-2">{e.employeeName}</td>
                  <td className="px-5 py-2">{e.travelTitle || "-"}</td>
                  <td className="px-5 py-2">{e.category}</td>
                  <td className="px-5 py-2 text-right font-medium">
                    &#8377;{e.amount}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}