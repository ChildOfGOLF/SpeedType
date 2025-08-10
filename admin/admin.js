const API_URL = "http://localhost:8080/admin/texts";

document.addEventListener('DOMContentLoaded', function() {
    checkAdminAuth();
});

function checkAdminAuth() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (!token || !username) {
        alert('Please login first');
        window.location.href = '../login.html';
        return;
    }

    verifyAdminToken(token);
}

async function verifyAdminToken(token) {
    try {
        const response = await fetch('http://localhost:8080/texts?difficulty=easy', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.status === 401 || response.status === 403) {
            alert('Session expired. Please login again');
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            window.location.href = '../login.html';
            return;
        }

        loadTexts();
    } catch (error) {
        console.error('Error verifying token:', error);
        alert('Connection error. Please try again');
    }
}

function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

async function loadTexts() {
    try {
        const response = await fetch(API_URL, {
            headers: getAuthHeaders()
        });

        if (response.ok) {
            const texts = await response.json();
            displayTexts(texts);
        } else {
            throw new Error('Failed to load texts');
        }
    } catch (error) {
        console.error('Error loading texts:', error);
        document.getElementById('textsTableBody').innerHTML =
            '<tr><td colspan="5" class="error">Error loading texts. Please refresh the page.</td></tr>';
    }
}

function displayTexts(texts) {
    const tbody = document.getElementById('textsTableBody');

    if (texts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="loading">No texts available</td></tr>';
        return;
    }

    tbody.innerHTML = texts.map(text => `
        <tr>
            <td>${text.id}</td>
            <td>${text.content.substring(0, 100)}${text.content.length > 100 ? '...' : ''}</td>
            <td><span class="difficulty-badge ${text.difficulty}">${text.difficulty}</span></td>
            <td><span class="language-badge">${text.language === 'en' ? 'English' : 'Russian'}</span></td>
            <td>
                <button class="btn btn-sm btn-secondary" onclick="editText(${text.id})">Edit</button>
                <button class="btn btn-sm btn-danger" onclick="deleteText(${text.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

document.getElementById('addTextForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const content = document.getElementById('textContent').value;
    const difficulty = document.getElementById('textDifficulty').value;
    const language = document.getElementById('textLanguage').value;

    if (!content.trim()) {
        alert('Please enter text content');
        return;
    }

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ content, difficulty, language })
        });

        if (response.ok) {
            alert('Text added successfully!');
            document.getElementById('addTextForm').reset();
            loadTexts();
        } else {
            const errorData = await response.text();
            alert(`Failed to add text: ${errorData}`);
        }
    } catch (error) {
        console.error('Error adding text:', error);
        alert('Error adding text. Please try again.');
    }
});

async function deleteText(id) {
    if (!confirm('Are you sure you want to delete this text? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE",
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });

        if (!response.ok) {
            throw new Error(`Failed to delete text: ${response.status}`);
        }

        loadTexts();
        showNotification('Text deleted successfully!', 'success');
    } catch (error) {
        console.error('Error deleting text:', error);
        showNotification('Failed to delete text. Please try again.', 'error');
    }
}

function editText(id) {
    fetch(`${API_URL}/${id}`, {
        headers: getAuthHeaders()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch text');
        }
        return response.json();
    })
    .then(text => {
        document.getElementById('editTextId').value = text.id;
        document.getElementById('editTextContent').value = text.content;
        document.getElementById('editTextDifficulty').value = text.difficulty;
        document.getElementById('editTextLanguage').value = text.language || 'en';
        document.getElementById('editTextForm').classList.remove('hidden');
        document.getElementById('editTextForm').scrollIntoView({ behavior: 'smooth' });
    })
    .catch(error => {
        console.error('Error fetching text:', error);
        alert('Error loading text for editing');
    });
}

document.getElementById("editTextForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const id = document.getElementById("editTextId").value;
    const content = document.getElementById("editTextContent").value;
    const difficulty = document.getElementById("editTextDifficulty").value;
    const language = document.getElementById("editTextLanguage").value;
    const submitBtn = event.target.querySelector('button[type="submit"]');

    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = 'Saving...';
    submitBtn.disabled = true;

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "PUT",
            headers: getAuthHeaders(),
            body: JSON.stringify({ content, difficulty, language })
        });

        if (!response.ok) {
            throw new Error(`Failed to update text: ${response.status}`);
        }

        document.getElementById("editTextForm").classList.add("hidden");
        loadTexts();
        showNotification('Text updated successfully!', 'success');
    } catch (error) {
        console.error('Error updating text:', error);
        showNotification('Failed to update text. Please try again.', 'error');
    } finally {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
});

function showNotification(message, type) {
    const existingAlert = document.querySelector('.alert');
    if (existingAlert) {
        existingAlert.remove();
    }

    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;

    const adminContent = document.querySelector('.admin-content');
    adminContent.insertBefore(alert, adminContent.firstChild);

    setTimeout(() => {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}

function logout() {
    if (confirm('Return to main page?')) {
        window.location.href = '../index.html';
    }
}
