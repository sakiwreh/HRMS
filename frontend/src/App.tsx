import { Outlet } from 'react-router-dom';
import './App.css'
import { useAuthInit } from './modules/auth/hooks/useAuthInit'

function App() {
  useAuthInit();
  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b px-6 py-3">
        <h1 className="text-lg font-semibold">HRMS</h1>
      </header>
 
      <main className="p-6">
        <Outlet />
      </main>
    </div>
  );
}

export default App
