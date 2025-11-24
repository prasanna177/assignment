import './Pagination.css';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  loading?: boolean;
}

/**
 * Pagination Component
 * Provides navigation controls for paginated data
 */
export const Pagination = ({
  currentPage,
  totalPages,
  totalItems,
  pageSize,
  onPageChange,
  loading = false,
}: PaginationProps) => {
  
  // Calculate the range of items being displayed
  const startItem = totalItems === 0 ? 0 : currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalItems);

  // Generate page numbers to display
  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const maxPagesToShow = 5;
    
    if (totalPages <= maxPagesToShow) {
      // Show all pages if total is small
      for (let i = 0; i < totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Show first page
      pages.push(0);
      
      // Calculate range around current page
      let start = Math.max(1, currentPage - 1);
      let end = Math.min(totalPages - 2, currentPage + 1);
      
      // Add ellipsis if needed
      if (start > 1) {
        pages.push('...');
      }
      
      // Add pages around current
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      
      // Add ellipsis if needed
      if (end < totalPages - 2) {
        pages.push('...');
      }
      
      // Show last page
      if (totalPages > 1) {
        pages.push(totalPages - 1);
      }
    }
    
    return pages;
  };

  const pageNumbers = getPageNumbers();

  return (
    <div className="pagination">
      <div className="pagination-info">
        <span className="items-info">
          Showing <strong>{startItem}</strong> to <strong>{endItem}</strong> of{' '}
          <strong>{totalItems}</strong> transactions
        </span>
      </div>

      <div className="pagination-controls">
        {/* First Page */}
        <button
          className="pagination-button"
          onClick={() => onPageChange(0)}
          disabled={currentPage === 0 || loading || totalPages === 0}
          aria-label="First page"
        >
          « First
        </button>

        {/* Previous Page */}
        <button
          className="pagination-button"
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0 || loading || totalPages === 0}
          aria-label="Previous page"
        >
          ‹ Previous
        </button>

        {/* Page Numbers */}
        <div className="page-numbers">
          {pageNumbers.map((pageNum, index) => {
            if (pageNum === '...') {
              return (
                <span key={`ellipsis-${index}`} className="page-ellipsis">
                  ...
                </span>
              );
            }
            
            return (
              <button
                key={pageNum}
                className={`page-number ${
                  pageNum === currentPage ? 'active' : ''
                }`}
                onClick={() => onPageChange(pageNum as number)}
                disabled={loading}
              >
                {(pageNum as number) + 1}
              </button>
            );
          })}
        </div>

        {/* Next Page */}
        <button
          className="pagination-button"
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= totalPages - 1 || loading || totalPages === 0}
          aria-label="Next page"
        >
          Next ›
        </button>

        {/* Last Page */}
        <button
          className="pagination-button"
          onClick={() => onPageChange(totalPages - 1)}
          disabled={currentPage >= totalPages - 1 || loading || totalPages === 0}
          aria-label="Last page"
        >
          Last »
        </button>
      </div>

      <div className="pagination-summary">
        Page <strong>{totalPages === 0 ? 0 : currentPage + 1}</strong> of{' '}
        <strong>{totalPages}</strong>
      </div>
    </div>
  );
};
