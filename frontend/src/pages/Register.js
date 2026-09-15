import React, { useState } from "react";
import api from "../api/client";
import { useNavigate, Link } from "react-router-dom";

export default function Register() {
  const [form, setForm] = useState({ name: "", email: "", password: "", role: "CUSTOMER" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  function update(field, value) {
    setForm({ ...form, [field]: value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const res = await api.post("/auth/register", form);
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("name", res.data.name);
      localStorage.setItem("role", res.data.role);
      navigate("/");
    } catch (err) {
      setError(err.response?.data?.error || "Registration failed");
    }
  }

  return (
    <div style={{ maxWidth: 350, margin: "80px auto" }}>
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <input placeholder="Name" value={form.name} onChange={(e) => update("name", e.target.value)} style={{ width: "100%", marginBottom: 10 }} />
        <input placeholder="Email" value={form.email} onChange={(e) => update("email", e.target.value)} style={{ width: "100%", marginBottom: 10 }} />
        <input placeholder="Password" type="password" value={form.password} onChange={(e) => update("password", e.target.value)} style={{ width: "100%", marginBottom: 10 }} />
        <select value={form.role} onChange={(e) => update("role", e.target.value)} style={{ width: "100%", marginBottom: 10 }}>
          <option value="CUSTOMER">Customer</option>
          <option value="FRAUD_ANALYST">Fraud Analyst</option>
          <option value="ADMIN">Admin</option>
        </select>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <button type="submit" style={{ width: "100%" }}>Register</button>
      </form>
      <p>Already have an account? <Link to="/login">Login</Link></p>
    </div>
  );
}