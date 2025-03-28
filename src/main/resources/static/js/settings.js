document.addEventListener('DOMContentLoaded', function() {
    loadUserSettings();
});

function loadUserSettings() {
    const loadingElement = document.getElementById('loading');
    const settingsContent = document.getElementById('settingsContent');

    fetch('/api/settings/user')
        .then(response => {
            if (!response.ok) throw new Error('Failed to load user data');
            return response.json();
        })
        .then(user => {
            // Update user data
            document.getElementById('firstname').textContent = user.firstname;
            document.getElementById('lastname').textContent = user.lastname;
            document.getElementById('phoneNumber').textContent = user.phoneNumber;
            document.getElementById('email').textContent = user.email;
            document.getElementById('username').textContent = user.username;

            // Hide loading and show content
            loadingElement.style.display = 'none';
            settingsContent.style.display = 'block';
        })
        .catch(error => {
            console.error('Error:', error);
            loadingElement.textContent = 'Failed to load user data. Please try again later.';
        });
}

// Handle username change form
document.getElementById('changeUsernameForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const data = {
        newUsername: document.getElementById('newUsername').value
    };

    fetch('/api/settings/change-username', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to update username');
        }
        alert('Username has been updated successfully');
        document.getElementById('changeUsernameForm').reset();
        loadUserSettings(); // Refresh displayed data
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to update username');
    });
});

// Handle password change form
document.getElementById('changePasswordForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        alert('New password and confirmation do not match');
        return;
    }

    const data = {
        currentPassword: document.getElementById('currentPassword').value,
        password: password,
        confirmPassword: confirmPassword
    };

    fetch('/api/settings/change-password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text) });
        }
        return response.text();
    })
    .then(message => {
        alert(message);
        document.getElementById('changePasswordForm').reset();
        // Wykonaj wylogowanie przez przekierowanie na /logout
        window.location.href = '/logout';
    })
    .catch(error => {
        console.error('Error:', error);
        alert(error.message || 'Failed to update password');
    });
});