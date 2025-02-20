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
        sortDirection: 'desc'
    };

    // Stan ładowania
    let loading = false;
    let hasMoreData = true;

    // Funkcja do tworzenia URL z parametrami
    function buildUrl(baseUrl = '/api/transaction-history') {
        const params = new URLSearchParams();
        
        Object.entries(filters).forEach(([key, value]) => {
            if (value !== null && value !== '') {
                params.append(key, value);
            }
        });

        return `${baseUrl}?${params.toString()}`;
    }

    // Funkcja do tworzenia elementu transakcji (widok mobilny)
    function createTransactionCard(transaction) {
        const isPositive = transaction.type === 'DEPOSIT' || transaction.type === 'TRANSFER_IN';
        const amountClass = isPositive ? 'amount-positive' : 'amount-negative';
        const amountPrefix = isPositive ? '+' : '-';

        return `
            <div class="transaction-card">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <div class="transaction-date">${new Date(transaction.date).toLocaleString()}</div>
                        <div class="transaction-title">${transaction.title}</div>
                        <div class="transaction-parties">
                            ${transaction.sourceAccount ? transaction.sourceAccount.id : ''} → ${transaction.destinationAccount ? transaction.destinationAccount.id : ''}
                        </div>
                    </div>
                    <div>
                        <span class="transaction-amount ${amountClass}">
                            ${amountPrefix}${transaction.amount.toFixed(2)} PLN
                        </span>
                    </div>
                </div>
                <div class="mt-2">
                    <span class="badge badge-secondary">${transaction.type}</span>
                    <span class="badge badge-info">${transaction.status}</span>
                </div>
            </div>
        `;
    }

    // Funkcja do tworzenia wiersza tabeli (widok desktop)
    function createTransactionRow(transaction) {
        const isPositive = transaction.type === 'DEPOSIT' || transaction.type === 'TRANSFER_IN';
        const amountClass = isPositive ? 'amount-positive' : 'amount-negative';
        const amountPrefix = isPositive ? '+' : '-';

        return `
            <tr>
                <td>${new Date(transaction.date).toLocaleString()}</td>
                <td>
                    <span class="transaction-amount ${amountClass}">
                        ${amountPrefix}${transaction.amount.toFixed(2)} PLN
                    </span>
                </td>
                <td>${transaction.type}</td>
                <td>${transaction.sourceAccount ? transaction.sourceAccount.id : ''} → ${transaction.destinationAccount ? transaction.destinationAccount.id : ''}</td>
                <td>${transaction.title}</td>
                <td>${transaction.status}</td>
            </tr>
        `;
    }

    // Funkcja do ładowania transakcji
    async function loadTransactions(append = false) {
        if (loading || (!append && !hasMoreData)) return;
        
        loading = true;
        const loadingSpinner = document.querySelector('.loading-spinner');
        loadingSpinner.classList.add('visible');

        try {
            const response = await fetch(buildUrl());
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();

            hasMoreData = data.content.length === filters.size;
            
            if (!append) {
                document.querySelector('.transactions-table tbody').innerHTML = '';
                document.querySelector('.transaction-cards').innerHTML = '';
            }

            data.content.forEach(transaction => {
                document.querySelector('.transactions-table tbody')
                    .insertAdjacentHTML('beforeend', createTransactionRow(transaction));
                document.querySelector('.transaction-cards')
                    .insertAdjacentHTML('beforeend', createTransactionCard(transaction));
            });

            if (append) {
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
        // Usuń parametry paginacji z filtrów dla eksportu
        const { page, size, sortBy, sortDirection, ...exportFilters } = filters;
        
        const params = new URLSearchParams();
        Object.entries(exportFilters).forEach(([key, value]) => {
            if (value !== null && value !== '') {
                params.append(key, value);
            }
        });
        params.append('format', format);

        // Pobierz plik
        window.location.href = `/api/transaction-history/export?${params.toString()}`;
    };

    // Obsługa filtrów
    document.querySelectorAll('.filter-input').forEach(input => {
        input.addEventListener('change', () => {
            filters[input.name] = input.value;
            filters.page = 0;
            hasMoreData = true;
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
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
            loadTransactions(true);
        }
    });

    // Inicjalne załadowanie
    loadTransactions();
});