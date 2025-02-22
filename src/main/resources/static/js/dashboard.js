document.addEventListener('DOMContentLoaded', function() {
    // Dodaj nasłuchiwanie na wszystkie przyciski kopiowania
    document.querySelectorAll('.copy-iban-btn').forEach(button => {
        button.addEventListener('click', function() {
            const iban = this.getAttribute('data-iban');
            copyIban(iban);
        });
    });

    // Dodaj nasłuchiwanie na wszystkie spany z IBAN-em
    document.querySelectorAll('.iban-text').forEach(span => {
        span.addEventListener('click', function() {
            const iban = this.getAttribute('data-iban');
            copyIban(iban);
        });
    });

        // Dodaj nasłuchiwanie na wszystkie spany z IBAN-em
    document.querySelectorAll('.iban-text').forEach(span => {
        span.style.cursor = 'pointer'; // Dodaj styl kursora
        span.addEventListener('click', function() {
            const iban = this.getAttribute('data-iban');
            copyIban(iban);
        });
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