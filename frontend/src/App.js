/*import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;*/
import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import NewTransaction from "./pages/NewTransaction";
import TransactionHistory from "./pages/TransactionHistory";
import FraudAlerts from "./pages/FraudAlerts";
import TransactionInvestigation from "./pages/TransactionInvestigation";
import Navbar from "./components/Navbar";

function isLoggedIn() {
  return !!localStorage.getItem("token");
}

function PrivateRoute({ children }) {
  return isLoggedIn() ? children : <Navigate to="/login" />;
}

export default function App() {
  return (
    <BrowserRouter>
      {isLoggedIn() && <Navbar />}
      <div style={{ maxWidth: 900, margin: "0 auto", padding: 20 }}>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
          <Route path="/new-transaction" element={<PrivateRoute><NewTransaction /></PrivateRoute>} />
          <Route path="/history" element={<PrivateRoute><TransactionHistory /></PrivateRoute>} />
          <Route path="/alerts" element={<PrivateRoute><FraudAlerts /></PrivateRoute>} />
          <Route path="/investigate/:id" element={<PrivateRoute><TransactionInvestigation /></PrivateRoute>} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}