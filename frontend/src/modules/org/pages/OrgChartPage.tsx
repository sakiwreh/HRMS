import { useState, useCallback } from "react";
import { useAppSelector } from "../../../store/hooks";
import useOrgChart from "../hooks/useOrgChart";
import EmployeeSearch from "../components/EmployeeSearch";
import OrgChartView from "../components/OrgChartView";
 
function getErrorMessage(error: unknown): string {
  if (error instanceof Error) return error.message;
  if (typeof error === "string") return error;
  return "Failed to load organization chart. Please try again.";
}
 
export default function OrgChartPage() {
  const user = useAppSelector((s) => s.auth.user);
 
  const [selectedEmpId, setSelectedEmpId] = useState<number | null>(
    user?.id ?? null
  );
 
  const { data, isLoading, isError, error, refetch } =
    useOrgChart(selectedEmpId);
 
  const handleSelect = useCallback((id: number) => setSelectedEmpId(id), []);
 
  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <h1 className="text-xl font-semibold">Organization Chart</h1>
        <EmployeeSearch onSelect={handleSelect} />
      </div>
 
      {/* Loading */}
      {isLoading && <h2>Loading...</h2>}
 
      {/* Error */}
      {isError && (
        <div
          role="alert"
          className="bg-red-50 border border-red-200 rounded-lg p-4 text-sm text-red-600 flex items-center justify-between gap-4"
        >
          <span>{getErrorMessage(error)}</span>
          <button
            type="button"
            onClick={() => refetch()}
            className="shrink-0 text-red-700 underline hover:text-red-900 font-medium focus:outline-none focus-visible:ring-2 focus-visible:ring-red-500"
          >
            Retry
          </button>
        </div>
      )}
 
      {/* No selection yet */}
      {!selectedEmpId && !isLoading && (
        <div className="bg-white rounded-xl shadow p-12 text-center text-gray-400">
          Search for an employee above to view their org chart.
        </div>
      )}
 
      {/* Chart */}
      {data && !isLoading && (
        <div className="bg-white rounded-xl shadow p-6 overflow-x-auto">
          <OrgChartView data={data} onNodeClick={handleSelect} />
        </div>
      )}
    </div>
  );
}