let timer;
let startTime;
let testRunning = false;
let testText = "";
let currentUser = null;

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
});

function initializeApp() {
    console.log('Initializing app...');
    // Check if user is logged in
    checkUserAuth();
    // Load initial text
    setDifficulty();
    console.log('App initialization complete');
}

function setupEventListeners() {
    document.getElementById('startBtn').addEventListener('click', startTest);
    document.getElementById('resetBtn').addEventListener('click', resetTest);
    document.getElementById('inputText').addEventListener('input', checkTyping);
    document.getElementById('retryBtn').addEventListener('click', resetTest);
    document.getElementById('difficultySelect').addEventListener('change', setDifficulty);

    // Auth-related event listeners
    document.getElementById('logoutBtn').addEventListener('click', logout);
    document.getElementById('loginLinkBtn').addEventListener('click', goToLogin);
    document.getElementById('myResultsBtn').addEventListener('click', showMyResults);
    document.getElementById('closeModal').addEventListener('click', closeModal);

    // Close modal when clicking outside
    document.getElementById('myResultsModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal();
        }
    });
}

function checkUserAuth() {
    console.log('Checking user auth...');
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    console.log('Token:', token ? 'exists' : 'not found');
    console.log('Username:', username);

    if (token && username) {
        // Verify token validity
        verifyToken(token, username);
    } else {
        showGuestPanel();
    }
}

