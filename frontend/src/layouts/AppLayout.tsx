import { Outlet } from "react-router-dom";
import Sidebar from "../shared/components/layout/Sidebar";
import Header from "../shared/components/layout/Header";
import { useState } from "react";
 
export default function AppLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  return (
    <div className="flex h-screen bg-gray-100">
 
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)}/>
 
      <div className="flex-1 flex flex-col min-w-0">
        <Header onMenuClick={()=>setSidebarOpen(true)}/>
 
        <main className="p-4 md:p-6 overflow-auto flex-1">
          <Outlet />
        </main>
      </div>
    </div>
  );
}