import React from 'react';
import { ShieldAlert, Key, Laptop, UserPlus, DollarSign, ArrowDown } from 'lucide-react';

export default function AttackTimeline({ attackChain }) {
  if (!attackChain) return null;

  const getIcon = (iconName) => {
    switch (iconName) {
      case 'ShieldAlert': return ShieldAlert;
      case 'Key': return Key;
      case 'Laptop': return Laptop;
      case 'UserPlus': return UserPlus;
      case 'DollarSign': return DollarSign;
      default: return ShieldAlert;
    }
  };

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      <div className="flex items-center justify-between border-b border-slate-800 pb-3">
        <h3 className="text-sm font-mono font-semibold text-slate-200 uppercase tracking-wider">ATTACK SEQUENCE TIMELINE</h3>
        <span className="text-xs font-mono text-rose-400 bg-rose-950/60 px-2 py-0.5 rounded border border-rose-900">
          5-Stage Correlated Chain
        </span>
      </div>

      <div className="space-y-3 font-mono">
        {attackChain.map((step, idx) => {
          const Icon = getIcon(step.icon);
          return (
            <React.Fragment key={step.step}>
              <div className="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800/80 hover:border-slate-700 transition-all flex items-start space-x-3">
                <div className={`p-2 rounded-lg shrink-0 mt-0.5 ${
                  step.severity === 'CRITICAL' ? 'bg-rose-950 text-rose-400 border border-rose-800' : 'bg-amber-950 text-amber-400 border border-amber-800'
                }`}>
                  <Icon className="w-4 h-4" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-slate-200">Step {step.step}: {step.title}</span>
                    <span className="text-[10px] text-slate-500">{step.time}</span>
                  </div>
                  <p className="text-xs text-slate-400 mt-1">{step.description}</p>
                  <div className="mt-2 flex items-center space-x-2">
                    <span className="text-[9px] px-1.5 py-0.5 rounded bg-slate-900 text-slate-300 border border-slate-800">
                      {step.stage}
                    </span>
                    <span className={`text-[9px] font-bold px-1.5 py-0.5 rounded ${
                      step.status === 'BLOCKED' ? 'bg-rose-950 text-rose-300' : 'bg-slate-800 text-slate-300'
                    }`}>
                      STATUS: {step.status}
                    </span>
                  </div>
                </div>
              </div>

              {idx < attackChain.length - 1 && (
                <div className="flex justify-center my-1">
                  <ArrowDown className="w-4 h-4 text-slate-600 animate-bounce" />
                </div>
              )}
            </React.Fragment>
          );
        })}
      </div>
    </div>
  );
}
