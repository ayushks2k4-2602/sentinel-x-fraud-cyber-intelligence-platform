import React, { useEffect, useState } from 'react';
import SecurityOverview from '../components/dashboard/SecurityOverview';
import RiskTrendChart from '../components/dashboard/RiskTrendChart';
import LiveEventStream from '../components/dashboard/LiveEventStream';
import ThreatMap from '../components/dashboard/ThreatMap';
import RiskMetrics from '../components/dashboard/RiskMetrics';
import { api } from '../services/api';
import { RefreshCw } from 'lucide-react';

export default function Dashboard() {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const data = await api.getMetrics();
        setMetrics(data);
      } catch (err) {
        console.error("Failed to load dashboard metrics", err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="h-96 flex items-center justify-center text-slate-400 font-mono text-xs">
        <RefreshCw className="w-5 h-5 animate-spin mr-2 text-blue-500" />
        Loading SENTINEL-X Command Center Telemetry...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Overview Cards */}
      <SecurityOverview metrics={metrics} />

      {/* Main Charts & Live Stream Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <RiskTrendChart data={metrics?.riskTimeline} />
          <LiveEventStream />
        </div>

        <div className="space-y-6">
          <RiskMetrics distribution={metrics?.riskDistribution} />
          <ThreatMap />
        </div>
      </div>
    </div>
  );
}
