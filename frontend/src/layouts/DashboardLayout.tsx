import { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import Header from "./Header";
 
/**
* Main protected layout after login
* Contains sidebar + header + page content
*/
export default function DashboardLayout() {
  const [collapsed, setCollapsed] = useState(false);
 
  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <Sidebar collapsed={collapsed} setCollapsed={setCollapsed} />
 
      {/* Right side */}
      <div className="flex flex-col flex-1">
        <Header collapsed={collapsed} setCollapsed={setCollapsed} />
 
        {/* Page content */}
        <main className="p-4 overflow-auto flex-1">
          <Outlet />
        </main>
      </div>
    </div>
  );
}