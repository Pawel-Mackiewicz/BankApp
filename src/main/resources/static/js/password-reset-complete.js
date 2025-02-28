$(document).ready(function() {
    function validateField(field, condition, errorMessage) {
        if (!condition) {
            field.removeClass('is-valid').addClass('is-invalid');
            field.siblings('.invalid-feedback').text(errorMessage);
            return false;
        } else {
            field.removeClass('is-invalid').addClass('is-valid');
            return true;
        }
    }

    // Password validation
    function updatePasswordRequirements(password) {
        const requirements = {
            'length-check': password.length >= 8,
            'uppercase-check': /[A-Z]/.test(password),
            'lowercase-check': /[a-z]/.test(password),
            'number-check': /[0-9]/.test(password),
            'special-check': /[@$!%*?&]/.test(password)
        };

        Object.entries(requirements).forEach(([id, isMet]) => {
            const element = $(`#${id}`);
            if (isMet) {
                element.addClass('hidden');
            } else {
                element.removeClass('hidden');
            }
        });

        return Object.values(requirements).every(Boolean);
    }

    function validatePassword() {
        const password = $('#password').val();
        return validateField($('#password'), 
            updatePasswordRequirements(password),
            'Please meet all password requirements'
        );
    }

    function validateConfirmPassword() {
        const password = $('#password').val();
        const confirmPassword = $('#confirmPassword').val();
        return validateField($('#confirmPassword'), 
            confirmPassword === password && confirmPassword.length > 0, 
            'Passwords do not match'
        );
    }

    // Reset validation state on page load
    function resetValidationState() {
        $('input').removeClass('is-valid is-invalid');
        $('.requirement-item').removeClass('hidden');
    }

    // Form submission validation
    $('form').on('submit', function(e) {
        resetValidationState();

        const isPasswordValid = validatePassword();
        const isConfirmPasswordValid = validateConfirmPassword();

        if (!isPasswordValid || !isConfirmPasswordValid) {
            e.preventDefault();
            e.stopPropagation();
            
            if (!isPasswordValid) $('#password').addClass('is-invalid');
            if (!isConfirmPasswordValid) $('#confirmPassword').addClass('is-invalid');
        }
    });

    // Real-time validation
    $('#password').on('input', function() {
        validatePassword();
        if ($('#confirmPassword').val()) {
            validateConfirmPassword();
        }
    });
    
    $('#confirmPassword').on('input', validateConfirmPassword);

    // Initial state
    resetValidationState();
});