import TileCard from "../components/TileCard";
import { useActiveJobcount, usePendingExpenseCount, useTodayMatches, useTotalPlans } from "../hooks/useDashboard";

export default function HrDashboard() {

  const {data: countTravel} = useTotalPlans();
  const {data: pendingCount} = usePendingExpenseCount();
  const {data: activeJobCount} = useActiveJobcount();
  const {data: todayMatches} = useTodayMatches();
  return <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-7xl mx-auto">
        <div className="text-3xl font-semibold text-center mb-8">HR Dashboard</div>

        {/* Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          
          {/* Travel count card */}
          <TileCard title="Travel count" value={countTravel}/>
          
          {/* Pending expense card */}
          <TileCard title="Pending Expenses" value={pendingCount}/>
          
          {/* Active jobs card */}
          <TileCard title="Active Job Openings" value={activeJobCount}/>
        </div>

        {/* Today's matches section */}
        <div className="bg-white p-6 rounded-xl shadow-lg mt-8">
          <div className="text-xl font-medium text-orange-600 mb-4">Today's Matches</div>
          {todayMatches && todayMatches.length > 0 ? (
            todayMatches.map((match:any, index:number) => (
              <div key={index} className="border-b py-4">
                <div className="text-lg font-semibold">Game: {match.gameName}</div>
                <div className="text-gray-600">
                  <span className="font-medium">Time:</span> {new Date(match.slotStart).toLocaleString()} - {new Date(match.slotEnd).toLocaleString()}
                </div>
                <div className="text-gray-600">
                  <span className="font-medium">Booked By:</span> {match.bookedByName}
                </div>
                <div className="text-gray-600">
                  <span className="font-medium">Participants:</span> {match.participantNames.join(", ")}
                </div>
              </div>
            ))
          ) : (
            <div className="text-gray-500">No upcoming matches today.</div>
          )}
        </div>
      </div>
    </div>
}