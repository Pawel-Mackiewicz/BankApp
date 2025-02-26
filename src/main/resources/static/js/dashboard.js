document.addEventListener('DOMContentLoaded', function() {
    // Tab handling
    const tabs = document.querySelectorAll('.nav-link[data-toggle="tab"]');
    tabs.forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();
            // Remove active class from all tabs and panes
            tabs.forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-pane').forEach(p => {
                p.classList.remove('show', 'active');
            });
            
            // Add active class to clicked tab and its pane
            this.classList.add('active');
            const paneId = this.getAttribute('href').substring(1);
            const pane = document.getElementById(paneId);
            pane.classList.add('show', 'active');
            
            // Clear all forms and reset validation when switching tabs
            Object.values(forms).forEach(form => {
                if (form) {
                    form.reset();
                    resetValidation(form);
                    // Reset radio buttons to default state (IBAN)
                    if (form.id === 'internalTransferForm') {
                        const ibanRadio = form.querySelector('input[value="iban"]');
                        if (ibanRadio) {
                            ibanRadio.checked = true;
                            toggleRecipientInputs('iban');
                        }
                    }
                }
            });
        });
    });

    // Copy IBAN functionality
    document.querySelectorAll('.copy-iban-btn, .iban-text').forEach(element => {
        element.addEventListener('click', function() {
            const iban = this.getAttribute('data-iban');
            copyIban(iban);
        });
    });

    // Add pointer cursor to IBAN text
    document.querySelectorAll('.iban-text').forEach(span => {
        span.style.cursor = 'pointer';
    });

    // Transfer form handling
    const forms = {
        own: document.getElementById('ownTransferForm'),
        internal: document.getElementById('internalTransferForm'),
        external: document.getElementById('externalTransferForm')
    };

    // Source account selections
    const sourceAccounts = {
        own: document.getElementById('ownSourceAccountId'),
        internal: document.getElementById('internalSourceAccountId'),
        external: document.getElementById('externalSourceAccountId')
    };

    // Destination account for own transfer
    const ownDestinationAccount = document.getElementById('ownDestinationAccountId');

    // Update destination accounts when source account changes
    if (sourceAccounts.own) {
        sourceAccounts.own.addEventListener('change', function() {
            updateDestinationAccounts(this.value);
        });
    }

    // Handle internal transfer recipient method switch
    const recipientMethodLabels = document.querySelectorAll('.btn-group-toggle .btn');
    recipientMethodLabels.forEach(label => {
        label.addEventListener('click', function() {
            const radio = this.querySelector('input[name="recipientMethod"]');
            if (radio) {
                toggleRecipientInputs(radio.value);
            }
        });
    });

    // IBAN validation
    const ibanInputs = {
        internal: document.getElementById('internalRecipientIban'),
        external: document.getElementById('externalRecipientIban')
    };

    Object.values(ibanInputs).forEach(input => {
        if (input) {
            input.addEventListener('input', async function() {
                await validateIban(this);
            });
        }
    });

    // Email validation
    const emailInput = document.getElementById('internalRecipientEmail');
    if (emailInput) {
        emailInput.addEventListener('input', debounce(async function() {
            await validateEmail(emailInput);
        }, 500));
    }

    // Amount validation for all forms
    const amountInputs = document.querySelectorAll('input[id$="Amount"]');
    amountInputs.forEach(input => {
        input.addEventListener('input', function() {
            validateAmount(this);
        });
    });

    // Helper Functions
    function copyIban(iban) {
        navigator.clipboard.writeText(iban).then(() => {
            const notification = document.getElementById('copyNotification');
            notification.classList.add('show');
            setTimeout(() => {
                notification.classList.remove('show');
            }, 2000);
        }).catch(err => {
            console.error('Błąd podczas kopiowania:', err);
            alert('Nie udało się skopiować IBAN. Spróbuj ponownie.');
        });
    }

    function updateDestinationAccounts(selectedSourceId) {
        if (!ownDestinationAccount) return;

        // Clear current options
        ownDestinationAccount.innerHTML = '<option value="">Select account</option>';

        // Get all accounts from source account select
        const allAccounts = Array.from(sourceAccounts.own.options).slice(1); // Skip first empty option

        // Add all accounts except the selected source account
        allAccounts.forEach(option => {
            if (option.value !== selectedSourceId) {
                ownDestinationAccount.add(option.cloneNode(true));
            }
        });
    }

    function toggleRecipientInputs(method) {
        console.log('toggleRecipientInputs called with method:', method);
        const ibanDiv = document.querySelector('.recipient-iban');
        const emailDiv = document.querySelector('.recipient-email');
        const ibanInput = document.getElementById('internalRecipientIban');
        const emailInput = document.getElementById('internalRecipientEmail');

        if (method === 'iban') {
            ibanDiv.style.display = 'block';
            emailDiv.style.display = 'none';
            ibanInput.required = true;
            ibanInput.disabled = false;
            emailInput.required = false;
            emailInput.disabled = true;
            emailInput.value = '';
            resetValidation(emailInput);
        } else {
            ibanDiv.style.display = 'none';
            emailDiv.style.display = 'block';
            emailInput.required = true;
            emailInput.disabled = false;
            ibanInput.required = false;
            ibanInput.disabled = true;
            ibanInput.value = '';
            resetValidation(ibanInput);
        }
    }

    async function validateIban(input) {
        const iban = input.value.replace(/\s/g, '');
        console.log('Validating IBAN:', iban);
        
        // Sprawdź czy IBAN jest wymagany dla aktualnej metody
        const recipientMethod = document.querySelector('input[name="recipientMethod"]:checked').value;
        if (recipientMethod !== 'iban') {
            console.log('IBAN validation skipped - email method selected');
            return true;
        }

        if (!iban) {
            console.log('IBAN validation failed - empty value');
            setInvalid(input, 'IBAN is required');
            return false;
        }

        // Sprawdź format IBAN (PL + 26 cyfr)
        const ibanPattern = /^PL[0-9]{26}$/;
        if (!ibanPattern.test(iban)) {
            console.log('IBAN validation failed - invalid format');
            setInvalid(input, 'Invalid IBAN format (should be PL followed by 26 digits)');
            return false;
        }

        try {
            console.log('Sending IBAN validation request');
            const response = await fetch(`/api/validate-iban?iban=${iban}`);
            const data = await response.json();
            console.log('IBAN validation response:', data);

            if (data.valid) {
                setValid(input);
                return true;
            } else {
                setInvalid(input, data.message || 'Invalid IBAN format');
                return false;
            }
        } catch (error) {
            console.error('IBAN validation error:', error);
            setInvalid(input, 'Error validating IBAN');
            return false;
        }
    }

    async function validateEmail(input) {
        if (!input) return false;
        const email = input.value.trim();
        if (!email) {
            setInvalid(input, 'Email is required');
            return false;
        }

        if (!isValidEmailFormat(email)) {
            setInvalid(input, 'Invalid email format');
            return false;
        }

        try {
            const response = await fetch(`/api/validate-email?email=${email}`);
            const data = await response.json();

            if (data.valid) {
                setValid(input);
                return true;
            } else {
                setInvalid(input, data.message || 'No account found for this email');
                return false;
            }
        } catch (error) {
            console.error('Email validation error:', error);
            setInvalid(input, 'Error validating email');
            return false;
        }
    }

    function validateAmount(input) {
        const amount = parseFloat(input.value);
        const form = input.closest('form');
        let sourceAccountSelect;
        
        // Użyj odpowiedniego selektora w zależności od typu formularza
        if (form.id === 'ownTransferForm') {
            sourceAccountSelect = form.querySelector('select[id$="SourceAccountId"]');
        } else {
            sourceAccountSelect = form.querySelector('select[name="sourceIban"]');
        }
        
        const selectedOption = sourceAccountSelect.selectedOptions[0];
        
        if (!selectedOption.value) {
            setInvalid(input, 'Please select source account first');
            return false;
        }

        const balance = parseFloat(selectedOption.textContent.match(/Balance: ([\d,.]+)/)[1].replace(',', ''));
        
        if (isNaN(amount) || amount <= 0) {
            setInvalid(input, 'Amount must be greater than 0');
            return false;
        }

        if (amount > balance) {
            setInvalid(input, 'Insufficient funds');
            return false;
        }

        setValid(input);
        return true;
    }

    function resetValidation(form) {
        const inputs = form.querySelectorAll('input, select');
        inputs.forEach(input => {
            input.classList.remove('is-valid', 'is-invalid');
            const feedback = input.nextElementSibling;
            if (feedback && feedback.classList.contains('invalid-feedback')) {
                feedback.style.display = 'none';
            }
        });
    }

    function setValid(input) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        const feedback = input.nextElementSibling;
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.style.display = 'none';
        }
    }

    function setInvalid(input, message) {
        input.classList.remove('is-valid');
        input.classList.add('is-invalid');
        const feedback = input.nextElementSibling;
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.textContent = message;
            feedback.style.display = 'block';
        }
    }

    function isValidEmailFormat(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    function debounce(func, wait) {
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

    // Form submission handling
    Object.values(forms).forEach(form => {
        if (form) {
            form.addEventListener('submit', async function(e) {
                e.preventDefault();
                
                // Reset validation state
                resetValidation(this);
                
                // Validate all inputs
                const inputs = this.querySelectorAll('input, select');
                let isValid = true;

                console.log('Starting form validation');
                for (let input of inputs) {
                    console.log('Validating input:', input.id, {
                        type: input.type,
                        value: input.value,
                        disabled: input.disabled,
                        display: input.style.display,
                        required: input.required
                    });

                    if (input.type === 'email' && !input.disabled && input.style.display !== 'none') {
                        const emailValid = await validateEmail(input);
                        console.log('Email validation result:', emailValid);
                        isValid = emailValid && isValid;
                    } else if (input.id.includes('Iban') && !input.disabled && input.style.display !== 'none') {
                        const ibanValid = await validateIban(input);
                        console.log('IBAN validation result:', ibanValid);
                        isValid = ibanValid && isValid;
                    } else if (input.type === 'number') {
                        const amountValid = validateAmount(input);
                        console.log('Amount validation result:', amountValid);
                        isValid = amountValid && isValid;
                    } else if (input.tagName === 'SELECT' && !input.value) {
                        console.log('Select validation failed - no value selected');
                        setInvalid(input, 'This field is required');
                        isValid = false;
                    }
                }
                console.log('Final validation result:', isValid);

                if (isValid) {
                    try {
                        console.log('=== Transaction Flow Start ===');
                        console.log('Form validation passed');
                        const formData = new FormData(this);
                        
                        // Log detailed form data
                        console.log('Form data details:');
                        for (let [key, value] of formData.entries()) {
                            console.log(`Field: ${key}, Value: ${value}, Type: ${typeof value}`);
                        }
                        console.log('Form ID:', this.id);
                        console.log('Form Action:', this.action);
                        
                        console.log('Sending request to:', this.action);
                        // Przygotuj listę wymaganych pól w zależności od typu formularza
                        let requiredFields = ['amount', 'title'];
                        
                        if (this.id === 'ownTransferForm') {
                            requiredFields.push('sourceAccountId');
                        } else {
                            requiredFields.push('sourceIban');
                        }

                        const recipientMethod = formData.get('recipientMethod');
                        
                        // Dodaj logi aby sprawdzić typ formularza i recipientMethod
                        console.log('Form type:', this.id);
                        console.log('Recipient method:', recipientMethod);

                        if (this.id === 'internalTransferForm') {
                            if (recipientMethod === 'iban') {
                                requiredFields.push('recipientIban');
                            } else {
                                requiredFields.push('recipientEmail');
                            }
                        } else if (this.id === 'externalTransferForm') {
                            requiredFields.push('recipientIban', 'recipientName');
                        }

                        const missingFields = requiredFields.filter(field => !formData.get(field));
                        if (missingFields.length > 0) {
                            console.error('Missing required fields:', missingFields);
                            throw new Error(`Missing required fields: ${missingFields.join(', ')}`);
                        }

                        const response = await fetch(this.action, {
                            method: 'POST',
                            headers: {
                                'Accept': 'application/json'
                            },
                            body: formData
                        });

                        console.log('Response status:', response.status);
                        const responseText = await response.text();
                        console.log('Response text:', responseText);

                        if (!response.ok) {
                            throw new Error(`Transfer failed: ${response.status} ${responseText}`);
                        }

                        // Add flash message to form
                        const successAlert = document.createElement('div');
                        successAlert.className = 'alert alert-success mt-3';
                        successAlert.textContent = 'Transfer completed successfully.';
                        this.prepend(successAlert);
                        
                        // Reset form
                        this.reset();
                        resetValidation(this);

                        // Update account summary by fetching just the account summary part
                        fetch('/dashboard')
                            .then(response => response.text())
                            .then(html => {
                                const parser = new DOMParser();
                                const doc = parser.parseFromString(html, 'text/html');
                                // Update account summary
                                const newAccounts = doc.querySelector('.account-summary');
                                if (newAccounts) {
                                    const currentAccounts = document.querySelector('.account-summary');
                                    if (currentAccounts) {
                                        currentAccounts.innerHTML = newAccounts.innerHTML;
                                    }
                                }

                                // Update recent transactions
                                const newTransactions = doc.querySelector('.column:first-child');
                                if (newTransactions) {
                                    const currentTransactions = document.querySelector('.column:first-child');
                                    if (currentTransactions) {
                                        currentTransactions.innerHTML = newTransactions.innerHTML;
                                    }
                                }

                                // Re-attach IBAN copy functionality to new elements
                                document.querySelectorAll('.copy-iban-btn, .iban-text').forEach(element => {
                                    element.addEventListener('click', function() {
                                        const iban = this.getAttribute('data-iban');
                                        copyIban(iban);
                                    });
                                });
                            })
                            .catch(error => {
                                console.error('Error updating account summary:', error);
                            });

                        // Remove success message after 5 seconds
                        setTimeout(() => successAlert.remove(), 5000);

                    } catch (error) {
                        console.error('Transfer error:', error);
                        const errorAlert = document.createElement('div');
                        errorAlert.className = 'alert alert-danger mt-3';
                        errorAlert.textContent = 'Transfer failed. Please try again.';
                        this.prepend(errorAlert);
                        setTimeout(() => errorAlert.remove(), 5000);
                    }
                } else {
                    console.log('Form validation failed');
                }
                 // Debug: Log flash messages from server if any
                 const flashSuccess = document.querySelector('.alert.alert-success');
                 const flashError = document.querySelector('.alert.alert-danger');
                 if (flashSuccess) {
                     console.log("Flash success message found:", flashSuccess.textContent.trim());
                 }
                 if (flashError) {
                     console.log("Flash error message found:", flashError.textContent.trim());
                 }
            });
        }
    });
});