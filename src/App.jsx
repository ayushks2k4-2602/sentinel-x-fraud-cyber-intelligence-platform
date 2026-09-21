import React, { useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import Sidebar from './components/layout/Sidebar';
import Header from './components/layout/Header';
import CommandPalette from './components/layout/CommandPalette';
import Dashboard from './pages/Dashboard';
import Fraud from './pages/Fraud';
import Threats from './pages/Threats';
import Alerts from './pages/Alerts';
import Investigations from './pages/Investigations';
import Settings from './pages/Settings';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      staleTime: 1000 * 60 * 5, // 5 minutes
    },
  },
});

export default function App() {
  const [collapsed, setCollapsed] = useState(false);
  const [commandPaletteOpen, setCommandPaletteOpen] = useState(false);

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <div className="min-h-screen bg-[#070A12] text-slate-100 flex">
          {/* Main Navigation Sidebar */}
          <Sidebar collapsed={collapsed} setCollapsed={setCollapsed} />

          {/* Main App Container */}
          <div className={`flex-1 transition-all duration-300 ${collapsed ? 'ml-20' : 'ml-64'} flex flex-col min-h-screen`}>
            {/* Top Bar Header */}
            <Header onOpenCommandPalette={() => setCommandPaletteOpen(true)} />

            {/* Page View Body */}
            <main className="flex-1 p-6 max-w-[1600px] w-full mx-auto">
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/fraud" element={<Fraud />} />
                <Route path="/threats" element={<Threats />} />
                <Route path="/alerts" element={<Alerts />} />
                <Route path="/investigations" element={<Investigations />} />
                <Route path="/settings" element={<Settings />} />
              </Routes>
            </main>
          </div>

          {/* Command Palette Modal */}
          <CommandPalette isOpen={commandPaletteOpen} onClose={() => setCommandPaletteOpen(false)} />
        </div>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
