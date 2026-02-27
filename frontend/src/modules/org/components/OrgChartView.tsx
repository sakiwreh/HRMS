import type { EmployeeNode, OrgChartResponse } from "../api/orgApi";
 
type Props = {
  data: OrgChartResponse;
  onNodeClick: (empId: number) => void;
};
 
function getInitials(name: string | null | undefined): string {
  if (!name || !name.trim()) return "?";
  return name
    .split(" ")
    .map((w) => w[0])
    .filter(Boolean)
    .slice(0, 2)
    .join("")
    .toUpperCase();
}
 
function NodeCard ({
  node,
  isSelected = false,
  onClick,
}: {
  node: EmployeeNode;
  isSelected?: boolean;
  onClick: () => void;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`
        text-left rounded-lg border-2 px-4 py-3 transition w-56 shrink-0
        focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2
        ${
          isSelected
            ? "border-blue-500 bg-blue-50 shadow-md"
            : "border-gray-200 bg-white hover:border-blue-300 hover:shadow"
        }
      `}
    >
      <div className="flex items-center gap-3">
        <div className={`w-9 h-9 rounded-full flex items-center justify-center text-sm font-semibold shrink-0 ${isSelected ? "bg-blue-500 text-white" : "bg-gray-200 text-gray-600"}`}>
          {node.profilePath ? (
            <img src={`${(window as any).API_BASE || "http://localhost:8080"}/employees/photo/${node.id}`} alt="avatar" className="w-full h-full object-cover rounded-full" />
          ) : (
            <div className="w-full h-full flex items-center justify-center">{getInitials(node.name)}</div>
          )}
       </div>
        <div className="min-w-0">
          <p className="font-medium text-gray-900 text-sm truncate">
            {node.name}
          </p>
          <p className="text-xs text-gray-500 truncate">
            {node.designation || "No designation"}
          </p>
          {node.department && (
            <p className="text-xs text-gray-400 truncate">{node.department}</p>
          )}
        </div>
      </div>
    </button>
  );
}
 
function Connector({ vertical = true }: { vertical?: boolean }) {
  if (vertical) {
    return (
      <div className="flex justify-center">
        <div className="w-px h-6 bg-gray-300" />
      </div>
    );
  }
  return (
    <div className="w-8 h-px bg-gray-300 self-center shrink-0" />
  );
}
 
export default function OrgChartView({ data, onNodeClick }: Props) {
  const { selected, chain, reports } = data;

 
  if (!selected) {
    return (
      <p className="text-center text-sm text-gray-400 py-8">
        No employee data available.
      </p>
    );
  }
 
  return (
    <section className="space-y-8">
      {chain.length > 0 && (
        <div>
          <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">
            Reporting Chain
          </h3>
          <div className="flex flex-col items-center" role="list" >
            {chain.map((node, i) => (
              <div key={node.id} className="flex flex-col items-center" role="listitem">
                {i > 0 && <Connector />}
                <NodeCard
                  node={node}
                  isSelected={node.id === selected.id}
                  onClick={() => onNodeClick(node.id)}
                />
              </div>
            ))}
          </div>
        </div>
      )}
 
      {chain.length === 0 && (
        <div className="text-center">
          <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">
            Selected Employee
          </h3>
          <div className="flex justify-center">
            <NodeCard
              node={selected}
              isSelected
              onClick={() => onNodeClick(selected.id)}
            />
          </div>
        </div>
      )}
      {reports.length > 0 && (
        <div>
          <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">
            Direct Reports ({reports.length})
          </h3>
 
          <div className="flex justify-center">
            <Connector />
          </div>
 
          <div
            className="flex flex-wrap justify-center gap-3 mt-2"
            role="list"
          >
            {reports.map((node) => (
              <div key={node.id} role="listitem">
                <NodeCard
                  node={node}
                  onClick={() => onNodeClick(node.id)}
                />
              </div>
            ))}
          </div>
        </div>
      )}
 
      {reports.length === 0 && (
        <p className="text-center text-sm text-gray-400">
          {selected.name} has no direct reports.
        </p>
      )}
    </section>
  );
}