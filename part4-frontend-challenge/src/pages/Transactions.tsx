import { useState } from 'react';
import { FilterState, DEFAULT_FILTERS } from '../types/transaction';
import { TransactionList } from '../components/common/TransactionList';
import { TransactionSummary } from '../components/common/TransactionSummary';
import { TransactionFilters } from '../components/common/TransactionFilters';
import { Pagination } from '../components/common/Pagination';
import { useTransactions } from '../hooks/useTransactions';

/**
 * Transactions Page Component
 * Displays transaction dashboard with summary, filters, and paginated list
 */
export const Transactions = () => {
  const [merchantId, setMerchantId] = useState(import.meta.env.VITE_DEFAULT_MERCHANT_ID || 'MCH-00001');
  const [filters, setFilters] = useState<FilterState>(DEFAULT_FILTERS);
  const [tempMerchantId, setTempMerchantId] = useState(merchantId);

  const { data, loading, error } = useTransactions(merchantId, filters);

  const handleFilterChange = (newFilters: FilterState) => {
    setFilters(newFilters);
  };

  const handleMerchantSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (tempMerchantId.trim()) {
      setMerchantId(tempMerchantId.trim());
      setFilters({ ...filters, page: 0 }); // Reset to first page on new merchant
    }
  };

  const handlePageChange = (page: number) => {
    setFilters({
      ...filters,
      page,
    });
  };

  // Calculate total pages based on response data
  const totalPages = data?.pagination?.totalPages || 0;

  return (
    <main className="container">
      <div className="header-section" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1>Transaction Dashboard</h1>
          <p className="subtitle">Viewing transactions for: <strong>{merchantId}</strong></p>
        </div>
        
        <form onSubmit={handleMerchantSearch} className="merchant-search" style={{ display: 'flex', gap: '0.5rem' }}>
          <input
            type="text"
            value={tempMerchantId}
            onChange={(e) => setTempMerchantId(e.target.value)}
            placeholder="Enter Merchant ID"
            style={{ padding: '0.5rem', borderRadius: '4px', border: '1px solid #cbd5e1' }}
          />
          <button 
            type="submit"
            style={{ 
              padding: '0.5rem 1rem', 
              background: '#3b82f6', 
              color: 'white', 
              border: 'none', 
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Switch Merchant
          </button>
        </form>
      </div>

      {/* Transaction Filters */}
      <TransactionFilters 
        filters={filters}
        onFilterChange={handleFilterChange}
        loading={loading}
      />

      {error && (
        <div className="error-message" style={{ padding: '1rem', background: '#fee2e2', borderRadius: '8px', color: '#991b1b', margin: '1rem 0' }}>
          Error loading transactions: {error.message}
        </div>
      )}

      {loading && !data && (
        <div className="loading-message" style={{ padding: '2rem', textAlign: 'center', color: '#64748b' }}>
          Loading transactions...
        </div>
      )}

      {data && (
        <>
          <div className="summary-section">
            <TransactionSummary 
              summary={data.summary}
            />
          </div>

          <div className="transactions-section">
            <TransactionList 
              transactions={data.transactions || []} 
              loading={loading}
            />
          </div>

          {/* Pagination */}
          <Pagination 
            currentPage={data.pagination.page}
            totalPages={totalPages}
            totalItems={data.pagination.totalElements || 0}
            pageSize={filters.size}
            onPageChange={handlePageChange}
            loading={loading}
          />
        </>
      )}
    </main>
  );
};

