import { post, put, get } from './api';
import { 
  SearchRequestPayload, 
  ApiResponse, 
  MerchantSearchResponse,
  Merchant,
  MerchantAddPayload,
  MerchantEditPayload
} from '../types/merchant';

/**
 * Merchant Service
 * Handles all merchant-related API calls
 */

const MERCHANT_BASE = '/merchants';

/**
 * Search merchants with pagination and filtering
 * 
 * @param searchParams - Search parameters (page, size, sort, search term)
 * @returns Promise with merchant search response data
 */
export const searchMerchants = async (
  searchParams: SearchRequestPayload
): Promise<MerchantSearchResponse> => {
  const url = `${MERCHANT_BASE}/search`;
  
  try {
    const response = await post<ApiResponse<MerchantSearchResponse>>(url, searchParams);
    
    console.log('API Response:', response); // Debug log
    
    // Extract data from the RestResponse wrapper
    // Backend returns code: "0" for success
    if (response.code === '0' && response.data) {
      return response.data;
    }
    
    throw new Error(response.message || 'Failed to fetch merchants');
  } catch (error: any) {
    console.error('Search merchants error:', error);
    
    // Extract error message from API response
    if (error.response?.data?.message) {
      throw new Error(error.response.data.message);
    }
    
    // If it's already an Error with a message, rethrow it
    if (error instanceof Error) {
      throw error;
    }
    
    // Fallback to generic message
    throw new Error('Failed to fetch merchants');
  }
};

/**
 * Add a new merchant
 */
export const addMerchant = async (payload: MerchantAddPayload): Promise<Merchant> => {
  try {
    const response = await post<ApiResponse<Merchant>>(MERCHANT_BASE, payload);
    
    console.log('Add Merchant Response:', response);
    
    if (response.code === '0') {
      return response.data;
    }
    
    throw new Error(response.message || 'Failed to add merchant');
  } catch (error: any) {
    console.error('Add merchant error:', error);
    
    // Extract error message from API response
    if (error.response?.data?.message) {
      throw new Error(error.response.data.message);
    }
    
    // If it's already an Error with a message, rethrow it
    if (error instanceof Error) {
      throw error;
    }
    
    // Fallback to generic message
    throw new Error('Failed to add merchant');
  }
};

/**
 * Update an existing merchant
 */
export const updateMerchant = async (payload: MerchantEditPayload): Promise<Merchant> => {
  try {
    const response = await put<ApiResponse<Merchant>>(MERCHANT_BASE, payload);
    
    console.log('Update Merchant Response:', response);
    
    if (response.code === '0') {
      return response.data;
    }
    
    throw new Error(response.message || 'Failed to update merchant');
  } catch (error: any) {
    console.error('Update merchant error:', error);
    
    // Extract error message from API response
    if (error.response?.data?.message) {
      throw new Error(error.response.data.message);
    }
    
    // If it's already an Error with a message, rethrow it
    if (error instanceof Error) {
      throw error;
    }
    
    // Fallback to generic message
    throw new Error('Failed to update merchant');
  }
};

/**
 * Get merchant by ID
 */
export const getMerchantById = async (id: string): Promise<Merchant> => {
  try {
    const response = await get<ApiResponse<Merchant>>(`${MERCHANT_BASE}/${id}`);
    
    console.log('Get Merchant By ID Response:', response);
    
    if (response.code === '0') {
      return response.data;
    }
    
    throw new Error(response.message || 'Failed to fetch merchant details');
  } catch (error: any) {
    console.error('Get merchant by ID error:', error);
    
    // Extract error message from API response
    if (error.response?.data?.message) {
      throw new Error(error.response.data.message);
    }
    
    // If it's already an Error with a message, rethrow it
    if (error instanceof Error) {
      throw error;
    }
    
    // Fallback to generic message
    throw new Error('Failed to fetch merchant details');
  }
};

export default {
  searchMerchants,
  addMerchant,
  updateMerchant,
  getMerchantById,
};
