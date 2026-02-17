type Props = {
  travel: any;
};
 
export default function OverviewTab({ travel }: Props) {
  return (
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
 
      <div className="col-span-2">
        <p className="text-gray-500">Description</p>
        <p className="font-medium">{travel.description}</p>
      </div>
    </div>
  );
}
 