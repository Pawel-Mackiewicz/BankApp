// Form handlers module
import {UIHelper, Validator} from './validation.js';
import {DOMHelper} from './dom-helper.js';

export class FormManager {
    constructor() {
        this.forms = {
            own: document.getElementById('ownTransferForm'),
            internal: document.getElementById('internalTransferForm'),
            external: document.getElementById('externalTransferForm')
        };

        this.sourceAccounts = {
            own: document.getElementById('ownSourceAccountId'),
            internal: document.getElementById('internalSourceAccountId'),
            external: document.getElementById('externalSourceAccountId')
        };

        this.initializeEventListeners();
    }

    initializeEventListeners() {
        // Initialize form submissions
        Object.values(this.forms).forEach(form => {
            if (form) {
                form.addEventListener('submit', (e) => this.handleFormSubmit(e));
            }
        });

        // Initialize source account changes
        if (this.sourceAccounts.own) {
            this.sourceAccounts.own.addEventListener('change', (e) => {
                DOMHelper.updateDestinationAccounts(e.target.value);
            });
        }

        // Initialize amount validation
        const amountInputs = document.querySelectorAll('input[id$="Amount"]');
        amountInputs.forEach(input => {
            input.addEventListener('input', () => {
                Validator.validateInput(input);
            });
        });

        // Initialize IBAN validation
        const ibanInputs = {
            internal: document.getElementById('internalRecipientIban'),
            external: document.getElementById('externalRecipientIban')
        };

        Object.values(ibanInputs).forEach(input => {
            if (input) {
                input.addEventListener('input', async () => {
                    await Validator.validateInput(input);
                });
            }
        });

        // Initialize email validation
        const emailInput = document.getElementById('internalRecipientEmail');
        if (emailInput) {
            emailInput.addEventListener('input', this.debounce(async () => {
                await Validator.validateInput(emailInput);
            }, 500));
        }

        // Initialize recipient method toggle
        this.initializeRecipientMethodToggle();
    }

    initializeRecipientMethodToggle() {
        const recipientMethodLabels = document.querySelectorAll('.btn-group-toggle .btn');
        recipientMethodLabels.forEach(label => {
            label.addEventListener('click', () => {
                const radio = label.querySelector('input[name="recipientMethod"]');
                if (radio) {
                    DOMHelper.toggleRecipientInputs(radio.value);
                }
            });
        });
    }

    async handleFormSubmit(e) {
        e.preventDefault();
        const form = e.target;

        // Reset validation state
        UIHelper.resetValidation(form);
        
        // Validate all inputs
        const inputs = form.querySelectorAll('input:not([type="hidden"]), select');
        let isValid = true;

        for (let input of inputs) {
            if (input.disabled || input.style.display === 'none') continue;
            const inputValid = await Validator.validateInput(input);
            isValid = inputValid && isValid;
        }

        if (!isValid) return;

        // Handle form submission
        const submitButton = form.querySelector('button[type="submit"]');
        if (submitButton) {
            DOMHelper.setSubmitButtonState(submitButton, true);
        }

        try {
            const response = await this.submitForm(form);
            await this.handleFormResponse(response, form);
        } catch (error) {
            console.error('Error:', error);
            DOMHelper.showAlert('Transfer failed. Please try again.', 'danger', form);
        } finally {
            if (submitButton) {
                DOMHelper.setSubmitButtonState(submitButton, false);
            }
        }
    }

    async submitForm(form) {
        const formData = new FormData(form);
        const ibanValue = formData.get('recipientIban');
        if (ibanValue) {
            formData.set('recipientIban', ibanValue.replace(/\s/g, ''));
        }

        return await fetch(form.action, {
            method: 'POST',
            body: formData
        });
    }

    async handleFormResponse(response, form) {
        const html = await response.text();
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');

        if (response.ok) {
            // Update account summary first
            const newAccounts = doc.querySelector('.account-summary');
            if (newAccounts) {
                // This will trigger the update of all transfer form selects
                DOMHelper.updateAccountSummary(newAccounts.innerHTML);
            }

            // Update recent transactions
            const newTransactions = doc.querySelector('.recent-transactions');
            if (newTransactions) {
                DOMHelper.updateRecentTransactions(newTransactions.innerHTML);
            }

            // Handle success message
            const successMessage = doc.querySelector('.alert.alert-success');
            if (successMessage) {
                DOMHelper.showAlert(successMessage.textContent, 'success', form);
            }

            // Reset form
            form.reset();
            UIHelper.resetValidation(form);

            // If this is the internal transfer form, reset to default IBAN state
            if (form.id === 'internalTransferForm') {
                const ibanRadio = form.querySelector('input[value="iban"]');
                if (ibanRadio) {
                    ibanRadio.checked = true;
                    DOMHelper.toggleRecipientInputs('iban');
                }
            }
        } else {
            // Handle error message
            const errorMessage = doc.querySelector('.alert.alert-danger');
            if (errorMessage) {
                DOMHelper.showAlert(errorMessage.textContent, 'danger', form);
            } else {
                DOMHelper.showAlert('An error occurred during the transfer.', 'danger', form);
            }
        }
    }

    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
}

// Klasa pozostaje bez zmian do momentu metody handleSubmit
export class CreateAccountForm {
    constructor() {
        this.form = document.querySelector('form[action="/dashboard/new-account"]');
        this.button = this.form ? this.form.querySelector('button') : null;
        this.initialize();
    }

    initialize() {
        if (this.form) {
            this.form.addEventListener('submit', (e) => this.handleSubmit(e));
        }
    }

    handleSubmit(e) {
        e.preventDefault();
        if (!this.button) return;

        this.button.disabled = true;
        this.button.innerHTML = 'Creating Account...';

        fetch(this.form.action, {
            method: 'POST'
        })
            .then(async response => {
                // Pobierz odpowiedź HTML
                const html = await response.text();
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');

                if (response.ok) {
                    // Aktualizuj sekcję kont
                    const newAccounts = doc.querySelector('.account-summary');
                    if (newAccounts) {
                        DOMHelper.updateAccountSummary(newAccounts.innerHTML);
                    }

                    // Aktualizuj listę transakcji (jeśli potrzebne)
                    const newTransactions = doc.querySelector('.recent-transactions');
                    if (newTransactions) {
                        DOMHelper.updateRecentTransactions(newTransactions.innerHTML);
                    }

                    // Wyświetl komunikat sukcesu z odpowiedzi
                    const successMessage = doc.querySelector('.alert.alert-success');
                    if (successMessage) {
                        const accountForm = document.querySelector('.account-summary');
                        DOMHelper.showAlert(successMessage.textContent, 'success', accountForm);
                    }
                } else {
                    // Obsłuż błędy
                    const errorMessage = doc.querySelector('.alert.alert-danger');
                    if (errorMessage) {
                        const accountForm = document.querySelector('.account-summary');
                        DOMHelper.showAlert(errorMessage.textContent, 'danger', accountForm);
                    } else {
                        throw new Error('Failed to create account');
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                const accountForm = document.querySelector('.account-summary');
                DOMHelper.showAlert('Failed to create account. Please try again.', 'danger', accountForm);
            })
            .finally(() => {
                this.button.disabled = false;
                this.button.innerHTML = 'Create New Account';
            });
    }
}