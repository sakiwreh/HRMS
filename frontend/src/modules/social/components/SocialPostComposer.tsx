import { useState, type ChangeEvent, type FormEvent } from "react";
import type {
  SocialPostCreateInput,
  SocialPostCreateRequest,
  SocialVisibility,
} from "../api/socialApi";

type Props = {
  submitting: boolean;
  onSubmit: (input: SocialPostCreateInput) => Promise<void>;
};

export default function SocialPostComposer({ submitting, onSubmit }: Props) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [tagsInput, setTagsInput] = useState("");
  const [visibility, setVisibility] = useState<SocialVisibility>("ALL");
  const [images, setImages] = useState<File[]>([]);

  const reset = () => {
    setTitle("");
    setDescription("");
    setTagsInput("");
    setVisibility("ALL");
    setImages([]);
  };

  const parseTags = (value: string): string[] => {
    const unique = new Map<string, string>();
    value
      .split(",")
      .map((part) => part.trim())
      .filter((part) => part.length > 0)
      .forEach((part) => {
        unique.set(part.toLowerCase(), part);
      });
    return Array.from(unique.values());
  };

  const handleImagePick = (event: ChangeEvent<HTMLInputElement>) => {
    const selected = Array.from(event.target.files ?? []);
    if (selected.length === 0) return;
    const onlyImages = selected.filter((file) => file.type.startsWith("image/"));
    const merged = [...images, ...onlyImages].slice(0, 10);
    setImages(merged);
    event.target.value = "";
  };

  const removeImage = (index: number) => {
    setImages((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const payload: SocialPostCreateRequest = {
      title: title.trim(),
      description: description.trim(),
      visibility,
      tags: parseTags(tagsInput),
    };
    await onSubmit({
      payload,
      images
    });
    reset();
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow p-4 space-y-3">
      <h2 className="text-base font-semibold text-gray-800">Create Achievement Post</h2>

      <input
        type="text"
        placeholder="Title"
        value={title}
        onChange={(event) => setTitle(event.target.value)}
        maxLength={200}
        required
        className="w-full border rounded px-3 py-2 text-sm"
      />

      <textarea
        placeholder="Description"
        value={description}
        onChange={(event) => setDescription(event.target.value)}
        rows={4}
        required
        className="w-full border rounded px-3 py-2 text-sm"
      />

      <div className="grid gap-3 md:grid-cols-2">
        <input
          type="text"
          placeholder="Tags separated by comma"
          value={tagsInput}
          onChange={(event) => setTagsInput(event.target.value)}
          className="w-full border rounded px-3 py-2 text-sm"
        />

        <select
          value={visibility}
          onChange={(event) => setVisibility(event.target.value as SocialVisibility)}
          className="w-full border rounded px-3 py-2 text-sm"
        >
          <option value="ALL">All Employees</option>
          <option value="DEPARTMENT">Department</option>
          <option value="MANAGER_ONLY">Manager Only</option>
        </select>
      </div>

      <div className="space-y-2">
        <label className="block text-sm text-gray-700">
          Images (optional, up to 10)
        </label>
        <input
          type="file"
          accept="image/*"
          multiple
          onChange={handleImagePick}
          className="w-full border rounded px-3 py-2 text-sm bg-white"
        />
        {images.length > 0 && (
          <div className="grid gap-2 sm:grid-cols-2">
            {images.map((image, index) => (
              <div
                key={`${image.name}-${index}`}
                className="flex items-center justify-between rounded border px-2 py-1 text-xs text-gray-700"
              >
                <span className="truncate pr-2">{image.name}</span>
                <button
                  type="button"
                  onClick={() => removeImage(index)}
                  className="text-red-600 hover:underline"
                >
                  Remove
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="flex justify-end">
        <button
          type="submit"
          disabled={submitting || title.trim().length === 0 || description.trim().length === 0}
          className="bg-blue-600 hover:bg-blue-700 text-white text-sm px-4 py-2 rounded-lg disabled:opacity-50"
        >
          {submitting ? "Posting..." : "Post Achievement"}
        </button>
      </div>
    </form>
  );
}