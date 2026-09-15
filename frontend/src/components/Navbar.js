import React from "react";
import { Link, useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();
  const name = localStorage.getItem("name");

  function logout() {
    localStorage.clear();
    navigate("/login");
  }

  return (
    <div style={{ display: "flex", justifyContent: "space-between", padding: 15, background: "#1f2937", color: "white" }}>
      <div style={{ display: "flex", gap: 15 }}>
        <Link style={{ color: "white" }} to="/">Dashboard</Link>
        <Link style={{ color: "white" }} to="/new-transaction">New Transaction</Link>
        <Link style={{ color: "white" }} to="/history">History</Link>
        <Link style={{ color: "white" }} to="/alerts">Fraud Alerts</Link>
      </div>
      <div>
        <span style={{ marginRight: 15 }}>Hi, {name}</span>
        <button onClick={logout}>Logout</button>
      </div>
    </div>
  );
}