import AssignParticipants from "../AssignParticipants";
 
export default function ParticipantsTab({ travel }: any) {
  return (
    <div>
      <AssignParticipants travelId={travel.id}/>
    </div>
  );
}
 