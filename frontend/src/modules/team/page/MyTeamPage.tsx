import { useState } from "react";
import { useTeamExpenses, useTeamMembers, useTeamTravels } from "../../dashboard/hooks/useTeam";
import MembersTab from "../components/MembersTab";
import TravelsTab from "../components/TravelTab";
import ExpensesTab from "../components/ExpenseTab";

type Tab = "members" | "travels" | "expenses";

export default function MyTeamPage() {
  const [tab, setTab] = useState<Tab>("members");

  const { data: members = [], isLoading: membersLoading } = useTeamMembers();
  const { data: travels = [], isLoading: travelsLoading } = useTeamTravels();
  const { data: expenses = [], isLoading: expensesLoading } = useTeamExpenses();

  const tabClass = (t: Tab) =>
    `px-4 py-2 border-b-2 text-sm font-medium transition ${
      tab === t
        ? "border-blue-600 text-blue-600"
        : "border-transparent text-gray-500 hover:text-blue-600"
    }`;

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-semibold">My Team</h1>

      {/* Tabs selection */}
      <div className="flex gap-4 border-b">
        <button className={tabClass("members")} onClick={() => setTab("members")}>
          Members ({members.length})
        </button>
        <button className={tabClass("travels")} onClick={() => setTab("travels")}>
          Team Travels ({travels.length})
        </button>
        <button className={tabClass("expenses")} onClick={() => setTab("expenses")}>
          Team Expenses ({expenses.length})
        </button>
      </div>

      {/* Members Tab */}
      {tab === "members" && (
        <MembersTab members={members} isLoading={membersLoading} />
      )}

      {/* Travels Tab */}
      {tab === "travels" && (
        <TravelsTab travels={travels} isLoading={travelsLoading} />
      )}

      {/* Expenses Tab */}
      {tab === "expenses" && (
        <ExpensesTab expenses={expenses} isLoading={expensesLoading} />
      )}
    </div>
  );
}