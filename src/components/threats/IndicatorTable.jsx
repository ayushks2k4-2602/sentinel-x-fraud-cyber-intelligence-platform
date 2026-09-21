import React, { useState } from 'react';
import { Radio, Search, ShieldAlert, ExternalLink } from 'lucide-react';

export default function IndicatorTable({ indicators }) {
  const [filter, setFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  const filtered = indicators.filter(item => {
    const matchesFilter = filter === 'ALL' || item.type === filter || item.severity === filter;
    const matchesSearch = 
      item.value.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.category.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.campaign.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesFilter && matchesSearch;
  });

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 className="text-sm font-mono font-semibold text-slate-200 uppercase tracking-wider">THREAT INTELLIGENCE INDICATORS</h3>
          <p className="text-xs text-slate-400">Normalized IOC indicators across IP, Domain, Hash, and Malicious Infrastructure</p>
        </div>

        <div className="flex items-center space-x-3">
          <div className="relative">
            <Search className="w-3.5 h-3.5 absolute left-2.5 top-2.5 text-slate-400" />
            <input
              type="text"
              placeholder="Search IOC value or campaign..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="bg-slate-900 text-slate-200 placeholder-slate-500 text-xs pl-8 pr-3 py-1.5 rounded-lg border border-slate-800 focus:outline-none focus:border-blue-500 font-mono w-48"
            />
          </div>

          <div className="flex items-center space-x-1 bg-slate-900 p-1 rounded-lg border border-slate-800 text-xs font-mono">
            {['ALL', 'IP_ADDRESS', 'DOMAIN', 'FILE_HASH'].map((tab) => (
              <button
                key={tab}
                onClick={() => setFilter(tab)}
                className={`px-2.5 py-1 rounded transition-colors ${filter === tab ? 'bg-blue-600 text-white font-bold' : 'text-slate-400 hover:text-slate-200'}`}
              >
                {tab.replace('_', ' ')}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left font-mono text-xs border-collapse">
          <thead>
            <tr className="border-b border-slate-800 text-slate-400 bg-slate-950/40">
              <th className="p-3">INDICATOR VALUE</th>
              <th className="p-3">TYPE</th>
              <th className="p-3">CATEGORY / CAMPAIGN</th>
              <th className="p-3">MITRE TECHNIQUE</th>
              <th className="p-3">CONFIDENCE</th>
              <th className="p-3 text-right">LAST SEEN</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {filtered.map((item, idx) => (
              <tr key={idx} className="hover:bg-slate-800/40 transition-colors">
                <td className="p-3 font-semibold text-blue-400 truncate max-w-[220px]">{item.value}</td>
                <td className="p-3">
                  <span className="bg-slate-900 text-slate-300 text-[10px] px-2 py-0.5 rounded border border-slate-800">
                    {item.type}
                  </span>
                </td>
                <td className="p-3">
                  <span className="block font-semibold text-slate-200">{item.category}</span>
                  <span className="text-[10px] text-cyan-400">{item.campaign}</span>
                </td>
                <td className="p-3 text-slate-300 text-[11px]">{item.mitreTechnique}</td>
                <td className="p-3">
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                    item.confidence >= 90 ? 'badge-critical' : 'badge-high'
                  }`}>
                    {item.confidence}%
                  </span>
                </td>
                <td className="p-3 text-right text-slate-400 text-[11px]">{item.lastSeen}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
