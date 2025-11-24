import { get } from "./api";
import { TransactionResponse, FilterState } from "../types/transaction";

/**
 * Transaction Service
 * Handles all transaction-related API calls
 */

const MERCHANT_BASE = "/transactions";

/**
 * Get transactions for a specific merchant
 *
 * TODO: Implement this method to call the backend API
 *
 * @param merchantId - The merchant ID
 * @param filters - Filter parameters (page, size, dates, status)
 * @returns Promise with transaction response data
 */
export const getTransactions = async (
  merchantId: string,
  filters: FilterState
): Promise<TransactionResponse> => {
  // TODO: Build query parameters
  const params = {
    page: filters.page,
    size: filters.size,
    ...(filters.startDate && { startDate: filters.startDate }),
    ...(filters.endDate && { endDate: filters.endDate }),
    ...(filters.status && { status: filters.status }),
  };

  // TODO: Make API call
  const url = `${MERCHANT_BASE}/${merchantId}/transactions`;

  try {
    const response = await get<TransactionResponse>(url, { params });
    return response;
  } catch (error) {
    console.error("Error fetching transactions:", error);
    throw error;
  }
};

/**
 * Get a single transaction by ID
 * (Optional - for future enhancement)
 */
export const getTransactionById = async (txnId: number): Promise<any> => {
  // TODO: Implement if needed
  throw new Error("Not implemented");
};

export default {
  getTransactions,
  getTransactionById,
};
