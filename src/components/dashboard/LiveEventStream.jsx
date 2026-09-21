import React, { useState, useEffect } from 'react';
import { eventStreamService } from '../../services/websocket';

export default function LiveEventStream() {
  const [events, setEvents] = useState([
    { id: "EVT-9001", type: "TRANSACTION", account: "ACC-892140", amount: "₹50,000", riskScore: 94, status: "BLOCKED", ip: "185.220.101.5", timestamp: "13:32:05" },
    { id: "EVT-9000", type: "AUTH_FAILURE", account: "ACC-109923", amount: "-", riskScore: 58, status: "ALERT", ip: "185.220.101.5", timestamp: "13:31:40" },
    { id: "EVT-8999", type: "DEVICE_SWAP", account: "ACC-892140", amount: "-", riskScore: 72, status: "FLAGGED", ip: "45.142.120.12", timestamp: "13:29:02" },
    { id: "EVT-8998", type: "UPI_PAYMENT", account: "ACC-410293", amount: "₹1,250", riskScore: 12, status: "APPROVED", ip: "103.21.124.90", timestamp: "13:28:10" }
  ]);

  useEffect(() => {
    const unsubscribe = eventStreamService.subscribeTelemetry((newEvent) => {
      setEvents(prev => [newEvent, ...prev.slice(0, 15)]);
    });
    return () => unsubscribe();
  }, []);

  const getStatusBadge = (status, score) => {
    if (score >= 85 || status === 'BLOCKED') return 'badge-critical';
    if (score >= 60 || status === 'FLAGGED') return 'badge-high';
    if (score >= 30) return 'badge-medium';
    return 'badge-low';
  };

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 flex flex-col h-full">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center space-x-2">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping"></span>
          <h3 className="text-sm font-mono font-semibold text-slate-200 uppercase tracking-wider">LIVE KAFKA TELEMETRY STREAM</h3>
        </div>
        <span className="text-[10px] font-mono text-slate-400 bg-slate-900 px-2 py-0.5 rounded border border-slate-800">
          Real-time Ingestion
        </span>
      </div>

      <div className="flex-1 overflow-y-auto space-y-2 pr-1 max-h-[340px]">
        {events.map((evt) => (
          <div key={evt.id} className="p-3 rounded-lg bg-slate-900/60 border border-slate-800/80 hover:border-slate-700 transition-all text-xs font-mono flex items-center justify-between">
            <div className="flex items-center space-x-3 truncate">
              <span className="text-slate-500 text-[10px]">{evt.timestamp}</span>
              <span className="font-semibold text-slate-200">{evt.type}</span>
              <span className="text-slate-400 hidden sm:inline">{evt.account}</span>
              {evt.amount !== '-' && <span className="text-blue-400 font-bold">{evt.amount}</span>}
            </div>

            <div className="flex items-center space-x-2 shrink-0">
              <span className="text-slate-400 text-[11px] hidden md:inline">{evt.ip}</span>
              <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${getStatusBadge(evt.status, evt.riskScore)}`}>
                SCORE {evt.riskScore}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
