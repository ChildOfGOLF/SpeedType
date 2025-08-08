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
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const texts = await response.json();
        const tableBody = document.getElementById("textsTableBody");

        if (texts.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 40px; color: #6c757d;">No texts found. Add your first text above!</td></tr>';
            return;
        }

        tableBody.innerHTML = "";
        texts.forEach(text => {
            const row = document.createElement("tr");

            const difficultyBadge = `<span class="difficulty-badge difficulty-${text.difficulty}">${text.difficulty}</span>`;

            const truncatedContent = text.content.length > 100
                ? text.content.substring(0, 100) + '...'
                : text.content;

            row.innerHTML = `
                <td><strong>#${text.id}</strong></td>
                <td class="text-content">${truncatedContent}</td>
                <td>${difficultyBadge}</td>
                <td class="actions">
                    <button onclick="editText(${text.id}, \`${text.content.replace(/`/g, '\\`')}\`, '${text.difficulty}')" class="btn btn-secondary btn-small">
                        Edit
                    </button>
                    <button onclick="deleteText(${text.id})" class="btn btn-danger btn-small">
                        Delete
                    </button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading texts:', error);
        document.getElementById("textsTableBody").innerHTML =
            '<tr><td colspan="4" class="alert alert-error">Error loading texts. Please refresh the page.</td></tr>';
    }
}

document.getElementById("addTextForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const content = document.getElementById("textContent").value;
    const difficulty = document.getElementById("textDifficulty").value;
    const submitBtn = event.target.querySelector('button[type="submit"]');

    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = 'Adding...';
    submitBtn.disabled = true;

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: getAuthHeaders(),
            body: JSON.stringify({ content, difficulty })
        });

        if (!response.ok) {
            throw new Error(`Failed to add text: ${response.status}`);
        }

        document.getElementById("textContent").value = "";
        loadTexts();
        showNotification('Text added successfully!', 'success');
    } catch (error) {
        console.error('Error adding text:', error);
        showNotification('Failed to add text. Please try again.', 'error');
    } finally {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
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

function editText(id, content, difficulty) {
    document.getElementById("editTextId").value = id;
    document.getElementById("editTextContent").value = content;
    document.getElementById("editTextDifficulty").value = difficulty;
    document.getElementById("editTextForm").classList.remove("hidden");

    document.getElementById("editTextForm").scrollIntoView({
        behavior: 'smooth',
        block: 'center'
    });
}

function cancelEdit() {
    document.getElementById("editTextForm").classList.add("hidden");
}

document.getElementById("editTextForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const id = document.getElementById("editTextId").value;
    const content = document.getElementById("editTextContent").value;
    const difficulty = document.getElementById("editTextDifficulty").value;
    const submitBtn = event.target.querySelector('button[type="submit"]');

    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = 'Saving...';
    submitBtn.disabled = true;

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "PUT",
            headers: getAuthHeaders(),
            body: JSON.stringify({ content, difficulty })
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
