import React, { useEffect, useState } from "react";
import api from "../api/client";
import { Line, Bar, Pie } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, ArcElement, Tooltip, Legend);

const RISK_COLORS = {
  LOW: "#16a34a",
  MEDIUM: "#ca8a04",
  HIGH: "#ea580c",
  CRITICAL: "#dc2626",
};

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [fraudTrend, setFraudTrend] = useState([]);
  const [riskDist, setRiskDist] = useState(null);
  const [volume, setVolume] = useState([]);
  const [byLocation, setByLocation] = useState(null);
  const [error, setError] = useState("");
  const role = localStorage.getItem("role");
  const isAnalystOrAdmin = role === "ADMIN" || role === "FRAUD_ANALYST";

  useEffect(() => {
    if (role === "ADMIN") {
      api.get("/admin/dashboard").then((res) => setStats(res.data)).catch(() => {});
    }
    if (isAnalystOrAdmin) {
      api.get("/analytics/fraud-trends").then((res) => setFraudTrend(res.data)).catch(() => {});
      api.get("/analytics/risk-distribution").then((res) => setRiskDist(res.data)).catch(() => {});
      api.get("/analytics/transactions").then((res) => setVolume(res.data)).catch(() => {});
      api.get("/analytics/fraud-by-location").then((res) => setByLocation(res.data)).catch((err) => {
        setError(err.response?.data?.error || "");
      });
    }
  }, [role, isAnalystOrAdmin]);

  return (
    <div>
      <h2>Fraud Intelligence Dashboard</h2>

      {!isAnalystOrAdmin && (
        <p>Welcome! Use "New Transaction" to simulate a transaction, or "History" to see your past ones.</p>
      )}

      {error && <p style={{ color: "red" }}>{error}</p>}

      {stats && (
        <div style={{ display: "grid", gridTemplateColumns: "repeat(2, 1fr)", gap: 15, marginBottom: 30 }}>
          <StatCard label="Total Transactions" value={stats.totalTransactions} />
          <StatCard label="Flagged Transactions" value={stats.flaggedTransactions} />
          <StatCard label="High Risk Transactions" value={stats.highRiskTransactions} />
          <StatCard label="Amount at Risk" value={`₹${stats.amountAtRisk.toLocaleString()}`} />
          <StatCard label="Total Users" value={stats.totalUsers} />
        </div>
      )}

      {isAnalystOrAdmin && (
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 30 }}>
          <ChartBox title="Fraud Trend (high-risk txns per day)">
            <Line
              data={{
                labels: fraudTrend.map((d) => d.date),
                datasets: [{
                  label: "Flagged transactions",
                  data: fraudTrend.map((d) => d.flaggedCount),
                  borderColor: "#dc2626",
                  backgroundColor: "#dc262633",
                  tension: 0.3,
                }],
              }}
            />
          </ChartBox>

          <ChartBox title="Risk Distribution">
            {riskDist && (
              <Pie
                data={{
                  labels: Object.keys(riskDist),
                  datasets: [{
                    data: Object.values(riskDist),
                    backgroundColor: Object.keys(riskDist).map((k) => RISK_COLORS[k]),
                  }],
                }}
              />
            )}
          </ChartBox>

          <ChartBox title="Transaction Volume (per day)">
            <Bar
              data={{
                labels: volume.map((d) => d.date),
                datasets: [{
                  label: "Transactions",
                  data: volume.map((d) => d.count),
                  backgroundColor: "#2563eb",
                }],
              }}
            />
          </ChartBox>

          <ChartBox title="Fraud by Location">
            {byLocation && (
              <Bar
                data={{
                  labels: Object.keys(byLocation),
                  datasets: [{
                    label: "High-risk transactions",
                    data: Object.values(byLocation),
                    backgroundColor: "#ea580c",
                  }],
                }}
              />
            )}
          </ChartBox>
        </div>
      )}
    </div>
  );
}

function StatCard({ label, value }) {
  return (
    <div style={{ border: "1px solid #ddd", borderRadius: 8, padding: 15 }}>
      <div style={{ fontSize: 13, color: "#666" }}>{label}</div>
      <div style={{ fontSize: 24, fontWeight: "bold" }}>{value}</div>
    </div>
  );
}

function ChartBox({ title, children }) {
  return (
    <div style={{ border: "1px solid #ddd", borderRadius: 8, padding: 15 }}>
      <h4 style={{ marginTop: 0 }}>{title}</h4>
      {children}
    </div>
  );
}