import { useState, useEffect } from 'react';
import { getTransactions } from '../services/transactionService';
import { FilterState, TransactionResponse } from '../types/transaction';

interface UseTransactionsResult {
  data: TransactionResponse | null;
  loading: boolean;
  error: Error | null;
  refetch: () => void;
}

export const useTransactions = (
  merchantId: string,
  filters: FilterState
): UseTransactionsResult => {
  const [data, setData] = useState<TransactionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await getTransactions(merchantId, filters);
      setData(response);
    } catch (err) {
      setError(err as Error);
      console.error('Error fetching transactions:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions();
  }, [merchantId, filters.page, filters.size, filters.status, filters.startDate, filters.endDate]);

  return {
    data,
    loading,
    error,
    refetch: fetchTransactions,
  };
};
