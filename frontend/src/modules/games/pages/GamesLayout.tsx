import { useState } from "react";
import GamesPage from "./GamesPage";
import MyGameActivity from "./MyGameActivity";
 
const tabs = ["Games", "My Activity"] as const;
 
export default function GamesLayout() {
  const [tab, setTab] = useState<(typeof tabs)[number]>("Games");
 
  return (
    <div className="space-y-4">
      {/* TAB BAR */}
      <div className="flex gap-1 bg-gray-100 rounded-lg p-1 w-fit">
        {tabs.map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-1.5 rounded-md text-sm font-medium transition ${
              tab === t
                ? "bg-white text-blue-700 shadow-sm"
                : "text-gray-600 hover:text-gray-800"
            }`}
          >
            {t}
          </button>
        ))}
      </div>
 
      {/* TAB CONTENT */}
      {tab === "Games" ? <GamesPage /> : <MyGameActivity />}
    </div>
  );
}