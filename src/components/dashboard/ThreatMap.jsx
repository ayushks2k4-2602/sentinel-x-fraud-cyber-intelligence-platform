import React from 'react';
import { Radio, AlertOctagon, ShieldAlert, ArrowUpRight } from 'lucide-react';

export default function ThreatMap() {
  const topThreats = [
    { title: "Suspicious Tor IP Activity", count: 142, risk: "CRITICAL", vector: "Credential Stuffing" },
    { title: "Impossible Travel Logins", count: 89, risk: "HIGH", vector: "Account Compromise" },
    { title: "Rapid Beneficiary Additions", count: 54, risk: "HIGH", vector: "Fund Exfiltration" },
    { title: "High-Velocity Card Testing", count: 31, risk: "MEDIUM", vector: "Merchant Fraud" },
  ];

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 flex flex-col h-full">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-sm font-mono font-semibold text-slate-200">TOP ATTACK VECTORS & CAMPAIGNS</h3>
        <span className="text-xs text-slate-400 font-mono">Active Correlated Indicators</span>
      </div>

      <div className="space-y-3 flex-1">
        {topThreats.map((threat, idx) => (
          <div key={idx} className="p-3 rounded-lg bg-slate-950/40 border border-slate-800/80 flex items-center justify-between text-xs font-mono">
            <div className="flex items-center space-x-3">
              <div className={`p-1.5 rounded ${threat.risk === 'CRITICAL' ? 'bg-rose-950 text-rose-400' : 'bg-amber-950 text-amber-400'}`}>
                <ShieldAlert className="w-4 h-4" />
              </div>
              <div>
                <span className="block font-semibold text-slate-200">{threat.title}</span>
                <span className="text-[10px] text-slate-400">Vector: {threat.vector}</span>
              </div>
            </div>

            <div className="text-right">
              <span className="block text-sm font-bold text-slate-100">{threat.count}</span>
              <span className={`text-[9px] font-bold px-1.5 py-0.5 rounded ${
                threat.risk === 'CRITICAL' ? 'bg-rose-950 text-rose-300 border border-rose-800' : 'bg-amber-950 text-amber-300 border border-amber-800'
              }`}>
                {threat.risk}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
