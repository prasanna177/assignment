import './TransactionSummary.css';
import { TransactionSummary as TransactionSummaryType } from '../../types/transaction';

interface TransactionSummaryProps {
  summary: TransactionSummaryType;
}

export const TransactionSummary = ({ summary }: TransactionSummaryProps) => {
  const formatAmount = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: summary.currency || 'USD',
    }).format(amount);
  };

  const getCount = (status: string) => {
    const lowerStatus = status.toLowerCase();
    const key = Object.keys(summary.byStatus).find(k => k.toLowerCase() === lowerStatus);
    return key ? summary.byStatus[key] : 0;
  };

  return (
    <div className="transaction-summary">
      <div className="summary-card">
        <div className="summary-label">Total Transactions</div>
        <div className="summary-value">{summary.totalTransactions}</div>
      </div>

      <div className="summary-card">
        <div className="summary-label">Total Amount</div>
        <div className="summary-value amount">{formatAmount(summary.totalAmount)}</div>
      </div>

      <div className="summary-card">
        <div className="summary-label">Completed</div>
        <div className="summary-value completed">{getCount('completed')}</div>
      </div>

      <div className="summary-card">
        <div className="summary-label">Pending</div>
        <div className="summary-value pending">{getCount('pending')}</div>
      </div>

      <div className="summary-card">
        <div className="summary-label">Failed</div>
        <div className="summary-value failed">{getCount('failed')}</div>
      </div>
    </div>
  );
};
