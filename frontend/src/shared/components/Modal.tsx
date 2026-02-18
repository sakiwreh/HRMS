import React from "react";
 
type Props = {
  title: string;
  open: boolean;
  onClose: () => void;
  children: React.ReactNode;
};
 
export default function Modal({ title, open, onClose, children }: Props) {
  if (!open) return null;
 
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div
        className="absolute inset-0 bg-black/40"
        onClick={onClose}
      />
      <div className="relative bg-white rounded-xl shadow-xl w-[520px] max-w-[95%] p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold">{title}</h2>
 
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-red-500 text-xl"
          >
            X
          </button>
        </div>
 
        {children}
      </div>
    </div>
  );
}