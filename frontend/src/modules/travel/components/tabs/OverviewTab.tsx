import { useAppSelector } from "../../../../store/hooks";
import useCancelTravel from "../../hooks/useCancelTravel";
 
type Props = {
  travel: any;
};
 
export default function OverviewTab({ travel }: Props) {
  const user = useAppSelector((s) => s.auth.user);
  const cancelMutation = useCancelTravel();
 
  const handleCancel = () => {
    if (!confirm("Are you sure you want to cancel this travel plan?")) return;
    cancelMutation.mutate(travel.id);
  };
 
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-2 gap-6 text-sm">
        <div>
          <p className="text-gray-500">Title</p>
          <p className="font-medium">{travel.title}</p>
        </div>
 
        <div>
          <p className="text-gray-500">Destination</p>
          <p className="font-medium">{travel.destination}</p>
        </div>
 
        <div>
          <p className="text-gray-500">Departure</p>
          <p className="font-medium">{travel.departureDate}</p>
        </div>
 
        <div>
          <p className="text-gray-500">Return</p>
          <p className="font-medium">{travel.returnDate}</p>
        </div>
 
        {travel.maxPerDayAmount != null && (
          <div>
            <p className="text-gray-500">Max Per-Day Amount</p>
            <p className="font-medium">{travel.maxPerDayAmount}</p>
          </div>
        )}
 
        <div>
          <p className="text-gray-500">Status</p>
          <p
            className={`font-medium ${travel.cancelled ? "text-red-600" : "text-green-600"}`}
          >
            {travel.cancelled ? "Cancelled" : "Active"}
          </p>
        </div>
 
        <div className="col-span-2">
          <p className="text-gray-500">Description</p>
          <p className="font-medium">{travel.description}</p>
        </div>
      </div>
 
      {/* Cancel button — HR only, not already cancelled */}
      {user?.role === "HR" && !travel.cancelled && (
        <button
          onClick={handleCancel}
          disabled={cancelMutation.isPending}
          className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md disabled:opacity-50"
        >
          {cancelMutation.isPending ? "Cancelling..." : "Cancel Travel Plan"}
        </button>
      )}
    </div>
  );
}