import React from 'react';
import { Activity, ShieldAlert, Radio, AlertTriangle, ArrowUpRight } from 'lucide-react';

export default function SecurityOverview({ metrics }) {
  const cards = [
    {
      title: "Events Processed",
      value: metrics?.eventsProcessed ? metrics.eventsProcessed.toLocaleString() : "248,391",
      change: "+12.4% vs last hour",
      icon: Activity,
      color: "text-blue-400",
      bg: "bg-blue-950/40",
      border: "border-blue-900/50"
    },
    {
      title: "Fraud Alerts",
      value: metrics?.fraudAlerts ? metrics.fraudAlerts.toLocaleString() : "1,284",
      change: "+4.1% high-value flags",
      icon: ShieldAlert,
      color: "text-amber-400",
      bg: "bg-amber-950/40",
      border: "border-amber-900/50"
    },
    {
      title: "Cyber Threats",
      value: metrics?.cyberThreats ? metrics.cyberThreats.toLocaleString() : "427",
      change: "3 active campaigns",
      icon: Radio,
      color: "text-cyan-400",
      bg: "bg-cyan-950/40",
      border: "border-cyan-900/50"
    },
    {
      title: "Critical Risk Incidents",
      value: metrics?.criticalIncidents ? metrics.criticalIncidents.toLocaleString() : "18",
      change: "Action required",
      icon: AlertTriangle,
      color: "text-rose-400",
      bg: "bg-rose-950/40",
      border: "border-rose-900/50"
    }
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card, idx) => {
        const Icon = card.icon;
        return (
          <div key={idx} className={`p-4 rounded-xl glass-panel border ${card.border} hover:border-blue-500/40 transition-all`}>
            <div className="flex items-center justify-between">
              <span className="text-xs font-mono font-medium text-slate-400 uppercase tracking-wider">{card.title}</span>
              <div className={`p-2 rounded-lg ${card.bg} ${card.color}`}>
                <Icon className="w-4 h-4" />
              </div>
            </div>
            <div className="mt-3">
              <span className="text-2xl font-mono font-bold text-slate-100">{card.value}</span>
            </div>
            <div className="mt-2 flex items-center text-[11px] font-mono text-slate-400">
              <ArrowUpRight className="w-3 h-3 text-emerald-400 mr-1" />
              <span>{card.change}</span>
            </div>
          </div>
        );
      })}
    </div>
  );
}
