import React from 'react';
import { Clock, ShieldAlert, AlertTriangle } from 'lucide-react';

export default function FraudTimeline() {
  const timelineEvents = [
    { time: "13:32:05", title: "High-Value Transfer Blocked", desc: "₹50,000 transfer to Global Pay Corp blocked by Sentinel-X Risk Engine.", type: "CRITICAL" },
    { time: "13:30:15", title: "Rapid Beneficiary Addition", desc: "Beneficiary BEN-90214 added from IP 185.220.101.5.", type: "HIGH" },
    { time: "13:29:02", title: "Unrecognized Device Swap", desc: "Device fingerprint dev_89a2b1c4 (Linux/Firefox) registered.", type: "HIGH" },
    { time: "13:27:44", title: "Auth Compromise Success", desc: "Session authenticated from Frankfurt Tor exit node.", type: "CRITICAL" },
  ];

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      <h3 className="text-sm font-mono font-semibold text-slate-200">RECENT DETECTED FRAUD SEQUENCES</h3>
      <div className="space-y-4 border-l-2 border-slate-800 pl-4 ml-2">
        {timelineEvents.map((evt, idx) => (
          <div key={idx} className="relative group font-mono">
            <div className={`absolute -left-[21px] top-1 w-3 h-3 rounded-full border-2 border-slate-900 ${
              evt.type === 'CRITICAL' ? 'bg-rose-500' : 'bg-amber-500'
            }`}></div>
            <span className="text-[10px] text-slate-500 block">{evt.time}</span>
            <span className="text-xs font-semibold text-slate-200 block">{evt.title}</span>
            <p className="text-xs text-slate-400 mt-0.5">{evt.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
