import React from 'react';
import { Radio, AlertTriangle } from 'lucide-react';

export default function ThreatTimeline() {
  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      <h3 className="text-sm font-mono font-semibold text-slate-200">RECENT CAMPAIGN DISCOVERIES</h3>
      <div className="space-y-3 font-mono text-xs">
        <div className="p-3 rounded bg-slate-900/60 border border-slate-800">
          <span className="text-[10px] text-slate-500 block">2026-09-17 12:10:00</span>
          <span className="font-semibold text-rose-400 block">Phishing Infrastructure Ingested</span>
          <span className="text-slate-400 text-[11px]">Domain auth-verify-bank-update.com identified in 45 credential harvesting attempts.</span>
        </div>
        <div className="p-3 rounded bg-slate-900/60 border border-slate-800">
          <span className="text-[10px] text-slate-500 block">2026-09-16 18:40:11</span>
          <span className="font-semibold text-amber-400 block">Anubis Android Malware Hash Match</span>
          <span className="text-slate-400 text-[11px]">Banking Trojan payload matched against Qdrant Vector database knowledge base.</span>
        </div>
      </div>
    </div>
  );
}
