import React, { useState } from 'react';
import { Settings, Shield, Server, Database, Key, Bell, Cpu } from 'lucide-react';

export default function SettingsView() {
  const [kafkaEnabled, setKafkaEnabled] = useState(true);
  const [redisVelocity, setRedisVelocity] = useState(true);
  const [neo4jGraph, setNeo4jGraph] = useState(true);

  return (
    <div className="p-6 rounded-xl glass-panel border border-slate-800 space-y-6 font-mono">
      <div className="flex items-center space-x-3 border-b border-slate-800 pb-4">
        <Settings className="w-5 h-5 text-blue-400" />
        <div>
          <h2 className="text-base font-bold text-slate-100">SENTINEL-X PLATFORM CONFIGURATION</h2>
          <p className="text-xs text-slate-400">Microservice endpoint configuration, rule weights, and data pipelines</p>
        </div>
      </div>

      {/* Backend Integration Settings */}
      <div className="space-y-4">
        <h3 className="text-xs font-bold text-slate-300 uppercase tracking-wider">Phase 2 Spring Boot Microservice Drivers</h3>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="p-4 rounded-lg bg-slate-950/60 border border-slate-800 space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-200 flex items-center">
                <Server className="w-4 h-4 text-emerald-400 mr-2" /> Apache Kafka Cluster
              </span>
              <input type="checkbox" checked={kafkaEnabled} onChange={(e) => setKafkaEnabled(e.target.checked)} />
            </div>
            <span className="text-[10px] text-slate-500 block">Topic: sentinel.events.financial (Bootstrap: localhost:9092)</span>
          </div>

          <div className="p-4 rounded-lg bg-slate-950/60 border border-slate-800 space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-200 flex items-center">
                <Database className="w-4 h-4 text-rose-400 mr-2" /> Redis Velocity Engine
              </span>
              <input type="checkbox" checked={redisVelocity} onChange={(e) => setRedisVelocity(e.target.checked)} />
            </div>
            <span className="text-[10px] text-slate-500 block">Sliding windows: 1m, 5m, 1h (Redis: 6379)</span>
          </div>

          <div className="p-4 rounded-lg bg-slate-950/60 border border-slate-800 space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-200 flex items-center">
                <Cpu className="w-4 h-4 text-blue-400 mr-2" /> Neo4j Graph Database
              </span>
              <input type="checkbox" checked={neo4jGraph} onChange={(e) => setNeo4jGraph(e.target.checked)} />
            </div>
            <span className="text-[10px] text-slate-500 block">Entity Resolution Cypher Engine (bolt://localhost:7687)</span>
          </div>
        </div>
      </div>
    </div>
  );
}
