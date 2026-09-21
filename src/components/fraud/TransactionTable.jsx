import React, { useState } from 'react';
import { ShieldAlert, ExternalLink, Filter, Search } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function TransactionTable({ transactions }) {
  const [filter, setFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  const filtered = transactions.filter(t => {
    const matchesFilter = filter === 'ALL' || t.severity === filter || t.status === filter;
    const matchesSearch = 
      t.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.customerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.beneficiaryName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.ipAddress.includes(searchTerm);
    return matchesFilter && matchesSearch;
  });

  const getSeverityBadge = (severity) => {
    switch (severity) {
      case 'CRITICAL': return 'badge-critical';
      case 'HIGH': return 'badge-high';
      case 'MEDIUM': return 'badge-medium';
      default: return 'badge-low';
    }
  };

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4">
      {/* Table Header & Controls */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 className="text-sm font-mono font-semibold text-slate-200 uppercase tracking-wider">REAL-TIME FINANCIAL TRANSACTIONS</h3>
          <p className="text-xs text-slate-400">Card, IMPS, NEFT, and UPI payment events stream</p>
        </div>

        <div className="flex items-center space-x-3">
          {/* Search */}
          <div className="relative">
            <Search className="w-3.5 h-3.5 absolute left-2.5 top-2.5 text-slate-400" />
            <input
              type="text"
              placeholder="Filter transactions..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="bg-slate-900 text-slate-200 placeholder-slate-500 text-xs pl-8 pr-3 py-1.5 rounded-lg border border-slate-800 focus:outline-none focus:border-blue-500 font-mono w-48"
            />
          </div>

          {/* Filter Pills */}
          <div className="flex items-center space-x-1 bg-slate-900 p-1 rounded-lg border border-slate-800 text-xs font-mono">
            {['ALL', 'CRITICAL', 'HIGH', 'BLOCKED'].map((tab) => (
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
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left font-mono text-xs border-collapse">
          <thead>
            <tr className="border-b border-slate-800 text-slate-400 bg-slate-950/40">
              <th className="p-3">TRANSACTION ID</th>
              <th className="p-3">CUSTOMER / ACC</th>
              <th className="p-3">AMOUNT</th>
              <th className="p-3">BENEFICIARY</th>
              <th className="p-3">IP LOCATION</th>
              <th className="p-3">RISK SCORE</th>
              <th className="p-3">STATUS</th>
              <th className="p-3 text-right">ACTION</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="8" className="p-8 text-center text-slate-500">
                  No financial transactions match the selected filter criteria.
                </td>
              </tr>
            ) : (
              filtered.map((t) => (
                <tr key={t.id} className="hover:bg-slate-800/40 transition-colors">
                  <td className="p-3 font-semibold text-slate-200">{t.id}</td>
                  <td className="p-3">
                    <span className="block font-semibold text-slate-300">{t.customerName}</span>
                    <span className="text-[10px] text-slate-500">{t.accountId}</span>
                  </td>
                  <td className="p-3 font-bold text-blue-400">₹{t.amount.toLocaleString()}</td>
                  <td className="p-3">
                    <span className="block text-slate-300">{t.beneficiaryName}</span>
                    <span className="text-[10px] text-slate-500">{t.beneficiaryAccount}</span>
                  </td>
                  <td className="p-3 text-slate-400 text-[11px] truncate max-w-[160px]">{t.ipLocation}</td>
                  <td className="p-3">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${getSeverityBadge(t.severity)}`}>
                      {t.riskScore} / 100
                    </span>
                  </td>
                  <td className="p-3">
                    <span className={`text-[10px] font-bold ${t.status === 'BLOCKED' ? 'text-rose-400' : t.status === 'FLAGGED' ? 'text-amber-400' : 'text-emerald-400'}`}>
                      {t.status}
                    </span>
                  </td>
                  <td className="p-3 text-right">
                    <button
                      onClick={() => navigate('/investigations')}
                      className="p-1.5 rounded bg-blue-950 text-blue-400 hover:bg-blue-900 border border-blue-800/60 text-[10px] inline-flex items-center space-x-1"
                    >
                      <span>Investigate</span>
                      <ExternalLink className="w-3 h-3" />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
