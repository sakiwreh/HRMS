import type { TeamMember } from "../../dashboard/api/teamApi";
import { getInitials } from "../../org/components/OrgChartView";

export default function MembersTab({
  members,
  isLoading,
}: {
  members: TeamMember[];
  isLoading: boolean;
}) {
  if (isLoading) return <div>Loading team members...</div>;

  if (members.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow p-6 text-center text-gray-400">
        No team members found
      </div>
    );
  }

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {members.map((m) => (
        <div
          key={m.id}
          className="bg-white rounded-xl shadow p-5 flex items-start gap-4"
        >
            {m.profilePath ? (
            <img src={`${(window as any).API_BASE || "http://localhost:8080"}/employees/photo/${m.id}`} alt="avatar" className="w-14 h-14 rounded-full object-cover bg-gray-100" />
          ) : (
            <div className="w-14 h-14 rounded-full object-cover bg-gray-100 flex items-center justify-center">{getInitials(m.name)}</div>
          )}

          <div className="flex-1 min-w-0">
            <p className="font-semibold text-gray-800 truncate">{m.name}</p>
            <p className="text-sm text-gray-500">{m.designation || "-"}</p>
            <p className="text-xs text-gray-400">{m.department || "-"}</p>
            {m.email && (
              <p className="text-xs text-blue-600 truncate mt-1">{m.email}</p>
            )}
            <div className="flex gap-4 mt-2 text-xs text-gray-400">
              {m.doj && (
                <span>
                  Joined: {new Date(m.doj).toLocaleDateString()}
                </span>
              )}
              {m.dob && (
                <span>
                  DOB: {new Date(m.dob).toLocaleDateString()}
                </span>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}