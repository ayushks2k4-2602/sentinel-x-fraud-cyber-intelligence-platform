import React, { useState, useEffect } from 'react';
import { Search, ShieldAlert, User, Laptop, CreditCard, ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function CommandPalette({ isOpen, onClose }) {
  const [query, setQuery] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const handleKeyDown = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        if (isOpen) onClose();
        else {
          // Open
          window.dispatchEvent(new CustomEvent('open-command-palette'));
        }
      } else if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const quickItems = [
    { title: "Account ACC-892140", type: "ACCOUNT", desc: "Vikram Sharma (Flagged Account)", action: () => navigate('/investigations') },
    { title: "IP 185.220.101.5", type: "THREAT_IP", desc: "Tor Exit Node (Operation DarkLoom)", action: () => navigate('/threats') },
    { title: "Txn TXN-902418", type: "TRANSACTION", desc: "₹50,000 IMPS Transfer (Blocked)", action: () => navigate('/fraud') },
    { title: "Alert ALT-2026-8801", type: "ALERT", desc: "Account Takeover & Fraud Chain", action: () => navigate('/alerts') },
  ];

  const filtered = query 
    ? quickItems.filter(i => i.title.toLowerCase().includes(query.toLowerCase()) || i.desc.toLowerCase().includes(query.toLowerCase()))
    : quickItems;

  return (
    <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-start justify-center pt-24 px-4">
      <div className="w-full max-w-xl bg-[#0F1626] border border-slate-700/80 rounded-xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-150">
        <div className="p-4 border-b border-slate-800 flex items-center space-x-3">
          <Search className="w-5 h-5 text-slate-400" />
          <input
            type="text"
            autoFocus
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Type account ID, IP, transaction, or alert..."
            className="bg-transparent text-slate-100 placeholder-slate-500 text-sm focus:outline-none w-full font-mono"
          />
          <kbd className="bg-slate-800 text-slate-400 text-xs px-2 py-0.5 rounded border border-slate-700">ESC</kbd>
        </div>

        <div className="max-h-80 overflow-y-auto p-2">
          {filtered.length === 0 ? (
            <div className="p-8 text-center text-slate-400 text-xs">
              No matching security entities found.
            </div>
          ) : (
            filtered.map((item, idx) => (
              <button
                key={idx}
                onClick={() => {
                  item.action();
                  onClose();
                }}
                className="w-full p-3 rounded-lg hover:bg-slate-800/80 flex items-center justify-between text-left transition-colors group"
              >
                <div>
                  <div className="flex items-center space-x-2">
                    <span className="font-mono text-xs font-semibold text-slate-200">{item.title}</span>
                    <span className="text-[10px] font-mono px-1.5 py-0.5 bg-blue-950 text-blue-300 rounded border border-blue-800/60">{item.type}</span>
                  </div>
                  <span className="text-xs text-slate-400 block mt-0.5">{item.desc}</span>
                </div>
                <ArrowRight className="w-4 h-4 text-slate-500 group-hover:text-blue-400 transition-colors" />
              </button>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
