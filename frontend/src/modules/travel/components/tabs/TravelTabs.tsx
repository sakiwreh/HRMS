import { useState } from "react";
import { useAppSelector } from "../../../../store/hooks";
import OverviewTab from "./OverviewTab";
import ParticipantsTab from "./ParticipantsTab";
import DocumentsTab from "./DocumentsTab";
 
type Props = {
  travel: any;
};
 
export default function TravelTabs({ travel }: Props) {
  const [active, setActive] = useState("overview");
  const user = useAppSelector((s) => s.auth.user);
  const isHR = user?.role?.toUpperCase() === "HR";
 
  const tabClass = (tab: string) =>
    `px-4 py-2 border-b-2 transition ${
      active === tab
        ? "border-blue-600 text-blue-600 font-semibold"
        : "border-transparent text-gray-500 hover:text-blue-600"
    }`;
 
  return (
    <div className="bg-white rounded shadow">
      <div className="flex gap-6 border-b px-6 pt-4">
        <button
          className={tabClass("overview")}
          onClick={() => setActive("overview")}
        >
          Overview
        </button>
        {isHR && (
          <button
            className={tabClass("participants")}
            onClick={() => setActive("participants")}
          >
            Participants
          </button>
        )}
        <button
          className={tabClass("documents")}
          onClick={() => setActive("documents")}
        >
          Documents
        </button>
      </div>
      <div className="p-6">
        {active === "overview" && <OverviewTab travel={travel} />}
        {active === "participants" && isHR && (
          <ParticipantsTab travel={travel} />
        )}
        {active === "documents" && <DocumentsTab travel={travel} />}
      </div>
    </div>
  );
}