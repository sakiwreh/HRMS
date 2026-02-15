interface Props {
  collapsed: boolean;
  setCollapsed: (val: boolean) => void;
}
 
/**
 * Top header bar
 */
export default function Header({ collapsed, setCollapsed }: Props) {
  return (
    <header className="h-14 bg-white border-b flex items-center justify-between px-4">
      <button
        onClick={() => setCollapsed(!collapsed)}
        className="px-2 py-1 border rounded hover:bg-gray-100"
      >
        ☰
      </button>
 
      <div className="text-sm font-medium">HRMS Portal</div>
 
      <div className="text-sm">Profile</div>
    </header>
  );
}
 