import type { EmployeeLookup } from "../../org/api/orgApi";

type Props = {
  employees: EmployeeLookup[];
  authorId: string;
  tag: string;
  fromDate: string;
  toDate: string;
  onAuthorIdChange: (value: string) => void;
  onTagChange: (value: string) => void;
  onFromDateChange: (value: string) => void;
  onToDateChange: (value: string) => void;
  onClear: () => void;
};

export default function SocialFilterBar({
  employees,
  authorId,
  tag,
  fromDate,
  toDate,
  onAuthorIdChange,
  onTagChange,
  onFromDateChange,
  onToDateChange,
  onClear,
}: Props) {
  return (
    <div className="bg-white rounded-xl shadow p-4 space-y-3">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-semibold text-gray-700">Filters</h2>
        <button
          type="button"
          onClick={onClear}
          className="text-xs text-blue-600 hover:underline"
        >
          Clear
        </button>
      </div>

      <div className="grid gap-3 md:grid-cols-4">
        <select
          value={authorId}
          onChange={(event) => onAuthorIdChange(event.target.value)}
          className="border rounded px-3 py-2 text-sm"
        >
          <option value="">All authors</option>
          {employees.map((employee) => (
            <option key={employee.id} value={employee.id}>
              {employee.name}
            </option>
          ))}
        </select>

        <input
          type="text"
          value={tag}
          onChange={(event) => onTagChange(event.target.value)}
          placeholder="Tag (example: teamwork)"
          className="border rounded px-3 py-2 text-sm"
        />

        <input
          type="date"
          value={fromDate}
          onChange={(event) => onFromDateChange(event.target.value)}
          className="border rounded px-3 py-2 text-sm"
        />

        <input
          type="date"
          value={toDate}
          onChange={(event) => onToDateChange(event.target.value)}
          className="border rounded px-3 py-2 text-sm"
        />
      </div>
    </div>
  );
}