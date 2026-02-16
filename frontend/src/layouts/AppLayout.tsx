import { Outlet } from "react-router-dom";
import Sidebar from "../shared/components/layout/Sidebar";
import Header from "../shared/components/layout/Header";
 
export default function AppLayout() {
  return (
    <div className="flex h-screen bg-gray-100">
 
      <Sidebar />
 
      <div className="flex-1 flex flex-col">
        <Header />
 
        <main className="p-6 overflow-auto flex-1">
          <Outlet />
        </main>
      </div>
 
    </div>
  );
}