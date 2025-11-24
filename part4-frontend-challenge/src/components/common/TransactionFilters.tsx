import { FilterState } from '../../types/transaction';
import './TransactionFilters.css';

interface TransactionFiltersProps {
  filters: FilterState;
  onFilterChange: (filters: FilterState) => void;
  loading?: boolean;
}

/**
 * TransactionFilters Component
 * Provides date range and status filtering controls
 */
export const TransactionFilters = ({ 
  filters, 
  onFilterChange, 
  loading = false 
}: TransactionFiltersProps) => {
  
  const handleDateChange = (field: 'startDate' | 'endDate', value: string) => {
    onFilterChange({
      ...filters,
      [field]: value,
      page: 0, // Reset to first page when filters change
    });
  };

  const handleStatusChange = (status: string) => {
    onFilterChange({
      ...filters,
      status: status || undefined,
      page: 0, // Reset to first page when filters change
    });
  };

  const handlePageSizeChange = (size: number) => {
    onFilterChange({
      ...filters,
      size,
      page: 0, // Reset to first page when page size changes
    });
  };

  const handleReset = () => {
    onFilterChange({
      page: 0,
      size: 20,
      startDate: '',
      endDate: '',
      status: undefined,
      searchQuery: undefined,
    });
  };

  return (
    <div className="transaction-filters">
      <div className="filters-header">
        <h3>🔍 Filters</h3>
        <button 
          className="reset-button" 
          onClick={handleReset}
          disabled={loading}
        >
          Reset Filters
        </button>
      </div>

      <div className="filters-grid">
        {/* Date Range Filters */}
        <div className="filter-group">
          <label htmlFor="startDate">Start Date</label>
          <input
            id="startDate"
            type="date"
            value={filters.startDate}
            onChange={(e) => handleDateChange('startDate', e.target.value)}
            disabled={loading}
            className="filter-input"
          />
        </div>

        <div className="filter-group">
          <label htmlFor="endDate">End Date</label>
          <input
            id="endDate"
            type="date"
            value={filters.endDate}
            onChange={(e) => handleDateChange('endDate', e.target.value)}
            disabled={loading}
            className="filter-input"
          />
        </div>

        {/* Status Filter */}
        <div className="filter-group">
          <label htmlFor="status">Status</label>
          <select
            id="status"
            value={filters.status || ''}
            onChange={(e) => handleStatusChange(e.target.value)}
            disabled={loading}
            className="filter-select"
          >
            <option value="">All Statuses</option>
            <option value="completed">Completed</option>
            <option value="pending">Pending</option>
            <option value="failed">Failed</option>
            <option value="reversed">Reversed</option>
          </select>
        </div>

        {/* Page Size Filter */}
        <div className="filter-group">
          <label htmlFor="pageSize">Page Size</label>
          <select
            id="pageSize"
            value={filters.size}
            onChange={(e) => handlePageSizeChange(Number(e.target.value))}
            disabled={loading}
            className="filter-select"
          >
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
            <option value={100}>100</option>
          </select>
        </div>
      </div>

      <div className="active-filters">
        {filters.startDate && (
          <span className="filter-tag">
            From: {filters.startDate}
            <button onClick={() => handleDateChange('startDate', '')}>×</button>
          </span>
        )}
        {filters.endDate && (
          <span className="filter-tag">
            To: {filters.endDate}
            <button onClick={() => handleDateChange('endDate', '')}>×</button>
          </span>
        )}
        {filters.status && (
          <span className="filter-tag">
            Status: {filters.status}
            <button onClick={() => handleStatusChange('')}>×</button>
          </span>
        )}
      </div>
    </div>
  );
};
