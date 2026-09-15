import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/client";

const LEVEL_COLORS = {
  LOW: "#16a34a",
  MEDIUM: "#ca8a04",
  HIGH: "#ea580c",
  CRITICAL: "#dc2626",
};

export default function FraudAlerts() {
  const [alerts, setAlerts] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/fraud/alerts")
      .then((res) => setAlerts(res.data))
      .catch((err) => setError(err.response?.data?.error || "Could not load alerts (are you an Admin/Fraud Analyst?)"));
  }, []);

  return (
    <div>
      <h2>Fraud Alerts</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {alerts.map((a) => (
        <Link key={a.id} to={`/investigate/${a.transaction.id}`} style={{ textDecoration: "none", color: "inherit" }}>
          <div style={{ border: "1px solid #ddd", borderRadius: 8, padding: 15, marginBottom: 10, cursor: "pointer" }}>
            <strong>Alert #{a.id}</strong> — TXN-{a.transaction.id} — ₹{a.transaction.amount} —{" "}
            <span style={{ color: LEVEL_COLORS[a.riskLevel], fontWeight: "bold" }}>{a.riskLevel}</span>
            <p style={{ margin: "5px 0 0", color: "#666" }}>
              Status: {a.status} {a.reviewedBy ? `(reviewed by ${a.reviewedBy})` : "— click to investigate"}
            </p>
          </div>
        </Link>
      ))}
    </div>
  );
}