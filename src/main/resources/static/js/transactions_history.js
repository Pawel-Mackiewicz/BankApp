document.addEventListener('DOMContentLoaded', function() {
    // Stan filtrów
    let filters = {
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
        accountId: document.querySelector('#accountId').value
    };

    // Stan ładowania
    let loading = false;
    let hasMoreData = true;
    let totalElements = 0;
    let loadedElements = 0;

    // Funkcja do tworzenia URL z parametrami
    function buildUrl(baseUrl = '/api/transaction-history') {
        const params = new URLSearchParams();
        
        // Ensure accountId is always included
        params.append('accountId', filters.accountId);
        
        Object.entries(filters).forEach(([key, value]) => {
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
    }

    // Funkcja sprawdzająca czy są jeszcze transakcje do załadowania
    function hasMoreTransactions() {
        return loadedElements < totalElements;
    }

    // Funkcja określająca znak transakcji z perspektywy wybranego konta
    function shouldShowPositive(transaction) {
        // Dla depozytów zawsze plus
        if (transaction.type.name === 'DEPOSIT') {
            return true;
        }
        
        // Dla opłat i wypłat zawsze minus
        if (transaction.type.name === 'FEE' || transaction.type.name === 'WITHDRAWAL') {
            return false;
        }
        
        // Dla transferów sprawdzamy czy wybrane konto jest odbiorcą
        return transaction.destinationAccount?.id === parseInt(filters.accountId);
    }

    // Funkcja do tworzenia elementu transakcji (widok mobilny)
    function createTransactionCard(transaction) {
        const isPositive = shouldShowPositive(transaction);
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
    }

    // Funkcja do tworzenia wiersza tabeli (widok desktop)
    function createTransactionRow(transaction) {
        const isPositive = shouldShowPositive(transaction);
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
                <td>${transaction.status}</td>
            </tr>
        `;
    }

    // Funkcja do ładowania transakcji
    async function loadTransactions(append = false) {
        if (loading || (!append && !hasMoreData)) return;
        if (append && !hasMoreTransactions()) return;
        
        loading = true;
        const loadingSpinner = document.querySelector('.loading-spinner');
        loadingSpinner.classList.add('visible');

        try {
            const response = await fetch(buildUrl());
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();

            totalElements = data.totalElements;
            
            if (!append) {
                document.querySelector('.transactions-table tbody').innerHTML = '';
                document.querySelector('.transaction-cards').innerHTML = '';
                loadedElements = 0;
            }

            data.content.forEach(transaction => {
                document.querySelector('.transactions-table tbody')
                    .insertAdjacentHTML('beforeend', createTransactionRow(transaction));
                document.querySelector('.transaction-cards')
                    .insertAdjacentHTML('beforeend', createTransactionCard(transaction));
            });

            loadedElements += data.content.length;
            hasMoreData = hasMoreTransactions();

            if (append && hasMoreData) {
                filters.page++;
            }
        } catch (error) {
            console.error('Error loading transactions:', error);
            alert('Error loading transactions. Please try again.');
        } finally {
            loading = false;
            loadingSpinner.classList.remove('visible');
        }
    }

    // Funkcja eksportu
    window.exportTransactions = function(format) {
        const params = new URLSearchParams();
        
        // Always include accountId
        params.append('accountId', filters.accountId);
        
        // Add other filters except pagination
        const { page, size, sortBy, sortDirection, ...exportFilters } = filters;
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

        window.location.href = `/api/transaction-history/export?${params.toString()}`;
    };

    // Obsługa filtrów
    document.querySelectorAll('.filter-input').forEach(input => {
        input.addEventListener('change', () => {
            filters[input.name] = input.value;
            filters.page = 0;
            hasMoreData = true;
            loadedElements = 0;
            totalElements = 0;
            loadTransactions();
        });
    });

    // Obsługa sortowania
    document.querySelectorAll('.sort-header').forEach(header => {
        header.addEventListener('click', () => {
            const column = header.dataset.column;
            if (filters.sortBy === column) {
                filters.sortDirection = filters.sortDirection === 'asc' ? 'desc' : 'asc';
            } else {
                filters.sortBy = column;
                filters.sortDirection = 'desc';
            }
            filters.page = 0;
            hasMoreData = true;
            loadedElements = 0;
            totalElements = 0;
            loadTransactions();
            
            // Aktualizacja ikon sortowania
            document.querySelectorAll('.sort-header').forEach(h => {
                h.classList.remove('sorted-asc', 'sorted-desc');
            });
            header.classList.add(`sorted-${filters.sortDirection}`);
        });
    });

    // Obsługa infinite scroll
    window.addEventListener('scroll', () => {
        if (!loading && hasMoreData && 
            window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
            loadTransactions(true);
        }
    });

    // Inicjalne załadowanie
    loadTransactions();
});