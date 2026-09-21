import React, { useEffect, useState } from 'react';
import AlertCenter from '../components/alerts/AlertCenter';
import { api } from '../services/api';
import { RefreshCw } from 'lucide-react';

export default function Alerts() {
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadAlerts() {
      try {
        const data = await api.getAlerts();
        setAlerts(data);
      } catch (err) {
        console.error("Failed to load alerts", err);
      } finally {
        setLoading(false);
      }
    }
    loadAlerts();
  }, []);

  const handleUpdateStatus = async (alertId, newStatus) => {
    await api.updateAlertStatus(alertId, newStatus);
    setAlerts(prev => prev.map(a => a.id === alertId ? { ...a, status: newStatus } : a));
  };

  if (loading) {
    return (
      <div className="h-96 flex items-center justify-center text-slate-400 font-mono text-xs">
        <RefreshCw className="w-5 h-5 animate-spin mr-2 text-rose-500" />
        Loading Incident Alert Center...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <AlertCenter alerts={alerts} onUpdateStatus={handleUpdateStatus} />
    </div>
  );
}
