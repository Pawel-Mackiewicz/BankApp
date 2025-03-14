$(document).ready(function() {
    // Reset validation state on page load
    function resetValidationState() {
        $('input').removeClass('is-valid is-invalid');
    }

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

    // Validate firstname field: checks for non-empty and only allows Polish letters
    function validateName() {
        const firstname = $('#firstname').val();
        if (!validateField($('#firstname'), firstname.length >= 1, 'First name is required')) {
            return false;
        }
        const regex = /^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż]+$/;
        return validateField($('#firstname'), regex.test(firstname), 'First name must contain only letters');
    }

    // Validate lastname field: checks for non-empty and only allows Polish letters
    function validateLastname() {
        const lastname = $('#lastname').val();
        if (!validateField($('#lastname'), lastname.length >= 1, 'Last name is required')) {
            return false;
        }
        const regex = /^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż]+$/;
        return validateField($('#lastname'), regex.test(lastname), 'Last name must contain only letters');
    }

    // pesel validation
    function validatepesel() {
        const pesel = $('#pesel').val();
        const peselRegex = /^\d{11}$/;
        return validateField($('#pesel'), peselRegex.test(pesel), 'PESEL must be exactly 11 digits');
    }

    // Email validation
    function validateEmail() {
        const email = $('#email').val();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return validateField($('#email'), emailRegex.test(email), 'Please enter a valid email address');
    }

    // Phone number validation
    function validatePhoneNumber() {
        const phoneNumber = $('#phoneNumber').val();
        const phoneRegex = /^(\+48\d{9}|0\d{9}|[1-9]\d{8})$/;
        const isValid = phoneRegex.test(phoneNumber);
        let errorMessage = 'Invalid phone number format. ';
        
        if (!isValid) {
            if (phoneNumber.startsWith('+') && !phoneNumber.startsWith('+48')) {
                errorMessage += 'Only +48 prefix is allowed.';
            } else if (phoneNumber.startsWith('0') && phoneNumber.length !== 10) {
                errorMessage += 'When starting with 0, number must be exactly 10 digits.';
            } else if (!phoneNumber.startsWith('+') && !phoneNumber.startsWith('0') && phoneNumber.length !== 9) {
                errorMessage += 'Number must be exactly 9 digits when not using prefix.';
            } else {
                errorMessage += 'Use +48XXXXXXXXX, 0XXXXXXXXX or XXXXXXXXX format.';
            }
        }
        
        return validateField($('#phoneNumber'), isValid, errorMessage);
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

    // Password confirmation validation
    function validateConfirmPassword() {
        const password = $('#password').val();
        const confirmPassword = $('#confirmPassword').val();
        return validateField($('#confirmPassword'), 
            confirmPassword === password && confirmPassword.length > 0, 
            'Passwords do not match'
        );
    }

    // Date of birth validation
    function validateDateOfBirth() {
        const dateOfBirth = new Date($('#dateOfBirth').val());
        const today = new Date();
        
        if (dateOfBirth > today) {
            return validateField($('#dateOfBirth'), false, 'Date of birth cannot be in the future');
        }
        
        let age = today.getFullYear() - dateOfBirth.getFullYear();
        const monthDiff = today.getMonth() - dateOfBirth.getMonth();
        
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dateOfBirth.getDate())) {
            age--;
        }

        if (age > 120) {
            return validateField($('#dateOfBirth'), false, 'Age cannot exceed 120 years');
        }
        
        if (age < 18) {
            return validateField($('#dateOfBirth'), false, 'You must be at least 18 years old');
        }
        
        return validateField($('#dateOfBirth'), true, '');
    }

    // Reset validation state on page load
    resetValidationState();

    // Form submission validation
    $('form').on('submit', function(e) {
        // Reset validation states before checking
        resetValidationState();

        // Validate all fields
        const isNameValid = validateName();
        const isLastnameValid = validateLastname();
        const ispeselValid = validatepesel();
        const isEmailValid = validateEmail();
        const isPhoneNumberValid = validatePhoneNumber();
        const isPasswordValid = validatePassword();
        const isConfirmPasswordValid = validateConfirmPassword();
        const isDateOfBirthValid = validateDateOfBirth();

        // If any validation fails, prevent form submission
        if (!isNameValid || !isLastnameValid || !ispeselValid || !isEmailValid || 
            !isPhoneNumberValid || !isPasswordValid || !isConfirmPasswordValid || !isDateOfBirthValid) {
            e.preventDefault();
            e.stopPropagation();
            
            // Mark all invalid fields
            if (!isNameValid) $('#firstname').addClass('is-invalid');
            if (!isLastnameValid) $('#lastname').addClass('is-invalid');
            if (!ispeselValid) $('#pesel').addClass('is-invalid');
            if (!isEmailValid) $('#email').addClass('is-invalid');
            if (!isPhoneNumberValid) $('#phoneNumber').addClass('is-invalid');
            if (!isPasswordValid) $('#password').addClass('is-invalid');
            if (!isConfirmPasswordValid) $('#confirmPassword').addClass('is-invalid');
            if (!isDateOfBirthValid) $('#dateOfBirth').addClass('is-invalid');
        }
    });

    // Attach validation to input events
    $('#firstname').on('input', validateName);
    $('#lastname').on('input', validateLastname);
    $('#pesel').on('input', validatepesel);
    $('#email').on('input', validateEmail);
    $('#phoneNumber').on('input', validatePhoneNumber);
    $('#password').on('input', function() {
        validatePassword();
        validateConfirmPassword();
        updatePasswordRequirements($(this).val());
    });
    $('#confirmPassword').on('input', validateConfirmPassword);
    $('#dateOfBirth').on('change', validateDateOfBirth);

    // Handle server-side validation errors
    if ($('.alert-danger').length > 0) {
        resetValidationState();
        $('.form-control').each(function() {
            if ($(this).siblings('.invalid-feedback').text()) {
                $(this).addClass('is-invalid');
            }
        });
    }
});