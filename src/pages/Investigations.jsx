import React, { useEffect, useState } from 'react';
import InvestigationWorkspace from '../components/investigations/InvestigationWorkspace';
import { api } from '../services/api';
import { RefreshCw } from 'lucide-react';

export default function Investigations() {
  const [incident, setIncident] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadIncident() {
      try {
        const data = await api.getIncidentDetails();
        setIncident(data);
      } catch (err) {
        console.error("Failed to load incident details", err);
      } finally {
        setLoading(false);
      }
    }
    loadIncident();
  }, []);

  if (loading) {
    return (
      <div className="h-96 flex items-center justify-center text-slate-400 font-mono text-xs">
        <RefreshCw className="w-5 h-5 animate-spin mr-2 text-rose-500" />
        Loading Correlated Investigation Workspace & Graph Topology...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <InvestigationWorkspace incident={incident} />
    </div>
  );
}
