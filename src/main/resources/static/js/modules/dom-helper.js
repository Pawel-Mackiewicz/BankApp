// DOM manipulation helper module

export class DOMHelper {
    // Account management
    static updateAccountSummary(newAccountsHtml) {
        const currentAccounts = document.querySelector('.account-summary');
        if (currentAccounts) {
            // Store current selections before update
            const currentSelections = this.getCurrentSelections();
            
            currentAccounts.innerHTML = newAccountsHtml;
            this.reattachIbanCopyListeners();

            // Extract account data and update forms
            const accountCards = currentAccounts.querySelectorAll('.account-card');
            const accountData = Array.from(accountCards).map(card => ({
                accountNumber: card.querySelector('h3 span').textContent,
                balance: card.querySelector('.details div span').textContent,
                iban: card.querySelector('.iban-text').getAttribute('data-iban'),
                id: null // Will be filled from existing options for own transfers
            }));

            // Update all transfer form selects with the new data
            this.updateTransferFormSelects(accountData, currentSelections);
        }
    }

    static getCurrentSelections() {
        return {
            own: document.getElementById('ownSourceAccountId')?.value,
            internal: document.getElementById('internalSourceAccountId')?.value,
            external: document.getElementById('externalSourceAccountId')?.value
        };
    }

    static getAccountInfo(accountData, formType, existingOption) {
        if (formType === 'own') {
            // For own transfers, preserve the account ID from the existing option
            return {
                value: existingOption?.value || '',
                text: `Account ${accountData.accountNumber} - ${accountData.iban.replace(/(.{4})/g, '$1 ')} (Balance: ${accountData.balance})`
            };
        } else {
            // For internal and external transfers, use IBAN
            return {
                value: accountData.iban,
                text: `Account ${accountData.accountNumber} - ${accountData.iban.replace(/(.{4})/g, '$1 ')} (Balance: ${accountData.balance})`
            };
        }
    }

    static updateTransferFormSelects(accountData, currentSelections = {}) {
        ['own', 'internal', 'external'].forEach(formType => {
            const select = document.getElementById(`${formType}SourceAccountId`);
            if (!select) return;

            const selectedValue = currentSelections[formType] || select.value;
            let options = '<option value="">Select account</option>';
            
            // For own transfers, we need to match account numbers to preserve IDs
            const existingOptions = formType === 'own' ? Array.from(select.options) : [];
            
            accountData.forEach(account => {
                let existingOption = null;
                if (formType === 'own') {
                    existingOption = existingOptions.find(opt => 
                        opt.text.includes(`Account ${account.accountNumber}`)
                    );
                }

                const accountInfo = this.getAccountInfo(account, formType, existingOption);
                if (accountInfo.value) {
                    options += `<option value="${accountInfo.value}" data-account-number="${account.accountNumber}" ${accountInfo.value === selectedValue ? 'selected' : ''}>${accountInfo.text}</option>`;
                }
            });
            
            select.innerHTML = options;

            // Update destination accounts for own transfers
            if (formType === 'own' && select.value) {
                this.updateDestinationAccounts(select.value);
            }
        });
    }

    static updateDestinationAccounts(selectedSourceId) {
        const ownSourceAccount = document.getElementById('ownSourceAccountId');
        const ownDestinationAccount = document.getElementById('ownDestinationAccountId');
        
        if (!ownSourceAccount || !ownDestinationAccount) return;

        let options = '<option value="">Select account</option>';
        const allAccounts = Array.from(ownSourceAccount.options).slice(1);
        
        allAccounts.forEach(option => {
            if (option.value !== selectedSourceId) {
                options += option.outerHTML;
            }
        });

        ownDestinationAccount.innerHTML = options;
    }

    // Transaction management
    static updateRecentTransactions(newTransactionsHtml) {
        const currentTransactions = document.querySelector('.recent-transactions');
        if (currentTransactions) {
            currentTransactions.innerHTML = newTransactionsHtml;
        }
    }

    // UI state management
    static toggleRecipientInputs(method) {
        const ibanDiv = document.querySelector('.recipient-iban');
        const emailDiv = document.querySelector('.recipient-email');
        const ibanInput = document.getElementById('internalRecipientIban');
        const emailInput = document.getElementById('internalRecipientEmail');

        if (method === 'iban') {
            this.showIbanInput(ibanDiv, ibanInput, emailDiv, emailInput);
        } else {
            this.showEmailInput(emailDiv, emailInput, ibanDiv, ibanInput);
        }
    }

    static showIbanInput(ibanDiv, ibanInput, emailDiv, emailInput) {
        ibanDiv.style.display = 'block';
        emailDiv.style.display = 'none';
        ibanInput.required = true;
        ibanInput.disabled = false;
        emailInput.required = false;
        emailInput.disabled = true;
        emailInput.value = '';
    }

    static showEmailInput(emailDiv, emailInput, ibanDiv, ibanInput) {
        ibanDiv.style.display = 'none';
        emailDiv.style.display = 'block';
        emailInput.required = true;
        emailInput.disabled = false;
        ibanInput.required = false;
        ibanInput.disabled = true;
        ibanInput.value = '';
    }

    // IBAN copy functionality
    static copyIban(iban) {
        navigator.clipboard.writeText(iban).then(() => {
            this.showCopyNotification();
        }).catch(err => {
            console.error('Error copying IBAN:', err);
            alert('Failed to copy IBAN. Please try again.');
        });
    }

    static showCopyNotification() {
        const notification = document.getElementById('copyNotification');
        if (notification) {
            notification.classList.add('show');
            setTimeout(() => {
                notification.classList.remove('show');
            }, 2000);
        }
    }

    static reattachIbanCopyListeners() {
        document.querySelectorAll('.copy-iban-btn, .iban-text').forEach(element => {
            element.addEventListener('click', () => {
                const iban = element.getAttribute('data-iban');
                this.copyIban(iban);
            });
        });

        document.querySelectorAll('.iban-text').forEach(span => {
            span.style.cursor = 'pointer';
        });
    }

    // Alert handling
    static showAlert(message, type = 'success', container, duration = 5000) {
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} mt-3`;
        alert.textContent = message;
        container.prepend(alert);
        setTimeout(() => alert.remove(), duration);
    }

    // Form submission state
    static setSubmitButtonState(button, isSubmitting) {
        if (button) {
            button.disabled = isSubmitting;
            button.innerHTML = isSubmitting ? 'Processing...' : 'Send Transfer';
        }
    }
}