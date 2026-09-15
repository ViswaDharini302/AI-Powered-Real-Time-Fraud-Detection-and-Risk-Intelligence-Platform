import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/client";

const LEVEL_COLORS = {
  LOW: "#16a34a",
  MEDIUM: "#ca8a04",
  HIGH: "#ea580c",
  CRITICAL: "#dc2626",
};

export default function TransactionInvestigation() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [error, setError] = useState("");

  function load() {
    api.get(`/transactions/${id}/investigate`)
      .then((res) => setData(res.data))
      .catch((err) => setError(err.response?.data?.error || "Could not load investigation details"));
  }

  useEffect(() => { load(); }, [id]);

  async function decide(decision) {
    // Find the open alert for this transaction, then review it.
    try {
      const alerts = await api.get("/fraud/alerts");
      const alert = alerts.data.find((a) => a.transaction.id === data.transaction.id && a.status === "OPEN");
      if (!alert) {
        setError("No open alert found for this transaction");
        return;
      }
      await api.put(`/fraud/alerts/${alert.id}/review`, { decision });
      navigate("/alerts");
    } catch (err) {
      setError(err.response?.data?.error || "Could not submit decision");
    }
  }

  if (error) return <p style={{ color: "red" }}>{error}</p>;
  if (!data) return <p>Loading...</p>;

  const tx = data.transaction;
  const level = tx.riskLevel;

  return (
    <div>
      <h2>Investigate Transaction TXN-{tx.id}</h2>

      <Section title="Customer Information">
        <p><strong>Name:</strong> {data.customer.name}</p>
        <p><strong>Email:</strong> {data.customer.email}</p>
        <p><strong>Previous confirmed fraud cases:</strong> {data.previousFraudCount}</p>
      </Section>

      <Section title="Transaction Information">
        <p><strong>Amount:</strong> ₹{tx.amount}</p>
        <p><strong>Type:</strong> {tx.transactionType}</p>
        <p><strong>Location:</strong> {tx.location}</p>
        <p><strong>Device:</strong> {tx.deviceId}</p>
        <p><strong>Time:</strong> {new Date(tx.timestamp).toLocaleString()}</p>
        <p><strong>Status:</strong> {tx.status}</p>
      </Section>

      <Section title="Risk Analysis">
        <p style={{ fontSize: 20 }}>
          <strong>Risk Score:</strong> {tx.riskScore} —{" "}
          <span style={{ color: LEVEL_COLORS[level], fontWeight: "bold" }}>{level}</span>
        </p>
        <p><strong>Rule-based score:</strong> {tx.ruleScore}</p>
        <p><strong>ML fraud probability:</strong> {(tx.fraudProbability * 100).toFixed(0)}%</p>
        <p><strong>Triggered rules / explanation:</strong></p>
        <pre style={{ whiteSpace: "pre-wrap", background: "#f9f9f9", padding: 10 }}>{tx.riskReasons}</pre>
      </Section>

      <Section title={`Customer's Other Transactions (${data.customerHistory.length})`}>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr>
              <th style={th}>Date</th>
              <th style={th}>Amount</th>
              <th style={th}>Location</th>
              <th style={th}>Risk Score</th>
              <th style={th}>Status</th>
            </tr>
          </thead>
          <tbody>
            {data.customerHistory.map((t) => (
              <tr key={t.id} style={{ background: t.id === tx.id ? "#fef9c3" : "transparent" }}>
                <td style={td}>{new Date(t.timestamp).toLocaleString()}</td>
                <td style={td}>₹{t.amount}</td>
                <td style={td}>{t.location}</td>
                <td style={td}>{t.riskScore}</td>
                <td style={td}>{t.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </Section>

      {tx.status === "FLAGGED" && (
        <div style={{ marginTop: 20 }}>
          <button onClick={() => decide("FRAUD")} style={{ marginRight: 10, background: "#dc2626", color: "white", padding: "8px 16px" }}>
            Mark as Fraud
          </button>
          <button onClick={() => decide("LEGITIMATE")} style={{ background: "#16a34a", color: "white", padding: "8px 16px" }}>
            Mark as Legitimate
          </button>
        </div>
      )}
    </div>
  );
}

function Section({ title, children }) {
  return (
    <div style={{ border: "1px solid #ddd", borderRadius: 8, padding: 15, marginBottom: 15 }}>
      <h4 style={{ marginTop: 0 }}>{title}</h4>
      {children}
    </div>
  );
}

const th = { textAlign: "left", borderBottom: "2px solid #ddd", padding: 8 };
const td = { borderBottom: "1px solid #eee", padding: 8 };