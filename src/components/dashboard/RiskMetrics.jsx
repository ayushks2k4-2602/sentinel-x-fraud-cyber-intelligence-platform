import React from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';

export default function RiskMetrics({ distribution }) {
  const data = distribution || [
    { name: 'Low (0-30)', count: 214500, color: '#10B981' },
    { name: 'Medium (31-60)', count: 28400, color: '#3B82F6' },
    { name: 'High (61-85)', count: 4207, color: '#F59E0B' },
    { name: 'Critical (86-100)', count: 1284, color: '#F43F5E' },
  ];

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 flex flex-col h-full">
      <h3 className="text-sm font-mono font-semibold text-slate-200 mb-2">GLOBAL RISK SCORE DISTRIBUTION</h3>
      
      <div className="h-44 w-full relative">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={50}
              outerRadius={75}
              paddingAngle={4}
              dataKey="count"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} stroke="#070A12" strokeWidth={2} />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{ backgroundColor: '#0F1626', borderColor: '#334155', borderRadius: '8px', color: '#F8FAFC', fontSize: '11px', fontFamily: 'monospace' }}
            />
          </PieChart>
        </ResponsiveContainer>
      </div>

      <div className="grid grid-cols-2 gap-2 mt-2 font-mono text-xs">
        {data.map((item, idx) => (
          <div key={idx} className="flex items-center space-x-2 bg-slate-950/40 p-2 rounded border border-slate-800/80">
            <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: item.color }}></span>
            <div className="truncate">
              <span className="text-[10px] text-slate-400 block truncate">{item.name}</span>
              <span className="font-bold text-slate-200">{item.count.toLocaleString()}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
