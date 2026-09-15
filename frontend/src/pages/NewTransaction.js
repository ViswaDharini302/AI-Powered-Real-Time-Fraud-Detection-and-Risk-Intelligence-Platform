import React, { useState } from "react";
import api from "../api/client";

const LEVEL_COLORS = {
  LOW: "#16a34a",
  MEDIUM: "#ca8a04",
  HIGH: "#ea580c",
  CRITICAL: "#dc2626",
};

export default function NewTransaction() {
  const [form, setForm] = useState({ amount: "", transactionType: "ONLINE", location: "", deviceId: "" });
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  function update(field, value) {
    setForm({ ...form, [field]: value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setResult(null);
    try {
      const res = await api.post("/transactions", { ...form, amount: parseFloat(form.amount) });
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.error || "Transaction failed");
    }
  }

  return (
    <div>
      <h2>New Transaction</h2>
      <form onSubmit={handleSubmit} style={{ maxWidth: 350 }}>
        <input placeholder="Amount" type="number" value={form.amount} onChange={(e) => update("amount", e.target.value)} style={{ width: "100%", marginBottom: 10 }} />
        <select value={form.transactionType} onChange={(e) => update("transactionType", e.target.value)} style={{ width: "100%", marginBottom: 10 }}>
          <option value="ONLINE">Online</option>
          <option value="ATM">ATM</option>
          <option value="POS">POS</option>
          <option value="TRANSFER">Transfer</option>
        </select>
        <input placeholder="Location (e.g. Chennai)" value={form.location} onChange={(e) => update("location", e.target.value)} style={{ width: "100%", marginBottom: 10 }} />
        <input placeholder="Device ID (e.g. my-phone-1)" value={form.deviceId} onChange={(e) => update("deviceId", e.target.value)} style={{ width: "100%", marginBottom: 10 }} />
        {error && <p style={{ color: "red" }}>{error}</p>}
        <button type="submit" style={{ width: "100%" }}>Submit Transaction</button>
      </form>

      {result && (
        <div style={{ marginTop: 20, border: `2px solid ${LEVEL_COLORS[result.riskLevel]}`, borderRadius: 8, padding: 15, maxWidth: 400 }}>
          <h3>Risk Score: {result.riskScore} — <span style={{ color: LEVEL_COLORS[result.riskLevel] }}>{result.riskLevel}</span></h3>
          <p>ML Fraud Probability: {(result.fraudProbability * 100).toFixed(0)}%</p>
          <strong>Reasons:</strong>
          <ul>
            {result.reasons.filter(r => r.trim().length > 0).map((r, i) => <li key={i}>{r.replace(/^- /, "")}</li>)}
          </ul>
        </div>
      )}
    </div>
  );
}