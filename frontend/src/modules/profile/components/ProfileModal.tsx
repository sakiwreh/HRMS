import { useState } from "react";
import Modal from "../../../shared/components/Modal";
import useProfile from "../hooks/useProfile";
import { updateMyProfile, uploadMyProfilePhoto } from "../api/profileApi";
import { queryClient } from "../../../app/providers";
 
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
  const [editing, setEditing] = useState(false);
  const [file,setFile] = useState<File | null>(null);
  const [form, setForm] = useState<any>({});
 
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
        <div>
          <div className="flex justify-between items-start mb-4">
            <div>
              <div className="text-lg font-semibold">{fullName}</div>
              <div className="text-sm text-gray-500">{profile.email}</div>
            </div>
            <div className="flex flex-col items-end gap-2">
              <div className="w-20 h-20 rounded-full overflow-hidden bg-gray-100">
                {profile.profilePath ? (
                  <img
                    src={`${(window as any).API_BASE || "http://localhost:8080"}/employees/photo/${profile.id}`}
                    alt="avatar"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-xl text-gray-600">
                    {(profile.firstName || "?").charAt(0)}
                  </div>
                )}
              </div>
              <div>
                <button
                  onClick={() => {
                    setEditing(!editing);
                    setForm(profile);
                  }}
                  className="text-sm text-blue-600"
                >
                  {editing ? "Cancel" : "Edit Profile"}
                </button>
              </div>
            </div>
          </div>

          {!editing ? (
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
          ) : (
            <div className="space-y-3">
              <div className="grid grid-cols-2 gap-4">
                <input
                  value={form.firstName || ""}
                  onChange={(e) =>
                    setForm({ ...form, firstName: e.target.value })
                  }
                  className="border rounded px-3 py-2"
                  placeholder="First name"
                />
                <input
                  value={form.lastName || ""}
                  onChange={(e) =>
                    setForm({ ...form, lastName: e.target.value })
                  }
                  className="border rounded px-3 py-2"
                  placeholder="Last name"
                />
                <input
                  value={form.designation || ""}
                  onChange={(e) =>
                    setForm({ ...form, designation: e.target.value })
                  }
                  className="border rounded px-3 py-2"
                  placeholder="Designation"
                />
                <input
                  value={form.dob || ""}
                  onChange={(e) => setForm({ ...form, dob: e.target.value })}
                  className="border rounded px-3 py-2"
                  placeholder="DOB (YYYY-MM-DD)"
                />
              </div>

              <div className="flex items-center gap-3">
                <input
                  type="file"
                  onChange={(e) => setFile(e.target.files?.[0] ?? null)}
                />
                <button
                  className="bg-blue-600 text-white px-3 py-1 rounded"
                  onClick={async () => {
                    try {
                      if (file) {
                        await uploadMyProfilePhoto(file);
                      }
                      await updateMyProfile(form);
                      queryClient.invalidateQueries({
                        queryKey: ["my-profile"],
                      });
                      setEditing(false);
                    } catch (err) {
                      // handled by interceptors
                    }
                  }}
                >
                  Save
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </Modal>
  );
}