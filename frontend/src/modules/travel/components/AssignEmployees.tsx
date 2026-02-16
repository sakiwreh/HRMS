import { useState } from "react";
import { assignEmployees } from "../api/travelApi";
 
export default function AssignEmployees({ travelId }: { travelId: string }) {
  const [ids, setIds] = useState("");
 
  const assign = async () => {
    const list = ids.split(",").map(n => Number(n.trim()));
    await assignEmployees(travelId, list);
    setIds("");
  };
 
  return (
    <div className="mb-4">
      <h3 className="font-medium">Assign Employees</h3>
      <input
        value={ids}
        onChange={e => setIds(e.target.value)}
        placeholder="1,2,3"
        className="border p-1 mr-2"
      />
      <button onClick={assign} className="bg-green-500 text-white px-2">
        Assign
      </button>
    </div>
  );
}
 