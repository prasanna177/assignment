import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getMerchantById } from '../services/merchantService';
import { getTransactions } from '../services/transactionService';
import { Merchant } from '../types/merchant';
import { TransactionResponse } from '../types/transaction';
import { Toast } from '../components/common/Toast';
import './MerchantDetail.css';

export const MerchantDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [merchant, setMerchant] = useState<Merchant | null>(null);
  const [transactions, setTransactions] = useState<TransactionResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' | 'info' } | null>(null);

  useEffect(() => {
    if (!id) {
      navigate('/merchants');
      return;
    }

    const fetchData = async () => {
      setLoading(true);
      try {
        // Fetch merchant details and transactions in parallel
        const [merchantData, transactionData] = await Promise.all([
          getMerchantById(id),
          getTransactions(id, {
            page: 0,
            size: 20, // Get last 20 transactions
            startDate: '',
            endDate: '',
            status: ''
          })
        ]);

        setMerchant(merchantData);
        setTransactions(transactionData);
      } catch (err) {
        setToast({
          message: err instanceof Error ? err.message : 'Failed to load merchant details',
          type: 'error'
        });
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, navigate]);

  if (loading) {
    return (
      <main className="container">
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading merchant details...</p>
        </div>
      </main>
    );
  }

  if (!merchant) {
    return (
      <main className="container">
        <div className="error-state">
          <h2>Merchant not found</h2>
          <button className="btn-primary" onClick={() => navigate('/merchants')}>
            Back to Merchants
          </button>
        </div>
      </main>
    );
  }

  const summary = transactions?.summary || {
    totalTransactions: 0,
    totalAmount: 0,
    currency: 'USD',
    byStatus: {}
  };

  // Calculate activity timeline from transactions
  const recentActivity = transactions?.transactions.slice(0, 5).map(txn => ({
    date: new Date(txn.timestamp),
    description: `Transaction ${txn.txnId} - ${txn.status}`,
    amount: txn.amount,
    type: txn.status
  })) || [];

  // Export transactions to CSV
  const handleExportCSV = () => {
    if (!transactions || transactions.transactions.length === 0) {
      setToast({
        message: 'No transactions to export',
        type: 'info'
      });
      return;
    }

    // CSV headers
    const headers = ['Transaction ID', 'Date', 'Amount', 'Currency', 'Status', 'Card Type', 'Card Last 4', 'Acquirer', 'Issuer'];
    
    // CSV rows
    const rows = transactions.transactions.map(txn => [
      txn.txnId,
      new Date(txn.timestamp).toLocaleString('en-US'),
      txn.amount.toFixed(2),
      txn.currency,
      txn.status,
      txn.cardType,
      txn.cardLast4,
      txn.acquirer,
      txn.issuer
    ]);

    // Combine headers and rows
    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');

    // Create blob and download
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', `merchant_${id}_transactions_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    setToast({
      message: 'Transaction history exported successfully!',
      type: 'success'
    });
  };

  return (
    <main className="container">
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}

      <div className="merchant-detail-page">
        {/* Header with back button */}
        <div className="detail-header">
          <button className="btn-back" onClick={() => navigate('/merchants')}>
            ← Back to Merchants
          </button>
          <h1>Merchant Details</h1>
        </div>

        {/* Merchant Profile Card */}
        <div className="profile-card">
          <div className="profile-header">
            <div className="profile-icon">🏢</div>
            <div className="profile-info">
              <h2>{merchant.businessName || merchant.name}</h2>
              <span className={`status-badge status-${merchant.status?.toLowerCase()}`}>
                {merchant.status}
              </span>
            </div>
          </div>

          <div className="profile-details">
            <div className="detail-row">
              <div className="detail-item">
                <span className="detail-label">Merchant ID</span>
                <span className="detail-value merchant-id">{merchant.id}</span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Contact Name</span>
                <span className="detail-value">{merchant.name}</span>
              </div>
            </div>

            <div className="detail-row">
              <div className="detail-item">
                <span className="detail-label">Email</span>
                <span className="detail-value">{merchant.email}</span>
              </div>
              <div className="detail-item">
                <span className="detail-label">Phone</span>
                <span className="detail-value">{merchant.phone}</span>
              </div>
            </div>

            <div className="detail-row">
              <div className="detail-item full-width">
                <span className="detail-label">Business Address</span>
                <span className="detail-value">{merchant.address || 'N/A'}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Transaction Statistics */}
        <div className="statistics-section">
          <div className="statistics-header">
            <h2>Transaction Statistics</h2>
            <button 
              className="btn-export" 
              onClick={handleExportCSV}
              disabled={!transactions || transactions.transactions.length === 0}
            >
              📥 Export CSV
            </button>
          </div>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon">📊</div>
              <div className="stat-content">
                <span className="stat-label">Total Transactions</span>
                <span className="stat-value">{summary.totalTransactions}</span>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">💰</div>
              <div className="stat-content">
                <span className="stat-label">Total Amount</span>
                <span className="stat-value">
                  {summary.currency} {summary.totalAmount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </span>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">✅</div>
              <div className="stat-content">
                <span className="stat-label">Completed</span>
                <span className="stat-value">{summary.byStatus?.['COMPLETED'] || summary.byStatus?.['completed'] || 0}</span>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">⏳</div>
              <div className="stat-content">
                <span className="stat-label">Pending</span>
                <span className="stat-value">{summary.byStatus?.['PENDING'] || summary.byStatus?.['pending'] || 0}</span>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">❌</div>
              <div className="stat-content">
                <span className="stat-label">Failed</span>
                <span className="stat-value">{summary.byStatus?.['FAILED'] || summary.byStatus?.['failed'] || 0}</span>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">↩️</div>
              <div className="stat-content">
                <span className="stat-label">Reversed</span>
                <span className="stat-value">{summary.byStatus?.['REVERSED'] || summary.byStatus?.['reversed'] || 0}</span>
              </div>
            </div>
          </div>
        </div>

        <div className="content-grid">
          {/* Recent Transactions */}
          <div className="recent-transactions">
            <h2>Recent Transactions</h2>
            {transactions && transactions.transactions.length > 0 ? (
              <div className="transactions-list">
                {transactions.transactions.map((txn) => (
                  <div key={txn.txnId} className="transaction-item">
                    <div className="transaction-main">
                      <div className="transaction-id">{txn.txnId}</div>
                      <div className="transaction-date">
                        {new Date(txn.timestamp).toLocaleDateString('en-US', {
                          year: 'numeric',
                          month: 'short',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit'
                        })}
                      </div>
                    </div>
                    <div className="transaction-details">
                      <span className="transaction-amount">
                        {txn.currency} {txn.amount.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                      </span>
                      <span className={`status-badge status-${txn.status.toLowerCase()}`}>
                        {txn.status}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <p>No transactions found</p>
              </div>
            )}
          </div>

          {/* Activity Timeline */}
          <div className="activity-timeline">
            <h2>Activity Timeline</h2>
            {recentActivity.length > 0 ? (
              <div className="timeline">
                {recentActivity.map((activity, index) => (
                  <div key={index} className="timeline-item">
                    <div className="timeline-marker"></div>
                    <div className="timeline-content">
                      <div className="timeline-date">
                        {activity.date.toLocaleDateString('en-US', {
                          month: 'short',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit'
                        })}
                      </div>
                      <div className="timeline-description">{activity.description}</div>
                      <div className="timeline-amount">
                        {summary.currency} {activity.amount.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <p>No recent activity</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  );
};
