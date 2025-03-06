// Form validation module
export class Validator {
    static async validateInput(input) {
        if (input.type === 'email') {
            return await EmailValidator.validate(input);
        } else if (input.id.includes('Iban')) {
            return await IbanValidator.validate(input);
        } else if (input.type === 'number') {
            return AmountValidator.validate(input);
        } else if (input.tagName === 'SELECT' && !input.value) {
            UIHelper.setInvalid(input, 'This field is required');
            return false;
        }
        return true;
    }
}

class EmailValidator {
    static isValidEmailFormat(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    static async validate(input) {
        const email = input.value.trim();
        if (!email) {
            UIHelper.setInvalid(input, 'Email is required');
            return false;
        }

        if (!this.isValidEmailFormat(email)) {
            UIHelper.setInvalid(input, 'Invalid email format');
            return false;
        }

        try {
            const response = await fetch(`/api/validate-email?email=${email}`);
            const data = await response.json();

            if (data.valid) {
                UIHelper.setValid(input);
                return true;
            } else {
                UIHelper.setInvalid(input, data.message || 'Invalid email');
                return false;
            }
        } catch (error) {
            console.error('Email validation error:', error);
            UIHelper.setInvalid(input, 'Error validating email');
            return false;
        }
    }
}

class IbanValidator {
    static async validate(input) {
        const iban = input.value.replace(/\s/g, '');
        
        if (!iban) {
            UIHelper.setInvalid(input, 'IBAN is required');
            return false;
        }

        const ibanPattern = /^PL[0-9]{26}$/;
        if (!ibanPattern.test(iban)) {
            UIHelper.setInvalid(input, 'Invalid IBAN format (should be PL followed by 26 digits)');
            return false;
        }

        try {
            const response = await fetch(`/api/validate-iban?iban=${iban}`);
            const data = await response.json();

            if (data.valid) {
                UIHelper.setValid(input);
                return true;
            } else {
                UIHelper.setInvalid(input, data.message || 'Invalid IBAN');
                return false;
            }
        } catch (error) {
            console.error('IBAN validation error:', error);
            UIHelper.setInvalid(input, 'Error validating IBAN');
            return false;
        }
    }
}

class AmountValidator {
    static validate(input) {
        const amount = parseFloat(input.value);
        const form = input.closest('form');
        let sourceAccountSelect = form.querySelector('select[id$="SourceAccountId"]') ||
                               form.querySelector('select[name="sourceIban"]');
        
        if (!this.validateSourceAccount(sourceAccountSelect)) {
            UIHelper.setInvalid(input, 'Please select source account first');
            return false;
        }

        const balance = this.getAccountBalance(sourceAccountSelect);
        if (balance === null) {
            UIHelper.setInvalid(input, 'Error reading account balance');
            return false;
        }

        if (!this.validateAmountValue(amount, balance, input)) {
            return false;
        }

        UIHelper.setValid(input);
        console.log('Amount validation passed:', { amount, balance });
        return true;
    }

    static validateSourceAccount(select) {
        if (!select || !select.value || !select.selectedOptions[0]) {
            return false;
        }
        return true;
    }

    static getAccountBalance(select) {
        const selectedOption = select.selectedOptions[0];
        const balanceMatch = selectedOption.textContent.match(/Balance: ([\d,.]+)/);
        if (!balanceMatch) {
            console.error('Could not find balance in account text:', selectedOption.textContent);
            return null;
        }
        return parseFloat(balanceMatch[1].replace(/,/g, ''));
    }

    static validateAmountValue(amount, balance, input) {
        if (!input.value.trim()) {
            UIHelper.setInvalid(input, 'Amount is required');
            return false;
        }

        if (isNaN(amount)) {
            UIHelper.setInvalid(input, 'Please enter a valid number');
            return false;
        }

        if (amount <= 0) {
            UIHelper.setInvalid(input, 'Amount must be greater than 0');
            return false;
        }

        if (amount > 1000000) {
            UIHelper.setInvalid(input, 'Maximum transfer amount is 1,000,000 PLN');
            return false;
        }

        if (amount > balance) {
            const formattedBalance = new Intl.NumberFormat('pl-PL', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }).format(balance);
            UIHelper.setInvalid(input, `Insufficient funds. Available balance: ${formattedBalance} PLN`);
            return false;
        }

        return true;
    }
}

export class UIHelper {
    static setValid(input) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        this.updateFeedback(input, '', false);
    }

    static setInvalid(input, message) {
        input.classList.remove('is-valid');
        input.classList.add('is-invalid');
        this.updateFeedback(input, message, true);
    }

    static updateFeedback(input, message, isError) {
        const inputGroup = input.closest('.input-group') || input.parentElement;
        let feedback = inputGroup.querySelector('.invalid-feedback') || input.nextElementSibling;
        
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.textContent = message;
            feedback.style.display = isError ? 'block' : 'none';
            feedback.style.visibility = isError ? 'visible' : 'hidden';
            feedback.style.opacity = isError ? '1' : '0';
            if (isError) {
                feedback.style.marginTop = '0.25rem';
            }
        }
    }

    static resetValidation(form) {
        const inputs = form.querySelectorAll ? form.querySelectorAll('input, select') : [form];
        inputs.forEach(input => {
            input.classList.remove('is-valid', 'is-invalid');
            const feedback = this.getFeedbackElement(input);
            if (feedback) {
                this.updateFeedback(input, '', false);
            }
        });
    }

    static getFeedbackElement(input) {
        const inputGroup = input.closest('.input-group') || input.parentElement;
        return inputGroup.querySelector('.invalid-feedback') || input.nextElementSibling;
    }
}