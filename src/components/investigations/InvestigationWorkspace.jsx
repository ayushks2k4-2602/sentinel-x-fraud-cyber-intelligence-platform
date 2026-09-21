import React from 'react';
import AttackTimeline from './AttackTimeline';
import EntityGraph from './EntityGraph';
import EvidencePanel from './EvidencePanel';
import AIInvestigator from './AIInvestigator';
import { ShieldAlert, AlertOctagon, User, CreditCard, Laptop, Globe } from 'lucide-react';

export default function InvestigationWorkspace({ incident }) {
  if (!incident) return null;

  return (
    <div className="space-y-6">
      {/* Primary Incident Header Card */}
      <div className="p-6 rounded-xl glass-panel border border-rose-900/60 bg-rose-950/20 shadow-glow-rose space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-rose-900/40 pb-4">
          <div className="flex items-center space-x-3">
            <div className="p-3 rounded-xl bg-rose-950 border border-rose-600/50 text-rose-400">
              <ShieldAlert className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="text-xs font-mono font-bold text-rose-400 bg-rose-950 px-2 py-0.5 rounded border border-rose-800">
                  {incident.severity} INCIDENT [{incident.incidentId}]
                </span>
                <span className="text-xs font-mono text-slate-400">Created {incident.createdTime}</span>
              </div>
              <h2 className="text-lg font-mono font-bold text-slate-100 mt-1">{incident.title}</h2>
            </div>
          </div>

          <div className="flex items-center space-x-4 bg-slate-950/80 p-3 rounded-xl border border-slate-800 font-mono text-xs">
            <div className="text-center px-2">
              <span className="text-[10px] text-slate-500 block">RISK SCORE</span>
              <span className="text-xl font-bold text-rose-400">{incident.riskScore} / 100</span>
            </div>
            <div className="h-8 w-px bg-slate-800"></div>
            <div className="text-center px-2">
              <span className="text-[10px] text-slate-500 block">STATUS</span>
              <span className="text-sm font-bold text-amber-400">{incident.status}</span>
            </div>
          </div>
        </div>

        {/* Affected Entity Metadata Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 font-mono text-xs">
          <div className="p-2.5 rounded bg-slate-950/60 border border-slate-800/80">
            <span className="text-slate-500 block text-[10px]">AFFECTED ACCOUNT</span>
            <span className="font-bold text-slate-200">{incident.account.id}</span>
            <span className="text-[10px] text-slate-400 block">{incident.account.owner}</span>
          </div>
          <div className="p-2.5 rounded bg-slate-950/60 border border-slate-800/80">
            <span className="text-slate-500 block text-[10px]">BASELINE LOCATION</span>
            <span className="font-bold text-slate-200">{incident.account.baselineLocation}</span>
          </div>
          <div className="p-2.5 rounded bg-slate-950/60 border border-slate-800/80">
            <span className="text-slate-500 block text-[10px]">ACCOUNT BALANCE</span>
            <span className="font-bold text-emerald-400">{incident.account.balance}</span>
          </div>
          <div className="p-2.5 rounded bg-slate-950/60 border border-slate-800/80">
            <span className="text-slate-500 block text-[10px]">CORRELATED IP</span>
            <span className="font-bold text-rose-400">185.220.101.5</span>
            <span className="text-[10px] text-rose-300 block">Tor Exit Node</span>
          </div>
        </div>
      </div>

      {/* Main Grid: Attack Sequence vs Graph & AI Assistant */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <AttackTimeline attackChain={incident.attackChain} />
        <EntityGraph graphData={incident.entityGraph} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <EvidencePanel incident={incident} />
        <AIInvestigator />
      </div>
    </div>
  );
}
