import React, { useState } from 'react';
import { Network, User, Shield, Laptop, CreditCard, Radio, AlertOctagon } from 'lucide-react';

export default function EntityGraph({ graphData }) {
  const [selectedNode, setSelectedNode] = useState(graphData?.nodes[0]);

  if (!graphData) return null;

  const getNodeColor = (risk) => {
    switch (risk) {
      case 'CRITICAL': return 'bg-rose-950 border-rose-500 text-rose-300 shadow-glow-rose';
      case 'HIGH': return 'bg-amber-950 border-amber-500 text-amber-300 shadow-glow-amber';
      default: return 'bg-blue-950 border-blue-500 text-blue-300 shadow-glow-blue';
    }
  };

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4 flex flex-col h-full">
      <div className="flex items-center justify-between border-b border-slate-800 pb-3">
        <div className="flex items-center space-x-2">
          <Network className="w-5 h-5 text-blue-400" />
          <h3 className="text-sm font-mono font-semibold text-slate-200">NEO4J ENTITY RELATIONSHIP GRAPH</h3>
        </div>
        <span className="text-xs font-mono text-blue-400 bg-blue-950/60 px-2 py-0.5 rounded border border-blue-800">
          7 Nodes · 6 Edges
        </span>
      </div>

      {/* Graph Visualizer Canvas Area */}
      <div className="relative min-h-[320px] bg-[#070A12] border border-slate-800 rounded-xl p-4 flex flex-wrap items-center justify-center gap-4 overflow-hidden">
        {/* Background Grid Pattern */}
        <div className="absolute inset-0 opacity-10 bg-[radial-gradient(#3B82F6_1px,transparent_1px)] [background-size:16px_16px]"></div>

        {/* Nodes Display */}
        {graphData.nodes.map((node) => (
          <button
            key={node.id}
            onClick={() => setSelectedNode(node)}
            className={`relative z-10 p-3 rounded-xl border-2 transition-all font-mono text-xs flex flex-col items-center space-y-1 ${getNodeColor(node.risk)} ${
              selectedNode?.id === node.id ? 'scale-105 ring-2 ring-white/50' : 'hover:scale-102'
            }`}
          >
            <span className="text-[10px] font-bold tracking-wider opacity-80">{node.type}</span>
            <span className="font-bold text-slate-100 truncate max-w-[120px]">{node.label}</span>
          </button>
        ))}
      </div>

      {/* Selected Node Details */}
      {selectedNode && (
        <div className="p-3 rounded-lg bg-slate-950/80 border border-slate-800 font-mono text-xs space-y-1">
          <span className="text-slate-400 block text-[10px] uppercase">Selected Entity Topology Metadata</span>
          <div className="flex items-center justify-between text-slate-200 font-semibold">
            <span>{selectedNode.label}</span>
            <span className="text-rose-400">Risk Level: {selectedNode.risk}</span>
          </div>
        </div>
      )}
    </div>
  );
}
