import Modal from "../../../shared/components/Modal";
import useProfile from "../hooks/useProfile";
 
type Props = {
  open: boolean;
  onClose: () => void;
};
 
function Field({ label, value }: { label: string; value: string | null | undefined }) {
  return (
    <div>
      <label className="block text-xs font-medium text-gray-500 mb-1">{label}</label>
      <div className="border rounded-lg px-3 py-2 text-sm text-gray-800 bg-gray-50">
        {value || "-"}
      </div>
    </div>
  );
}
 
function formatDate(iso: string | null | undefined): string {
  if (!iso) return "-";
  try {
    return new Date(iso).toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  } catch {
    return iso;
  }
}
 
export default function ProfileModal({ open, onClose }: Props) {
  const { data: profile, isLoading, isError } = useProfile();
 
  const fullName = profile
    ? [profile.firstName, profile.middleName, profile.lastName].filter(Boolean).join(" ")
    : "";
 
  return (
    <Modal title="My Profile" open={open} onClose={onClose}>
      {isLoading && (
        <div className="space-y-3 animate-pulse">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="h-10 bg-gray-100 rounded-lg" />
          ))}
        </div>
      )}
 
      {isError && (
        <p className="text-red-500 text-sm py-4">
          Failed to load profile. Please try again later.
        </p>
      )}
 
      {profile && (
        <div className="grid grid-cols-2 gap-4">
          <div className="col-span-2">
            <Field label="Full Name" value={fullName} />
          </div>
          <Field label="Email" value={profile.email} />
          <Field label="Designation" value={profile.designation} />
          <Field label="Department" value={profile.department} />
          <Field label="Role" value={profile.role} />
          <Field label="Manager" value={profile.managerName} />
          <Field label="Date of Birth" value={formatDate(profile.dob)} />
          <Field label="Date of Joining" value={formatDate(profile.doj)} />
        </div>
      )}
    </Modal>
  );
}