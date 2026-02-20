import { useState, useMemo, useRef, useEffect } from "react";
import useEmployeeLookup from "../hooks/useEmployeeLookup";
import type { EmployeeLookup } from "../api/orgApi";
 
type Props = { onSelect: (empId: number) => void };
 
export default function EmployeeSearch({ onSelect }: Props) {
  const { data: employees, isLoading, isError } = useEmployeeLookup();
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);
 
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node))
        setOpen(false);
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);
 
  const filtered = useMemo(() => {
    if (!employees || !query.trim()) return [];
    const q = query.toLowerCase();
    return employees
      .filter(
        (e) =>
          e.name.toLowerCase().includes(q) ||
          e.email.toLowerCase().includes(q) ||
          e.designation?.toLowerCase().includes(q) ||
          e.department?.toLowerCase().includes(q),
      )
      .slice(0, 8);
  }, [employees, query]);
 
  const pick = (emp: EmployeeLookup) => {
    setQuery(emp.name);
    setOpen(false);
    onSelect(emp.id);
  };
 
  return (
    <div ref={ref} className="relative w-full max-w-md">
      <input
        type="text"
        value={query}
        onChange={(e) => {
          setQuery(e.target.value);
          setOpen(true);
        }}
        onFocus={() => query.trim() && setOpen(true)}
        placeholder={
          isLoading ? "Loading employees..." : "Search by name, email..."
        }
        disabled={isLoading}
        className="w-full border rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
      />
 
      {isError && (
        <p className="text-red-500 text-xs mt-1">
          Failed to load employees. Try refreshing.
        </p>
      )}
 
      {open && filtered.length > 0 && (
        <ul className="absolute z-20 mt-1 w-full bg-white border rounded-lg shadow-lg max-h-64 overflow-y-auto">
          {filtered.map((emp) => (
            <li key={emp.id}>
              <button
                type="button"
                onClick={() => pick(emp)}
                className="w-full text-left px-4 py-2.5 hover:bg-blue-50 transition"
              >
                <span className="font-medium text-gray-800">{emp.name}</span>
                {emp.designation && (
                  <span className="text-gray-400 text-xs ml-2">
                    {emp.designation}
                  </span>
                )}
                {emp.department && (
                  <span className="text-gray-400 text-xs ml-1">
                    · {emp.department}
                  </span>
                )}
              </button>
            </li>
          ))}
        </ul>
      )}
 
      {open && query.trim() && filtered.length === 0 && !isLoading && (
        <div className="absolute z-20 mt-1 w-full bg-white border rounded-lg shadow-lg px-4 py-3 text-sm text-gray-400">
          No employees match &ldquo;{query}&rdquo;
        </div>
      )}
    </div>
  );
}