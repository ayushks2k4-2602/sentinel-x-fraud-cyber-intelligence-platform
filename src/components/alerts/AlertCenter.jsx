import React, { useState, useEffect } from 'react';
import { Bell, ShieldAlert, CheckCircle, Clock, ArrowRight, User } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { eventStreamService } from '../../services/websocket';

export default function AlertCenter({ alerts: initialAlerts, onUpdateStatus }) {
  const [alerts, setAlerts] = useState(initialAlerts || []);
  const [filter, setFilter] = useState('ALL');
  const navigate = useNavigate();

  useEffect(() => {
    setAlerts(initialAlerts || []);
  }, [initialAlerts]);

  useEffect(() => {
    const unsubscribe = eventStreamService.subscribeAlerts((newAlert) => {
      setAlerts(prev => [newAlert, ...prev]);
    });
    return () => unsubscribe();
  }, []);

  const filtered = alerts.filter(a => filter === 'ALL' || a.status === filter || a.severity === filter);

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 className="text-sm font-mono font-semibold text-slate-200 uppercase tracking-wider">SECURITY INCIDENT ALERT CENTER</h3>
          <p className="text-xs text-slate-400">SOC Alert triage workflow, severity assignment, and real-time STOMP alert stream</p>
        </div>

        {/* Status Filters */}
        <div className="flex items-center space-x-1 bg-slate-900 p-1 rounded-lg border border-slate-800 text-xs font-mono">
          {['ALL', 'NEW', 'ACKNOWLEDGED', 'INVESTIGATING', 'RESOLVED'].map((tab) => (
            <button
              key={tab}
              onClick={() => setFilter(tab)}
              className={`px-2.5 py-1 rounded transition-colors ${filter === tab ? 'bg-blue-600 text-white font-bold' : 'text-slate-400 hover:text-slate-200'}`}
            >
              {tab}
            </button>
          ))}
        </div>
      </div>

      <div className="space-y-3">
        {filtered.map((alert) => (
          <div key={alert.id} className="p-4 rounded-xl bg-slate-950/60 border border-slate-800/80 hover:border-slate-700 transition-all font-mono space-y-3">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800/60 pb-3">
              <div className="flex items-center space-x-3">
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                  alert.severity === 'CRITICAL' ? 'badge-critical' : 'badge-high'
                }`}>
                  {alert.severity} ({alert.riskScore}/100)
                </span>
                <span className="font-bold text-slate-200 text-sm">{alert.title}</span>
                <span className="text-xs text-slate-500 font-normal">[{alert.id}]</span>
              </div>

              {/* Status Selector */}
              <div className="flex items-center space-x-2">
                <span className="text-[11px] text-slate-400">Status:</span>
                <select
                  value={alert.status}
                  onChange={(e) => onUpdateStatus(alert.id, e.target.value)}
                  className="bg-slate-900 text-slate-200 border border-slate-700 text-xs px-2 py-1 rounded focus:outline-none focus:border-blue-500"
                >
                  <option value="NEW">NEW</option>
                  <option value="ACKNOWLEDGED">ACKNOWLEDGED</option>
                  <option value="INVESTIGATING">INVESTIGATING</option>
                  <option value="RESOLVED">RESOLVED</option>
                  <option value="FALSE_POSITIVE">FALSE_POSITIVE</option>
                </select>
              </div>
            </div>

            <p className="text-xs text-slate-300">{alert.summary}</p>

            <div className="flex flex-col sm:flex-row sm:items-center justify-between text-[11px] text-slate-400 gap-2 pt-1">
              <div className="flex items-center space-x-4">
                <span>Account: <strong className="text-slate-200">{alert.affectedAccount}</strong> ({alert.affectedUser})</span>
                <span>Assigned: <strong className="text-blue-400">{alert.assignedTo}</strong></span>
              </div>

              <button
                onClick={() => navigate('/investigations')}
                className="px-3 py-1 rounded bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs inline-flex items-center space-x-1 self-start sm:self-auto shadow-glow-blue transition-all"
              >
                <span>Open Investigation Workspace</span>
                <ArrowRight className="w-3.5 h-3.5 ml-1" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
