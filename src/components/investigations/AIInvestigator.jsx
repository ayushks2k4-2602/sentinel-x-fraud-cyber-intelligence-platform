import React, { useState } from 'react';
import { Bot, Send, Sparkles, RefreshCw, BookOpen } from 'lucide-react';
import { api } from '../../services/api';

export default function AIInvestigator() {
  const [messages, setMessages] = useState([
    {
      sender: "AI",
      text: "Hello Analyst. I have analyzed Incident INC-2026-9904. Account ACC-892140 experienced a correlated attack chain originating from German Tor Exit IP 185.220.101.5. Ask me anything regarding the evidence, attack timeline, or threat sources.",
      sources: ["SENTINEL-X RAG Engine"]
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || loading) return;

    const userQuery = input;
    setInput('');
    setMessages(prev => [...prev, { sender: "USER", text: userQuery }]);
    setLoading(true);

    try {
      const res = await api.queryAiInvestigator(userQuery);
      setMessages(prev => [...prev, {
        sender: "AI",
        text: res.answer,
        sources: res.sources
      }]);
    } catch (err) {
      console.error("AI Investigator Query failed", err);
    } finally {
      setLoading(false);
    }
  };

  const quickPrompts = [
    "Why was this transaction flagged?",
    "Show originating IP location & threat feed match",
    "Map this attack to MITRE ATT&CK techniques"
  ];

  return (
    <div className="p-5 rounded-xl glass-panel border border-slate-800 space-y-4 flex flex-col h-[480px]">
      <div className="flex items-center justify-between border-b border-slate-800 pb-3">
        <div className="flex items-center space-x-2">
          <Bot className="w-5 h-5 text-blue-400" />
          <h3 className="text-sm font-mono font-semibold text-slate-200 uppercase tracking-wider">AI INVESTIGATION ASSISTANT (RAG)</h3>
        </div>
        <span className="text-[10px] font-mono text-emerald-400 bg-emerald-950/60 px-2 py-0.5 rounded border border-emerald-900 flex items-center space-x-1">
          <Sparkles className="w-3 h-3 mr-1" /> Grounded Evidence Mode
        </span>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto space-y-3 pr-1 font-mono text-xs">
        {messages.map((msg, idx) => (
          <div key={idx} className={`p-3 rounded-lg ${
            msg.sender === 'AI' ? 'bg-slate-950/90 border border-slate-800 text-slate-200' : 'bg-blue-950/50 border border-blue-900 text-blue-200 ml-8'
          }`}>
            <div className="flex items-center justify-between font-bold text-[10px] text-slate-400 mb-1">
              <span>{msg.sender === 'AI' ? 'SENTINEL-X AI ASSISTANT' : 'SOC ANALYST'}</span>
            </div>
            <p className="whitespace-pre-line leading-relaxed">{msg.text}</p>
            {msg.sources && (
              <div className="mt-2 pt-2 border-t border-slate-800/80 flex flex-wrap gap-1 text-[9px] text-cyan-400">
                <BookOpen className="w-3 h-3 text-cyan-400 mr-1" />
                <span>Citations: {msg.sources.join(', ')}</span>
              </div>
            )}
          </div>
        ))}
        {loading && (
          <div className="p-3 rounded-lg bg-slate-950/80 border border-slate-800 text-slate-400 text-xs flex items-center space-x-2 font-mono">
            <RefreshCw className="w-4 h-4 animate-spin text-blue-400" />
            <span>Retrieving vector embeddings & vector DB playbooks...</span>
          </div>
        )}
      </div>

      {/* Quick Prompts */}
      <div className="flex flex-wrap gap-1.5 font-mono text-[10px]">
        {quickPrompts.map((prompt, idx) => (
          <button
            key={idx}
            onClick={() => setInput(prompt)}
            className="px-2.5 py-1 rounded bg-slate-900 border border-slate-800 text-slate-300 hover:text-white hover:border-slate-700 transition-colors"
          >
            {prompt}
          </button>
        ))}
      </div>

      {/* Input Bar */}
      <form onSubmit={handleSend} className="flex items-center space-x-2 pt-1 border-t border-slate-800">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Ask AI Investigator about risk evidence or attack chain..."
          className="flex-1 bg-slate-900 text-slate-100 placeholder-slate-500 text-xs px-3 py-2 rounded-lg border border-slate-800 focus:outline-none focus:border-blue-500 font-mono"
        />
        <button
          type="submit"
          disabled={loading || !input.trim()}
          className="px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-500 text-white font-mono text-xs font-bold shadow-glow-blue disabled:opacity-50 transition-all flex items-center space-x-1"
        >
          <span>Ask</span>
          <Send className="w-3.5 h-3.5 ml-1" />
        </button>
      </form>
    </div>
  );
}
