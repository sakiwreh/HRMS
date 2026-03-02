import { useForm } from "react-hook-form";
import { useCreateGameV2, useUpdateGameV2 } from "../hooks/useGames";
import type { GameV2Dto } from "../api/gameApi";

interface Props {
  game?: GameV2Dto;
  onDone: () => void;
}

interface FormValues {
  name: string;
  startHour: string;
  endHour: string;
  maxDurationMins: number;
  maxPlayersPerSlot: number;
  cancellationBeforeMins: number;
}

export default function GameFormV2({ game, onDone }: Props) {
  const create = useCreateGameV2();
  const update = useUpdateGameV2();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    defaultValues: game
      ? {
          name: game.name,
          startHour: game.startHour,
          endHour: game.endHour,
          maxDurationMins: game.maxDurationMins,
          maxPlayersPerSlot: game.maxPlayersPerSlot,
          cancellationBeforeMins: game.cancellationBeforeMins,
        }
      : {
          name: "",
          startHour: "09:00",
          endHour: "18:00",
          maxDurationMins: 30,
          maxPlayersPerSlot: 4,
          cancellationBeforeMins: 30,
        },
  });

  const onSubmit = (values: FormValues) => {
    const payload = {
      ...values,
      maxDurationMins: Number(values.maxDurationMins),
      maxPlayersPerSlot: Number(values.maxPlayersPerSlot),
      cancellationBeforeMins: Number(values.cancellationBeforeMins),
    };
    if (game) {
      update.mutate({ id: game.id, data: payload }, { onSuccess: onDone });
    } else {
      create.mutate(payload, { onSuccess: onDone });
    }
  };

  const loading = create.isPending || update.isPending;

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Game Name</label>
        <input
          {...register("name", { required: "Required" })}
          className="w-full border rounded-lg px-3 py-2"
        />
        {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Start Hour</label>
          <input type="time" {...register("startHour", { required: "Required" })} className="w-full border rounded-lg px-3 py-2" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">End Hour</label>
          <input type="time" {...register("endHour", { required: "Required" })} className="w-full border rounded-lg px-3 py-2" />
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Slot Duration (mins)</label>
          <input type="number" {...register("maxDurationMins", { required: "Required", min: 10 })} className="w-full border rounded-lg px-3 py-2" />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Max Players / Slot</label>
          <input type="number" {...register("maxPlayersPerSlot", { required: "Required", min: 1 })} className="w-full border rounded-lg px-3 py-2" />
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Cancellation Lead (mins)</label>
        <input type="number" {...register("cancellationBeforeMins", { required: "Required", min: 0 })} className="w-full border rounded-lg px-3 py-2" />
      </div>

      <button
        disabled={loading}
        className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg disabled:opacity-50"
      >
        {loading ? "Saving..." : game ? "Update Game" : "Create Game"}
      </button>
    </form>
  );
}