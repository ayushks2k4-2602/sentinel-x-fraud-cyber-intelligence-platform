import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  ShieldAlert,
  LayoutDashboard,
  CreditCard,
  TrendingUp,
  Globe,
  Radio,
  MapPin,
  Bell,
  Search,
  Network,
  Settings,
  GitCommit,
  Activity,
  Layers
} from 'lucide-react';

export default function Sidebar({ collapsed, setCollapsed }) {
  const navSections = [
    {
      title: "OVERVIEW",
      items: [
        { name: "Command Center", path: "/", icon: LayoutDashboard }
      ]
    },
    {
      title: "FINANCIAL SECURITY",
      items: [
        { name: "Fraud Detection", path: "/fraud", icon: CreditCard },
        { name: "Transactions", path: "/fraud?tab=transactions", icon: Activity },
        { name: "Risk Analytics", path: "/fraud?tab=analytics", icon: TrendingUp }
      ]
    },
    {
      title: "CYBER INTELLIGENCE",
      items: [
        { name: "Threat Intelligence", path: "/threats", icon: Globe },
        { name: "Indicators", path: "/threats?tab=indicators", icon: Radio },
        { name: "Threat Map", path: "/threats?tab=map", icon: MapPin }
      ]
    },
    {
      title: "OPERATIONS",
      items: [
        { name: "Alert Center", path: "/alerts", icon: Bell, badge: "4 New" },
        { name: "Investigations", path: "/investigations", icon: Search, badge: "CRITICAL" },
        { name: "Attack Chains", path: "/investigations?tab=chains", icon: GitCommit }
      ]
    },
    {
      title: "ANALYTICS",
      items: [
        { name: "Security Analytics", path: "/investigations?tab=analytics", icon: Layers },
        { name: "Entity Graph", path: "/investigations?tab=graph", icon: Network }
      ]
    },
    {
      title: "SYSTEM",
      items: [
        { name: "Settings", path: "/settings", icon: Settings }
      ]
    }
  ];

  return (
    <aside className={`fixed top-0 left-0 bottom-0 z-40 bg-[#0A0E1A] border-r border-slate-800/80 transition-all duration-300 ${collapsed ? 'w-20' : 'w-64'} flex flex-col`}>
      {/* Brand Header */}
      <div className="h-16 flex items-center px-4 border-b border-slate-800/80 justify-between">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-lg bg-blue-600/20 border border-blue-500/50 flex items-center justify-center text-blue-400 shadow-glow-blue">
            <ShieldAlert className="w-5 h-5" />
          </div>
          {!collapsed && (
            <div>
              <span className="font-mono font-bold tracking-wider text-slate-100 text-lg">SENTINEL<span className="text-blue-500">-X</span></span>
              <span className="block text-[10px] font-mono text-slate-400 tracking-widest uppercase">Risk Intelligence</span>
            </div>
          )}
        </div>
      </div>

      {/* Navigation Links */}
      <div className="flex-1 overflow-y-auto py-4 px-3 space-y-6">
        {navSections.map((section, idx) => (
          <div key={idx} className="space-y-1">
            {!collapsed && (
              <h3 className="px-3 text-[10px] font-mono font-semibold text-slate-400 uppercase tracking-widest">
                {section.title}
              </h3>
            )}
            {section.items.map((item, itemIdx) => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={itemIdx}
                  to={item.path}
                  end={item.path === "/"}
                  className={({ isActive }) => `
                    flex items-center px-3 py-2.5 rounded-lg text-xs font-medium transition-all duration-200 group relative
                    ${isActive 
                      ? 'bg-blue-600/15 text-blue-400 border border-blue-500/40 shadow-glow-blue' 
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'}
                  `}
                >
                  <Icon className={`w-4 h-4 shrink-0 ${collapsed ? 'mx-auto' : 'mr-3'}`} />
                  {!collapsed && <span className="truncate">{item.name}</span>}
                  
                  {!collapsed && item.badge && (
                    <span className={`ml-auto text-[9px] font-mono font-bold px-1.5 py-0.5 rounded ${
                      item.badge.includes('CRITICAL') ? 'bg-rose-950 text-rose-300 border border-rose-600/40' : 'bg-slate-800 text-slate-300'
                    }`}>
                      {item.badge}
                    </span>
                  )}
                </NavLink>
              );
            })}
          </div>
        ))}
      </div>

      {/* Footer System Status */}
      {!collapsed && (
        <div className="p-3 border-t border-slate-800/80 bg-slate-950/40">
          <div className="flex items-center space-x-2 text-[11px] text-slate-400">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            <span className="font-mono">Kafka Pipeline Active</span>
          </div>
        </div>
      )}
    </aside>
  );
}
