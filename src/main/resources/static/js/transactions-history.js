/**
 * Transaction History Module
 */

// State Management
const TransactionState = {
    filters: {
        page: 0,
        size: 20,
        dateFrom: null,
        dateTo: null,
        type: null,
        amountFrom: null,
        amountTo: null,
        searchQuery: null,
        sortBy: 'date',
        sortDirection: 'desc',
        accountId: null
    },

    loading: false,
    hasMoreData: true,
    totalElements: 0,
    loadedElements: 0,

    resetPagination() {
        this.filters.page = 0;
        this.hasMoreData = true;
        this.loadedElements = 0;
        this.totalElements = 0;
    }
};

// API Functions
const TransactionAPI = {
    buildUrl(baseUrl = '/api/banking/history') {
        const params = new URLSearchParams();
        params.append('accountId', TransactionState.filters.accountId);
        
        Object.entries(TransactionState.filters).forEach(([key, value]) => {
            if (key !== 'accountId' && value !== null && value !== '') {
                if (key === 'dateFrom' && value) {
                    params.append(key, value + 'T00:00:00');
                } else if (key === 'dateTo' && value) {
                    params.append(key, value + 'T23:59:59');
                } else {
                    params.append(key, value);
                }
            }
        });

        return `${baseUrl}?${params.toString()}`;
    },

    async loadTransactions(append = false) {
        if (TransactionState.loading || (!append && !TransactionState.hasMoreData)) return;
        if (append && !TransactionUI.hasMoreTransactions()) return;
        
        TransactionState.loading = true;
        TransactionUI.showLoadingSpinner();

        try {
            const url = this.buildUrl();
            console.log('Requesting URL:', url);
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            TransactionState.totalElements = data.totalElements;
            
            if (!append) {
                TransactionUI.clearTransactions();
                TransactionState.loadedElements = 0;
            }

            data.content.forEach(transaction => {
                TransactionUI.renderTransaction(transaction);
            });

            TransactionState.loadedElements += data.content.length;
            TransactionState.hasMoreData = TransactionUI.hasMoreTransactions();

            if (append && TransactionState.hasMoreData) {
                TransactionState.filters.page++;
            }
        } catch (error) {
            console.error('Error loading transactions:', error);
            alert('Error loading transactions. Please try again.');
        } finally {
            TransactionState.loading = false;
            TransactionUI.hideLoadingSpinner();
        }
    }
};

// UI Functions
const TransactionUI = {
    elements: {
        table: () => document.querySelector('.transactions-table tbody'),
        cards: () => document.querySelector('.transaction-cards'),
        spinner: () => document.querySelector('.loading-spinner')
    },

    showLoadingSpinner() {
        this.elements.spinner().classList.add('visible');
    },

    hideLoadingSpinner() {
        this.elements.spinner().classList.remove('visible');
    },

    clearTransactions() {
        this.elements.table().innerHTML = '';
        this.elements.cards().innerHTML = '';
    },

    hasMoreTransactions() {
        return TransactionState.loadedElements < TransactionState.totalElements;
    },

    shouldShowPositive(transaction) {
        if (transaction.type.name === 'DEPOSIT') return true;
        if (transaction.type.name === 'FEE' || transaction.type.name === 'WITHDRAWAL') return false;
        return transaction.destinationAccount?.id === parseInt(TransactionState.filters.accountId);
    },

    createTransactionCard(transaction) {
        const isPositive = this.shouldShowPositive(transaction);
        const amountClass = isPositive ? 'amount-positive' : 'amount-negative';
        const amountPrefix = isPositive ? '+' : '-';
        const sourceOwner = transaction.sourceAccount?.owner?.fullName || '';
        const destinationOwner = transaction.destinationAccount?.owner?.fullName || '';

        return `
            <div class="transaction-card">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <div class="transaction-date">${new Date(transaction.date).toLocaleDateString()}</div>
                        <div class="transaction-title">${transaction.title || ''}</div>
                        <div class="transaction-parties">${sourceOwner} → ${destinationOwner}</div>
                    </div>
                    <div>
                        <span class="transaction-amount ${amountClass}">
                            ${amountPrefix}${transaction.amount.toFixed(2)} PLN
                        </span>
                    </div>
                </div>
                <div class="mt-2">
                    <span class="badge badge-secondary">${transaction.type.displayName}</span>
                    <span class="badge badge-info">${transaction.status}</span>
                </div>
            </div>
        `;
    },

    createTransactionRow(transaction) {
        const isPositive = this.shouldShowPositive(transaction);
        const amountClass = isPositive ? 'amount-positive' : 'amount-negative';
        const amountPrefix = isPositive ? '+' : '-';
        const sourceOwner = transaction.sourceAccount?.owner?.fullName || '';
        const destinationOwner = transaction.destinationAccount?.owner?.fullName || '';

        return `
            <tr>
                <td>${new Date(transaction.date).toLocaleDateString()}</td>
                <td>
                    <span class="transaction-amount ${amountClass}">
                        ${amountPrefix}${transaction.amount.toFixed(2)} PLN
                    </span>
                </td>
                <td>${transaction.type.displayName}</td>
                <td>${sourceOwner} → ${destinationOwner}</td>
                <td>${transaction.title || ''}</td>
                <td>${transaction.status.displayName}</td>
            </tr>
        `;
    },

    renderTransaction(transaction) {
        this.elements.table().insertAdjacentHTML('beforeend', this.createTransactionRow(transaction));
        this.elements.cards().insertAdjacentHTML('beforeend', this.createTransactionCard(transaction));
    }
};

