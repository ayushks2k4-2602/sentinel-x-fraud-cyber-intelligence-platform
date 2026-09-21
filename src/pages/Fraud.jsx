import React, { useEffect, useState } from 'react';
import TransactionTable from '../components/fraud/TransactionTable';
import FraudRiskCard from '../components/fraud/FraudRiskCard';
import FraudTimeline from '../components/fraud/FraudTimeline';
import { api } from '../services/api';
import { RefreshCw } from 'lucide-react';

export default function Fraud() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadTransactions() {
      try {
        const data = await api.getTransactions();
        setTransactions(data);
      } catch (err) {
        console.error("Failed to load transactions", err);
      } finally {
        setLoading(false);
      }
    }
    loadTransactions();
  }, []);

  if (loading) {
    return (
      <div className="h-96 flex items-center justify-center text-slate-400 font-mono text-xs">
        <RefreshCw className="w-5 h-5 animate-spin mr-2 text-blue-500" />
        Loading Financial Telemetry & Risk Scoring...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <TransactionTable transactions={transactions} />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <FraudRiskCard transaction={transactions[0]} />
        <FraudTimeline />
      </div>
    </div>
  );
}
