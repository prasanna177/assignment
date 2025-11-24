import { Routes, Route } from 'react-router-dom';
import { Header } from './components/layout/Header';
import { Transactions } from './pages/Transactions';
import { Merchants } from './pages/Merchants';
import { MerchantDetail } from './pages/MerchantDetail';
import { Reports } from './pages/Reports';
import './App.css';

/**
 * Main Application Component
 * 
 * Current status: Basic implementation with data fetching
 * 
 * Implemented:
 * 1. ✅ Data fetching using custom hook
 * 2. ✅ TransactionSummary component
 * 3. ✅ TransactionList component
 * 
 * TODO for junior developer:
 * 1. Add TransactionFilters component
 * 2. Add Pagination component
 * 3. Enhance error handling
 */

function App() {
  return (
    <div className="app">
      <Header />
      
      <Routes>
        <Route path="/" element={<Transactions />} />
        <Route path="/merchants" element={<Merchants />} />
        <Route path="/merchants/:id" element={<MerchantDetail />} />
        <Route path="/reports" element={<Reports />} />
      </Routes>
    </div>
  );
}

export default App;
