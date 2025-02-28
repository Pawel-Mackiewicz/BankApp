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

    function validateEmail() {
        const email = $('#email').val();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return validateField($('#email'), emailRegex.test(email), 'Please enter a valid email address');
    }

    // Form submission validation
    $('form').on('submit', function(e) {
        const isEmailValid = validateEmail();
        
        if (!isEmailValid) {
            e.preventDefault();
            e.stopPropagation();
        }
    });

    // Real-time email validation
    $('#email').on('input', validateEmail);
});