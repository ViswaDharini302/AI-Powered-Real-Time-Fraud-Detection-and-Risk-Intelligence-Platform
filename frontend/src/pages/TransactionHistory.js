import React, { useEffect, useState } from "react";
import api from "../api/client";

export default function TransactionHistory() {
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/transactions/me")
      .then((res) => setTransactions(res.data))
      .catch((err) => setError(err.response?.data?.error || "Could not load history"));
  }, []);

  return (
    <div>
      <h2>Transaction History</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th style={th}>Date</th>
            <th style={th}>Amount</th>
            <th style={th}>Location</th>
            <th style={th}>Risk Score</th>
            <th style={th}>Level</th>
            <th style={th}>Status</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((t) => (
            <tr key={t.id}>
              <td style={td}>{new Date(t.timestamp).toLocaleString()}</td>
              <td style={td}>₹{t.amount}</td>
              <td style={td}>{t.location}</td>
              <td style={td}>{t.riskScore}</td>
              <td style={td}>{t.riskLevel}</td>
              <td style={td}>{t.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

const th = { textAlign: "left", borderBottom: "2px solid #ddd", padding: 8 };
const td = { borderBottom: "1px solid #eee", padding: 8 };