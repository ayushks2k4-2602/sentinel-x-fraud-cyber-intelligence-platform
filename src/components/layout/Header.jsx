import React, { useState, useEffect } from 'react';
import { Search, Bell, Shield, Cpu, RefreshCw, Moon, Sun } from 'lucide-react';

export default function Header({ onOpenCommandPalette }) {
  const [time, setTime] = useState(new Date().toLocaleTimeString('en-US', { hour12: false }));
  const [darkMode, setDarkMode] = useState(true);

  useEffect(() => {
    const timer = setInterval(() => {
      setTime(new Date().toLocaleTimeString('en-US', { hour12: false }));
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const toggleTheme = () => {
    setDarkMode(!darkMode);
    if (darkMode) {
      document.documentElement.classList.remove('dark');
    } else {
      document.documentElement.classList.add('dark');
    }
  };

  return (
    <header className="h-16 bg-[#0A0E1A]/90 backdrop-blur-md border-b border-slate-800/80 sticky top-0 z-30 px-6 flex items-center justify-between">
      {/* Search & Command Palette Trigger */}
      <div className="flex items-center space-x-4">
        <button
          onClick={onOpenCommandPalette}
          className="flex items-center space-x-3 px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-slate-200 hover:border-slate-700 text-xs w-64 transition-all"
        >
          <Search className="w-3.5 h-3.5" />
          <span>Search entities, IPs, accounts...</span>
          <kbd className="ml-auto bg-slate-800 text-slate-300 font-mono text-[10px] px-1.5 py-0.5 rounded border border-slate-700">Ctrl K</kbd>
        </button>
      </div>

      {/* Center Status Indicators */}
      <div className="hidden md:flex items-center space-x-6 text-xs font-mono">
        <div className="flex items-center space-x-2 text-slate-400">
          <Shield className="w-4 h-4 text-emerald-400" />
          <span>GLOBAL RISK LEVEL: <strong className="text-emerald-400">NORMAL (24/100)</strong></span>
        </div>
        <div className="flex items-center space-x-2 text-slate-400">
          <Cpu className="w-4 h-4 text-blue-400" />
          <span>INGESTION: <strong className="text-slate-200">12,480 EPS</strong></span>
        </div>
      </div>

      {/* Right Controls */}
      <div className="flex items-center space-x-4">
        {/* Clock */}
        <div className="font-mono text-xs font-semibold text-blue-400 bg-blue-950/40 px-2.5 py-1 rounded border border-blue-900/60">
          {time} UTC
        </div>

        {/* Theme Toggle */}
        <button 
          onClick={toggleTheme}
          className="p-2 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-slate-200 transition-colors"
          title="Toggle Theme"
        >
          {darkMode ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-slate-300" />}
        </button>

        {/* Notifications */}
        <div className="relative">
          <button className="p-2 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-slate-200 transition-colors relative">
            <Bell className="w-4 h-4" />
            <span className="absolute top-1 right-1 w-2 h-2 rounded-full bg-rose-500 animate-ping"></span>
            <span className="absolute top-1 right-1 w-2 h-2 rounded-full bg-rose-500"></span>
          </button>
        </div>

        {/* User Profile */}
        <div className="flex items-center space-x-3 pl-3 border-l border-slate-800">
          <div className="w-8 h-8 rounded-full bg-blue-600/30 border border-blue-500/60 flex items-center justify-center font-mono text-xs font-bold text-blue-300">
            SA
          </div>
          <div className="hidden lg:block">
            <span className="block text-xs font-semibold text-slate-200">Analyst Lead</span>
            <span className="block text-[10px] text-slate-400 font-mono">SOC-TIER-3</span>
          </div>
        </div>
      </div>
    </header>
  );
}
