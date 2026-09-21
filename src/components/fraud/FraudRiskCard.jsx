import React from 'react';
import { ShieldAlert, Cpu, CheckCircle } from 'lucide-react';

export default function FraudRiskCard({ transaction }) {
  if (!transaction) return null;

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      <div className="flex items-center justify-between border-b border-slate-800 pb-3">
        <div className="flex items-center space-x-2">
          <ShieldAlert className="w-5 h-5 text-rose-400" />
          <h3 className="text-sm font-mono font-semibold text-slate-200">FRAUD RISK DIAGNOSTICS</h3>
        </div>
        <span className="font-mono text-xs font-bold text-rose-400 bg-rose-950/60 px-2.5 py-1 rounded border border-rose-800">
          RISK SCORE {transaction.riskScore} / 100
        </span>
      </div>

      <div className="grid grid-cols-2 gap-4 text-xs font-mono">
        <div>
          <span className="text-slate-500 block">Transaction ID:</span>
          <span className="text-slate-200 font-bold">{transaction.id}</span>
        </div>
        <div>
          <span className="text-slate-500 block">Amount / Type:</span>
          <span className="text-blue-400 font-bold">₹{transaction.amount.toLocaleString()} ({transaction.type})</span>
        </div>
        <div>
          <span className="text-slate-500 block">Device Fingerprint:</span>
          <span className="text-slate-300 truncate block">{transaction.deviceFingerprint}</span>
        </div>
        <div>
          <span className="text-slate-500 block">IP Reputation:</span>
          <span className="text-rose-400 truncate block">{transaction.ipLocation}</span>
        </div>
      </div>

      <div className="space-y-2 pt-2 border-t border-slate-800">
        <span className="text-xs font-mono font-semibold text-slate-300 block">XGBoost & Rule Evidence Breakdown:</span>
        {transaction.evidence.map((item, idx) => (
          <div key={idx} className="flex items-center justify-between p-2 rounded bg-slate-900/60 text-xs font-mono">
            <span className="text-slate-300">{item.factor}</span>
            <span className="text-rose-400 font-bold">+{item.weight} pts</span>
          </div>
        ))}
      </div>
    </div>
  );
}