// Event Handlers
const TransactionEvents = {
    setupFilterHandlers() {
        document.querySelectorAll('.filter-input').forEach(input => {
            input.addEventListener('change', () => {
                TransactionState.filters[input.name] = input.value;
                TransactionState.resetPagination();
                TransactionAPI.loadTransactions();
            });
        });
    },

    setupSortHandlers() {
        document.querySelectorAll('.sort-header').forEach(header => {
            header.addEventListener('click', () => {
                const column = header.dataset.column;
                if (TransactionState.filters.sortBy === column) {
                    TransactionState.filters.sortDirection = 
                        TransactionState.filters.sortDirection === 'asc' ? 'desc' : 'asc';
                } else {
                    TransactionState.filters.sortBy = column;
                    TransactionState.filters.sortDirection = 'desc';
                }

                TransactionState.resetPagination();
                TransactionAPI.loadTransactions();
                
                document.querySelectorAll('.sort-header').forEach(h => {
                    h.classList.remove('sorted-asc', 'sorted-desc');
                });
                header.classList.add(`sorted-${TransactionState.filters.sortDirection}`);
            });
        });
    },

    setupInfiniteScroll() {
        window.addEventListener('scroll', () => {
            if (!TransactionState.loading && TransactionState.hasMoreData && 
                window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
                TransactionAPI.loadTransactions(true);
            }
        });
    }
};

// Export function
window.exportTransactions = function(format) {
    const params = new URLSearchParams();
    params.append('accountId', TransactionState.filters.accountId);
    
    const { page, size, sortBy, sortDirection, ...exportFilters } = TransactionState.filters;
    Object.entries(exportFilters).forEach(([key, value]) => {
        if (key !== 'accountId' && value !== null && value !== '') {
            if (key === 'dateFrom' && value) {
                params.append(key, value + 'T00:00:00');
            } else if (key === 'dateTo' && value) {
                params.append(key, value + 'T23:59:59');
            } else {
                params.append(key, value);
            }
        }
    });
    params.append('format', format);

    window.location.href = `/api/banking/history/export?${params.toString()}`;
};

// Initialization
document.addEventListener('DOMContentLoaded', function() {
    TransactionState.filters.accountId = document.querySelector('#accountId').value;
    TransactionEvents.setupFilterHandlers();
    TransactionEvents.setupSortHandlers();
    TransactionEvents.setupInfiniteScroll();
    TransactionAPI.loadTransactions();
});