async function verifyToken(token, username) {
    console.log('Verifying token for user:', username);
    try {
        // Используем простой эндпоинт для проверки токена вместо /results/my
        const response = await fetch('http://localhost:8080/texts?difficulty=easy', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        console.log('Token verification response status:', response.status);

        if (response.status !== 401 && response.status !== 403) {
            // Token is valid
            console.log('Token is valid, showing user panel');
            currentUser = { username, token };
            showUserPanel(username);
        } else {
            // Token is invalid
            console.log('Token is invalid, clearing storage');
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            showGuestPanel();
        }
    } catch (error) {
        console.error('Error verifying token:', error);
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        showGuestPanel();
    }
}

function showUserPanel(username) {
    console.log('Showing user panel for:', username);
    document.getElementById('usernameDisplay').textContent = username;
    document.getElementById('userPanel').classList.remove('hidden');
    document.getElementById('guestPanel').classList.add('hidden');

    // Разблокируем поле ввода для авторизованных пользователей
    const inputText = document.getElementById('inputText');
    inputText.disabled = false;
    inputText.placeholder = "Click 'Start Test' and then type the text above here...";

    // Разблокируем кнопку Start Test
    document.getElementById('startBtn').disabled = false;

    console.log('Input field enabled for authenticated user');
}

function showGuestPanel() {
    console.log('Showing guest panel');
    document.getElementById('userPanel').classList.add('hidden');
    document.getElementById('guestPanel').classList.remove('hidden');
    currentUser = null;

    // Блокируем поле ввода для гостей
    const inputText = document.getElementById('inputText');
    inputText.disabled = true;
    inputText.placeholder = "Please login to take typing tests...";
    inputText.value = '';

    // Блокируем кнопку Start Test
    document.getElementById('startBtn').disabled = true;

    // Сбрасываем статистику
    document.getElementById('timeDisplay').textContent = '0';
    document.getElementById('speedDisplay').textContent = '0';
    document.getElementById('errorDisplay').textContent = '0';
    document.getElementById('finalResults').classList.add('hidden');

    console.log('Input field disabled for guest user');
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    currentUser = null;
    showGuestPanel();
    showSaveStatus('Logged out successfully', 'info');
}

function goToLogin() {
    window.location.href = 'login.html';
}

function startTest() {
    if (testRunning || !testText) return;

    testRunning = true;
    startTime = new Date().getTime();

    document.getElementById('inputText').value = '';
    document.getElementById('timeDisplay').textContent = '0';
    document.getElementById('speedDisplay').textContent = '0';
    document.getElementById('errorDisplay').textContent = '0';
    document.getElementById('finalResults').classList.add('hidden');
    document.getElementById('saveStatus').classList.add('hidden');

    timer = setInterval(updateTime, 1000);
}

function updateTime() {
    const currentTime = new Date().getTime();
    const elapsedTime = Math.floor((currentTime - startTime) / 1000);
    document.getElementById('timeDisplay').textContent = elapsedTime;
}

function resetTest() {
    clearInterval(timer);
    testRunning = false;

    document.getElementById('inputText').value = '';
    document.getElementById('timeDisplay').textContent = '0';
    document.getElementById('speedDisplay').textContent = '0';
    document.getElementById('errorDisplay').textContent = '0';
    document.getElementById('finalResults').classList.add('hidden');
    document.getElementById('saveStatus').classList.add('hidden');
}

function checkTyping() {
    const inputText = document.getElementById('inputText').value;
    const displayTextElement = document.getElementById('displayText');

    let highlightedText = "";
    let errors = 0;
    let isCompleted = inputText.length === testText.length;

    for (let i = 0; i < testText.length; i++) {
        let char = testText[i];
        let inputChar = inputText[i] || '';

        if (inputChar === char) {
            highlightedText += `<span class="correct">${char}</span>`;
        } else if (inputChar) {
            highlightedText += `<span class="incorrect">${char}</span>`;
            errors++;
        } else {
            highlightedText += `<span class="remaining">${char}</span>`;
            isCompleted = false;
        }
    }

    displayTextElement.innerHTML = highlightedText;
    document.getElementById('errorDisplay').textContent = errors;

    if (isCompleted) {
        clearInterval(timer);
        testRunning = false;
        const elapsedTime = (new Date().getTime() - startTime) / 60000;
        const wordCount = inputText.trim().split(/\s+/).length;
        const wpm = Math.round(wordCount / elapsedTime);
        const finalWpm = isNaN(wpm) ? 0 : wpm;

        document.getElementById('finalSpeed').textContent = finalWpm;
        document.getElementById('finalErrors').textContent = errors;
        document.getElementById('finalResults').classList.remove('hidden');

        // Save result if user is authenticated
        saveResult(finalWpm, errors);
    }
}

// Enhanced save result function
async function saveResult(wpm, errors) {
    const saveStatusEl = document.getElementById('saveStatus');

    if (!currentUser) {
        showSaveStatus('Result not saved - Login to save your progress!', 'info');
        return;
    }

    const difficulty = document.getElementById('difficultySelect').value;

    try {
        showSaveStatus('Saving result...', 'info');

        console.log('Saving result:', {
            typingSpeed: wpm,
            errors: errors,
            difficulty: difficulty,
            user: currentUser.username
        });

        const response = await fetch('http://localhost:8080/results', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentUser.token}`
            },
            body: JSON.stringify({
                typingSpeed: wpm,
                errors: errors,
                difficulty: difficulty
            })
        });

        console.log('Save result response status:', response.status);
        console.log('Save result response headers:', Object.fromEntries(response.headers.entries()));

        if (response.ok) {
            const result = await response.json();
            console.log('Result saved successfully:', result);
            showSaveStatus('Result saved successfully! 🎉', 'success');
        } else {
            const errorText = await response.text();
            console.error('Save result error response:', errorText);
            showSaveStatus(`Failed to save result. Status: ${response.status}`, 'error');
        }
    } catch (error) {
        console.error('Error saving result:', error);
        showSaveStatus(`Error saving result: ${error.message}`, 'error');
    }
}

function showSaveStatus(message, type) {
    const saveStatusEl = document.getElementById('saveStatus');
    saveStatusEl.textContent = message;
    saveStatusEl.className = `save-status ${type}`;
    saveStatusEl.classList.remove('hidden');

    // Auto-hide after 5 seconds
    setTimeout(() => {
        saveStatusEl.classList.add('hidden');
    }, 5000);
}

// Results modal functions
async function showMyResults() {
    console.log('showMyResults called');

    if (!currentUser) {
        console.log('No current user');
        showSaveStatus('Please login to view your results', 'info');
        return;
    }

    console.log('Current user:', currentUser.username);
    document.getElementById('myResultsModal').classList.remove('hidden');
    document.getElementById('resultsContainer').innerHTML = '<p>Loading your results...</p>';

    try {
        console.log('Making request to /results/my with token:', currentUser.token.substring(0, 20) + '...');

        const response = await fetch('http://localhost:8080/results/my', {
            headers: {
                'Authorization': `Bearer ${currentUser.token}`,
                'Content-Type': 'application/json'
            }
        });

        console.log('Response status:', response.status);
        console.log('Response headers:', Object.fromEntries(response.headers.entries()));

        if (response.ok) {
            const results = await response.json();
            console.log('Received results:', results);
            displayResults(results);
        } else {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            document.getElementById('resultsContainer').innerHTML =
                `<p>Failed to load results. Status: ${response.status}</p>`;
        }
    } catch (error) {
        console.error('Error loading results:', error);
        document.getElementById('resultsContainer').innerHTML =
            `<p>Error loading results: ${error.message}</p>`;
    }
}

function displayResults(results) {
    const container = document.getElementById('resultsContainer');

    if (results.length === 0) {
        container.innerHTML = '<p>No results found. Complete some tests to see your progress!</p>';
        return;
    }

    let html = `
        <table class="results-table">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Speed (WPM)</th>
                    <th>Errors</th>
                    <th>Difficulty</th>
                </tr>
            </thead>
            <tbody>
    `;

    results.forEach(result => {
        const date = new Date(result.date).toLocaleDateString();
        html += `
            <tr>
                <td>${date}</td>
                <td>${result.typingSpeed}</td>
                <td>${result.errors}</td>
                <td>${result.difficulty}</td>
            </tr>
        `;
    });

    html += '</tbody></table>';

    // Add statistics
    const avgSpeed = Math.round(results.reduce((sum, r) => sum + r.typingSpeed, 0) / results.length);
    const bestSpeed = Math.max(...results.map(r => r.typingSpeed));
    const totalTests = results.length;

    html += `
        <div style="margin-top: 20px; text-align: center;">
            <h3>Your Statistics</h3>
            <p><strong>Total Tests:</strong> ${totalTests}</p>
            <p><strong>Average Speed:</strong> ${avgSpeed} WPM</p>
            <p><strong>Best Speed:</strong> ${bestSpeed} WPM</p>
        </div>
    `;

    container.innerHTML = html;
}

function closeModal() {
    document.getElementById('myResultsModal').classList.add('hidden');
}

async function setDifficulty() {
    const difficulty = document.getElementById('difficultySelect').value;
    const url = `http://localhost:8080/texts?difficulty=${difficulty}`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Failed to fetch text');
        }

        const data = await response.json();

        if (Array.isArray(data) && data.length > 0) {
            const randomIndex = Math.floor(Math.random() * data.length);
            const textContent = data[randomIndex].content;
            document.getElementById('displayText').textContent = textContent;
            testText = textContent;
        } else if (data && typeof data === "object" && "content" in data) {
            document.getElementById('displayText').textContent = data.content;
            testText = data.content;
        } else {
            document.getElementById('displayText').textContent = 'No text available for this difficulty.';
        }
    } catch (error) {
        console.error('Error fetching text:', error);
        document.getElementById('displayText').textContent = 'Error loading text. Please try again.';
    }
}
