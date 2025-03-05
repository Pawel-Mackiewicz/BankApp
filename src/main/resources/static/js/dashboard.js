document.addEventListener('DOMContentLoaded', function() {
    // Debug logs for Create Account form
    console.log('Script loaded');
    
    const createAccountForm = document.querySelector('form[action="/dashboard/new-account"]');
    console.log('Create Account Form:', createAccountForm);
    
    const createAccountButton = createAccountForm ? createAccountForm.querySelector('button') : null;
    console.log('Create Account Button:', createAccountButton);

    if (createAccountForm) {
        console.log('Adding submit event listener to form');
        createAccountForm.addEventListener('submit', function(e) {
            e.preventDefault();
            console.log('Form submitted and default prevented');
            
            if (createAccountButton) {
                console.log('Disabling button');
                createAccountButton.disabled = true;
                createAccountButton.innerHTML = 'Creating Account...';
                console.log('Button state:', createAccountButton.disabled);
                console.log('Button text:', createAccountButton.innerHTML);
                
                // Send form data using fetch
                fetch(createAccountForm.action, {
                    method: 'POST'
                })
                .then(response => {
                    console.log('Response received');
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error:', error);
                    createAccountButton.disabled = false;
                    createAccountButton.innerHTML = 'Create New Account';
                });
            }
        });
    }

    // Tab handling
    const tabs = document.querySelectorAll('.nav-link[data-toggle="tab"]');
    tabs.forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();
            tabs.forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-pane').forEach(p => {
                p.classList.remove('show', 'active');
            });
            
            this.classList.add('active');
            const paneId = this.getAttribute('href').substring(1);
            const pane = document.getElementById(paneId);
            pane.classList.add('show', 'active');
            
            Object.values(forms).forEach(form => {
                if (form) {
                    form.reset();
                    resetValidation(form);
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

    document.querySelectorAll('.iban-text').forEach(span => {
        span.style.cursor = 'pointer';
    });

    const forms = {
        own: document.getElementById('ownTransferForm'),
        internal: document.getElementById('internalTransferForm'),
        external: document.getElementById('externalTransferForm')
    };

    const sourceAccounts = {
        own: document.getElementById('ownSourceAccountId'),
        internal: document.getElementById('internalSourceAccountId'),
        external: document.getElementById('externalSourceAccountId')
    };

    const ownDestinationAccount = document.getElementById('ownDestinationAccountId');

    if (sourceAccounts.own) {
        sourceAccounts.own.addEventListener('change', function() {
            updateDestinationAccounts(this.value);
        });
    }

    const recipientMethodLabels = document.querySelectorAll('.btn-group-toggle .btn');
    recipientMethodLabels.forEach(label => {
        label.addEventListener('click', function() {
            const radio = this.querySelector('input[name="recipientMethod"]');
            if (radio) {
                toggleRecipientInputs(radio.value);
            }
        });
    });

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

    const emailInput = document.getElementById('internalRecipientEmail');
    if (emailInput) {
        emailInput.addEventListener('input', debounce(async function() {
            await validateEmail(emailInput);
        }, 500));
    }

    const amountInputs = document.querySelectorAll('input[id$="Amount"]');
    amountInputs.forEach(input => {
        input.addEventListener('input', function() {
            validateAmount(this);
        });
    });

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
        ownDestinationAccount.innerHTML = '<option value="">Select account</option>';
        const allAccounts = Array.from(sourceAccounts.own.options).slice(1);
        allAccounts.forEach(option => {
            if (option.value !== selectedSourceId) {
                ownDestinationAccount.add(option.cloneNode(true));
            }
        });
    }

    function toggleRecipientInputs(method) {
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

    function validateAmount(input) {
        const amount = parseFloat(input.value);
        const form = input.closest('form');
        let sourceAccountSelect = form.querySelector('select[id$="SourceAccountId"]') ||
                                form.querySelector('select[name="sourceIban"]');
        
        // Validate if source account is selected
        if (!sourceAccountSelect || !sourceAccountSelect.value) {
            setInvalid(input, 'Please select source account first');
            return false;
        }

        const selectedOption = sourceAccountSelect.selectedOptions[0];
        if (!selectedOption) {
            setInvalid(input, 'Invalid source account selection');
            return false;
        }

        // Extract and parse balance
        const balanceMatch = selectedOption.textContent.match(/Balance: ([\d,.]+)/);
        if (!balanceMatch) {
            console.error('Could not find balance in account text:', selectedOption.textContent);
            setInvalid(input, 'Error reading account balance');
            return false;
        }

        const balance = parseFloat(balanceMatch[1].replace(/,/g, '')); // Handle multiple commas in balance
        
        // Validate amount format
        if (!input.value.trim()) {
            setInvalid(input, 'Amount is required');
            return false;
        }

        if (isNaN(amount)) {
            setInvalid(input, 'Please enter a valid number');
            return false;
        }

        // Validate amount value
        if (amount <= 0) {
            setInvalid(input, 'Amount must be greater than 0');
            return false;
        }

        if (amount > 1000000) {
            setInvalid(input, 'Maximum transfer amount is 1,000,000 PLN');
            return false;
        }

        // Validate against balance
        if (amount > balance) {
            const formattedBalance = new Intl.NumberFormat('pl-PL', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }).format(balance);
            setInvalid(input, `Insufficient funds. Available balance: ${formattedBalance} PLN`);
            return false;
        }

        setValid(input);
        console.log('Amount validation passed:', { amount, balance });
        return true;
    }

    async function validateIban(input) {
        const iban = input.value.replace(/\s/g, '');
        
        if (!iban) {
            setInvalid(input, 'IBAN is required');
            return false;
        }

        const ibanPattern = /^PL[0-9]{26}$/;
        if (!ibanPattern.test(iban)) {
            setInvalid(input, 'Invalid IBAN format (should be PL followed by 26 digits)');
            return false;
        }

        try {
            const response = await fetch(`/api/validate-iban?iban=${iban}`);
            const data = await response.json();

            if (data.valid) {
                setValid(input);
                return true;
            } else {
                setInvalid(input, data.message || 'Invalid IBAN');
                return false;
            }
        } catch (error) {
            console.error('IBAN validation error:', error);
            setInvalid(input, 'Error validating IBAN');
            return false;
        }
    }

    async function validateEmail(input) {
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
                setInvalid(input, data.message || 'Invalid email');
                return false;
            }
        } catch (error) {
            console.error('Email validation error:', error);
            setInvalid(input, 'Error validating email');
            return false;
        }
    }

    function resetValidation(form) {
        const inputs = form.querySelectorAll ? form.querySelectorAll('input, select') : [form];
        inputs.forEach(input => {
            input.classList.remove('is-valid', 'is-invalid');
            
            // First try to find invalid-feedback in input-group
            const inputGroup = input.closest('.input-group') || input.parentElement;
            let feedback = inputGroup.querySelector('.invalid-feedback');
            
            // If not found in input-group, try next sibling
            if (!feedback) {
                feedback = input.nextElementSibling;
            }
            
            if (feedback && feedback.classList.contains('invalid-feedback')) {
                feedback.style.display = 'none';
                feedback.style.visibility = 'hidden';
                feedback.style.opacity = '0';
                feedback.textContent = '';
            }
        });
    }

    function setValid(input) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        
        // First try to find invalid-feedback in input-group
        const inputGroup = input.closest('.input-group') || input.parentElement;
        let feedback = inputGroup.querySelector('.invalid-feedback');
        
        // If not found in input-group, try next sibling
        if (!feedback) {
            feedback = input.nextElementSibling;
        }
        
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.style.display = 'none';
            feedback.style.visibility = 'hidden';
            feedback.style.opacity = '0';
        }
    }

    function setInvalid(input, message) {
        input.classList.remove('is-valid');
        input.classList.add('is-invalid');
        
        // First try to find invalid-feedback in input-group
        const inputGroup = input.closest('.input-group') || input.parentElement;
        let feedback = inputGroup.querySelector('.invalid-feedback');
        
        // If not found in input-group, try next sibling
        if (!feedback) {
            feedback = input.nextElementSibling;
        }
        
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.textContent = message;
            feedback.style.display = 'block';
            feedback.style.visibility = 'visible';
            feedback.style.opacity = '1';
            
            // Add some spacing for better visibility
            feedback.style.marginTop = '0.25rem';
        } else {
            console.error('No invalid-feedback element found for:', input);
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

    // Transfer form handling
    Object.values(forms).forEach(form => {
        if (form) {
            form.addEventListener('submit', async function(e) {
                e.preventDefault();

                // Reset validation state
                resetValidation(this);
                
                // Validate all inputs
                const inputs = this.querySelectorAll('input:not([type="hidden"]), select');
                let isValid = true;

                for (let input of inputs) {
                    if (input.disabled || input.style.display === 'none') continue;

                    if (input.type === 'email') {
                        const emailValid = await validateEmail(input);
                        isValid = emailValid && isValid;
                    } else if (input.id.includes('Iban')) {
                        const ibanValid = await validateIban(input);
                        isValid = ibanValid && isValid;
                    } else if (input.type === 'number') {
                        const amountValid = validateAmount(input);
                        isValid = amountValid && isValid;
                    } else if (input.tagName === 'SELECT' && !input.value) {
                        setInvalid(input, 'This field is required');
                        isValid = false;
                    }
                }

                if (!isValid) {
                    return;
                }

                // Get and disable submit button
                const submitButton = this.querySelector('button[type="submit"]');
                if (submitButton) {
                    submitButton.disabled = true;
                    submitButton.innerHTML = 'Processing...';
                }

                try {
                    // Create FormData and modify IBAN format if needed
                    const formData = new FormData(this);
                    const ibanValue = formData.get('recipientIban');
                    if (ibanValue) {
                        formData.set('recipientIban', ibanValue.replace(/\s/g, ''));
                    }

                    const response = await fetch(this.action, {
                        method: 'POST',
                        body: formData
                    });

                    const html = await response.text();
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');

                    // Update account summary and transfer form selects
                    const newAccounts = doc.querySelector('.account-summary');
                    const currentAccounts = document.querySelector('.account-summary');
                    console.log('Account Update:', {
                        newAccountsFound: !!newAccounts,
                        currentAccountsFound: !!currentAccounts,
                        newContent: newAccounts?.innerHTML
                    });
                    if (newAccounts && currentAccounts) {
                        currentAccounts.innerHTML = newAccounts.innerHTML;
                        console.log('Updated account summary');
                        
                        // Get all account cards from the new HTML
                        const accountCards = newAccounts.querySelectorAll('.account-card');
                        const accountData = Array.from(accountCards).map(card => {
                            const accountNumber = card.querySelector('h3 span').textContent;
                            const balance = card.querySelector('.details div span').textContent;
                            const iban = card.querySelector('.iban-text').getAttribute('data-iban');
                            const id = card.getAttribute('data-id');
                            return { accountNumber, balance, iban, id };
                        });
                        
                        // Update all source account selects in transfer forms
                        ['own', 'internal', 'external'].forEach(formType => {
                            const select = document.getElementById(`${formType}SourceAccountId`);
                            if (select) {
                                const selectedValue = select.value; // Store current selection
                                let options = '<option value="">Select account</option>';
                                
                                accountData.forEach(account => {
                                    console.log('Processing account:', {
                                        formType,
                                        accountNumber: account.accountNumber,
                                        iban: account.iban
                                    });
                                    
                                    // For own transfers use account ID, for others use IBAN
                                    const value = formType === 'own' ? account.id : account.iban;

                                    if (value) {
                                        const text = `Account ${account.accountNumber} - ${account.iban.replace(/(.{4})/g, '$1 ')} (Balance: ${account.balance})`;
                                        options += `<option value="${value}" ${value === selectedValue ? 'selected' : ''}>${text}</option>`;
                                    }
                                });
                                
                                select.innerHTML = options;
                                
                                // If this is the own transfer form, update destination account options
                                if (formType === 'own' && select.value) {
                                    updateDestinationAccounts(select.value);
                                }
                            }
                        });
                        
                        console.log('Updated transfer form selects');
                    }

                    // Update recent transactions
                    const newTransactions = doc.querySelector('.recent-transactions');
                    const currentTransactions = document.querySelector('.recent-transactions');
                    console.log('Transactions Update:', {
                        newTransactionsFound: !!newTransactions,
                        currentTransactionsFound: !!currentTransactions,
                        transactionsCount: newTransactions?.querySelectorAll('.transaction').length,
                        content: newTransactions?.innerHTML.slice(0, 100) + '...' // Show first 100 chars for debugging
                    });
                    if (newTransactions && currentTransactions) {
                        currentTransactions.innerHTML = newTransactions.innerHTML;
                        console.log('Updated recent transactions');
                        
                        // Re-attach event listeners for copy buttons if needed
                        document.querySelectorAll('.copy-iban-btn, .iban-text').forEach(element => {
                            element.addEventListener('click', function() {
                                const iban = this.getAttribute('data-iban');
                                copyIban(iban);
                            });
                        });
                    } else {
                        console.warn('Could not find transaction elements:', {
                            newTransactionsNull: newTransactions === null,
                            currentTransactionsNull: currentTransactions === null
                        });
                    }

                    // Check for success/error messages
                    const serverMessage = doc.querySelector('.alert.alert-success, .alert.alert-danger');
                    if (serverMessage) {
                        const alert = document.createElement('div');
                        alert.className = serverMessage.className;
                        alert.textContent = serverMessage.textContent;
                        this.prepend(alert);
                        setTimeout(() => alert.remove(), 5000);
                    }

                    // Reset form on success
                    if (response.ok) {
                        this.reset();
                    }
                } catch (error) {
                    console.error('Error:', error);
                    const alert = document.createElement('div');
                    alert.className = 'alert alert-danger mt-3';
                    alert.textContent = 'Transfer failed. Please try again.';
                    this.prepend(alert);
                    setTimeout(() => alert.remove(), 5000);
                } finally {
                    if (submitButton) {
                        submitButton.disabled = false;
                        submitButton.innerHTML = 'Send Transfer';
                    }
                }
            });
        }
    });
});