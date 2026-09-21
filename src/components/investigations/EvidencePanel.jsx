import React from 'react';
import { ShieldAlert, CheckCircle, Cpu, FileText } from 'lucide-react';

export default function EvidencePanel({ incident }) {
  if (!incident) return null;

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      <div className="flex items-center justify-between border-b border-slate-800 pb-3">
        <div className="flex items-center space-x-2">
          <FileText className="w-5 h-5 text-amber-400" />
          <h3 className="text-sm font-mono font-semibold text-slate-200">EXPLAINABLE RISK EVIDENCE BREAKDOWN</h3>
        </div>
        <span className="font-mono text-xs font-bold text-rose-400">
          COMPOSITE SCORE: {incident.riskScore} / 100
        </span>
      </div>

      <div className="space-y-2 font-mono text-xs">
        {incident.evidenceBreakdown.map((item, idx) => (
          <div key={idx} className="p-3 rounded-lg bg-slate-950/80 border border-slate-800/80 flex items-center justify-between">
            <div>
              <span className="font-semibold text-slate-200 block">{item.factor}</span>
              <span className="text-[10px] text-slate-500">Source: {item.source}</span>
            </div>
            <span className="text-rose-400 font-bold text-sm bg-rose-950/40 px-2 py-0.5 rounded border border-rose-900">
              +{item.weight} pts
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}
