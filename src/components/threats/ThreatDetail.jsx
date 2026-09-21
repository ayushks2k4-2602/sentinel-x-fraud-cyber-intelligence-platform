import React from 'react';
import { ShieldAlert, Globe, Server, Activity } from 'lucide-react';

export default function ThreatDetail({ indicator }) {
  if (!indicator) return null;

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      <div className="flex items-center justify-between border-b border-slate-800 pb-3">
        <div className="flex items-center space-x-2">
          <Globe className="w-5 h-5 text-cyan-400" />
          <h3 className="text-sm font-mono font-semibold text-slate-200">THREAT ENRICHMENT SPECS</h3>
        </div>
        <span className="font-mono text-xs font-bold text-cyan-400 bg-cyan-950/60 px-2.5 py-1 rounded border border-cyan-800">
          CONFIDENCE {indicator.confidence}%
        </span>
      </div>

      <div className="grid grid-cols-2 gap-4 text-xs font-mono">
        <div>
          <span className="text-slate-500 block">Indicator Value:</span>
          <span className="text-blue-400 font-bold truncate block">{indicator.value}</span>
        </div>
        <div>
          <span className="text-slate-500 block">Source Feed:</span>
          <span className="text-slate-200">{indicator.source}</span>
        </div>
        <div>
          <span className="text-slate-500 block">Campaign Name:</span>
          <span className="text-amber-400 font-bold">{indicator.campaign}</span>
        </div>
        <div>
          <span className="text-slate-500 block">MITRE Mapping:</span>
          <span className="text-emerald-400">{indicator.mitreTechnique}</span>
        </div>
      </div>
    </div>
  );
}
