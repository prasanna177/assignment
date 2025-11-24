// Merchant-related types and interfaces

export interface Merchant {
  id: string;
  name: string;
  email: string;
  phone: string;
  status: string;
  address: string;
  businessName: string;
}

export interface SearchRequestPayload {
  pageNumber: number;
  pageSize: number;
  sortField?: string | null;
  sortOrder?: string | null;
  searchParameter: string;
}

export interface SearchResponse<T> {
  currentPage: number;
  totalRecord: number;
  pageSize: number;
  totalPage: number;
  data: T[];
}

export interface ApiResponse<T> {
  code: string;
  message: string;
  data: T;
}

// Merchant Add Payload
export interface MerchantAddPayload {
  id: string;
  name: string;
  email: string;
  phone: string;
  status: string;
  address: string;
  businessName: string;
}

// Form validation errors
export interface FormErrors {
  [key: string]: string;
}

// Merchant Edit Payload
export interface MerchantEditPayload {
  id: string;
  name: string;
  email: string;
  phone: string;
  status: string;
  address: string;
  businessName: string;
}


export type MerchantSearchResponse = SearchResponse<Merchant>;
