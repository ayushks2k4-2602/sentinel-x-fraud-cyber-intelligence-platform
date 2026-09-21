import React, { useEffect, useState } from 'react';
import IndicatorTable from '../components/threats/IndicatorTable';
import ThreatDetail from '../components/threats/ThreatDetail';
import ThreatTimeline from '../components/threats/ThreatTimeline';
import { api } from '../services/api';
import { RefreshCw } from 'lucide-react';

export default function Threats() {
  const [indicators, setIndicators] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadIndicators() {
      try {
        const data = await api.getThreatIndicators();
        setIndicators(data);
      } catch (err) {
        console.error("Failed to load indicators", err);
      } finally {
        setLoading(false);
      }
    }
    loadIndicators();
  }, []);

  if (loading) {
    return (
      <div className="h-96 flex items-center justify-center text-slate-400 font-mono text-xs">
        <RefreshCw className="w-5 h-5 animate-spin mr-2 text-cyan-500" />
        Loading Threat Intelligence Knowledge Base...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <IndicatorTable indicators={indicators} />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ThreatDetail indicator={indicators[0]} />
        <ThreatTimeline />
      </div>
    </div>
  );
}
