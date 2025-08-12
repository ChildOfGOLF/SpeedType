let timer;
let startTime;
let testRunning = false;
let testText = "";
let currentUser = null;
let currentTextLanguage = localStorage.getItem('textLanguage') || 'en';
let isCustomTextMode = false;
let isTestCompleted = false;

document.addEventListener('DOMContentLoaded', function() {
    initLocalization();
    initializeApp();
    setupEventListeners();
});

function initializeApp() {
    checkUserAuth();
    setDifficulty();
    document.getElementById('textLanguageSelect').value = currentTextLanguage;
}

function setupEventListeners() {
    document.getElementById('startBtn').addEventListener('click', startTest);
    document.getElementById('resetBtn').addEventListener('click', resetTest);
    document.getElementById('inputText').addEventListener('input', checkTyping);
    document.getElementById('retryBtn').addEventListener('click', resetTest);
    document.getElementById('difficultySelect').addEventListener('change', getNewText);
    document.getElementById('textLanguageSelect').addEventListener('change', setTextLanguage);
    document.getElementById('interfaceLanguageSelect').addEventListener('change', setInterfaceLanguage);

    document.getElementById('getNewTextBtn').addEventListener('click', getNewText);
    document.getElementById('customTextBtn').addEventListener('click', openCustomTextModal);
    document.getElementById('useCustomTextBtn').addEventListener('click', useCustomText);
    document.getElementById('cancelCustomTextBtn').addEventListener('click', closeCustomTextModal);
    document.getElementById('closeCustomTextModal').addEventListener('click', closeCustomTextModal);

    document.getElementById('logoutBtn').addEventListener('click', logout);
    document.getElementById('loginLinkBtn').addEventListener('click', goToLogin);
    document.getElementById('myResultsBtn').addEventListener('click', showMyResults);
    document.getElementById('closeModal').addEventListener('click', closeModal);

    document.getElementById('leaderboardBtn').addEventListener('click', showLeaderboard);
    document.getElementById('leaderboardBtn2').addEventListener('click', showLeaderboard);
    document.getElementById('closeLeaderboardModal').addEventListener('click', closeLeaderboardModal);
    document.getElementById('leaderboardDifficulty').addEventListener('change', updateLeaderboard);
    document.getElementById('leaderboardLanguage').addEventListener('change', updateLeaderboard);

    document.getElementById('realTimeGameBtn').addEventListener('click', goToRealTimeGame);
    document.getElementById('progressBtn').addEventListener('click', goToProgressAnalytics);

    document.getElementById('myResultsModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal();
        }
    });

    document.getElementById('leaderboardModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeLeaderboardModal();
        }
    });

    document.getElementById('customTextModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeCustomTextModal();
        }
    });
}

function checkUserAuth() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (token && username) {
        verifyToken(token, username);
    } else {
        showGuestPanel();
    }
}

async function verifyToken(token, username) {
    try {
        const response = await fetch('http://localhost:8080/texts?difficulty=easy', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.status !== 401 && response.status !== 403) {
            currentUser = { username, token };
            showUserPanel(username);
        } else {
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            showGuestPanel();
        }
    } catch (error) {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        showGuestPanel();
    }
}

function showUserPanel(username) {
    document.getElementById('usernameDisplay').textContent = username;
    document.getElementById('userPanel').classList.remove('hidden');
    document.getElementById('guestPanel').classList.add('hidden');

    const inputText = document.getElementById('inputText');
    inputText.disabled = false;
    inputText.placeholder = t('typeHere');

    document.getElementById('startBtn').disabled = false;
}

function showGuestPanel() {
    document.getElementById('userPanel').classList.add('hidden');
    document.getElementById('guestPanel').classList.remove('hidden');
    currentUser = null;

    const inputText = document.getElementById('inputText');
    inputText.disabled = true;
    inputText.placeholder = t('pleaseLogin');
    inputText.value = '';

    document.getElementById('startBtn').disabled = true;

    document.getElementById('timeDisplay').textContent = '0';
    document.getElementById('speedDisplay').textContent = '0';
    document.getElementById('errorDisplay').textContent = '0';
    document.getElementById('finalResults').classList.add('hidden');
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
    if (!testRunning) return;

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

    if (inputText.length > 0 && startTime) {
        const currentTime = new Date().getTime();
        const elapsedTimeMinutes = (currentTime - startTime) / 60000; // в минутах

        if (elapsedTimeMinutes > 0) {
            const typedWords = inputText.trim().split(/\s+/).length;
            const currentWPM = Math.round(typedWords / elapsedTimeMinutes);
            document.getElementById('speedDisplay').textContent = isNaN(currentWPM) ? 0 : currentWPM;
        }
    }

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

        saveResult(finalWpm, errors);
    }
}

async function saveResult(wpm, errors) {
    const saveStatusEl = document.getElementById('saveStatus');

    console.log('saveResult called: isCustomTextMode =', isCustomTextMode);

    if (isCustomTextMode) {
        showSaveStatus('customTextNotSaved', 'info');
        return;
    }

    if (!currentUser) {
        showSaveStatus('Result not saved - Login to save your progress!', 'info');
        return;
    }

    const difficulty = document.getElementById('difficultySelect').value;
    const language = currentTextLanguage;

    try {
        showSaveStatus('Saving result...', 'info');

        const response = await fetch('http://localhost:8080/results', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentUser.token}`
            },
            body: JSON.stringify({
                typingSpeed: wpm,
                errors: errors,
                difficulty: difficulty,
                language: language
            })
        });

        if (response.ok) {
            const result = await response.json();
            showSaveStatus('Result saved successfully!', 'success', true);
        } else {
            const errorText = await response.text();
            showSaveStatus(`Failed to save result. Status: ${response.status}`, 'error');
        }
    } catch (error) {
        showSaveStatus(`Error saving result: ${error.message}`, 'error');
    }
}

function showSaveStatus(messageKey, type, isTranslated = false) {
    const saveStatusEl = document.getElementById('saveStatus');
    const message = isTranslated ? messageKey : (t(messageKey) || messageKey);
    saveStatusEl.textContent = message;
    saveStatusEl.className = `save-status ${type}`;
    saveStatusEl.classList.remove('hidden');

    setTimeout(() => {
        saveStatusEl.classList.add('hidden');
    }, 5000);
}

function showCustomTextStatus(messageKey, type, isTranslated = false) {
    const statusEl = document.getElementById('customTextStatus');
    const message = isTranslated ? messageKey : (t(messageKey) || messageKey);
    statusEl.textContent = message;
    statusEl.className = `save-status ${type}`;
    statusEl.classList.remove('hidden');

    setTimeout(() => {
        statusEl.classList.add('hidden');
    }, 5000);
}

async function showMyResults() {
    if (!currentUser) {
        showSaveStatus('Please login to view your results', 'info');
        return;
    }

    document.getElementById('myResultsModal').classList.remove('hidden');
    document.getElementById('resultsContainer').innerHTML = '<p>Loading your results...</p>';

    try {
        const response = await fetch('http://localhost:8080/results/my', {
            headers: {
                'Authorization': `Bearer ${currentUser.token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const results = await response.json();
            displayResults(results);
        } else {
            const errorText = await response.text();
            document.getElementById('resultsContainer').innerHTML =
                `<p>Failed to load results. Status: ${response.status}</p>`;
        }
    } catch (error) {
        document.getElementById('resultsContainer').innerHTML =
            `<p>Error loading results: ${error.message}</p>`;
    }
}

function displayResults(results) {
    const container = document.getElementById('resultsContainer');

    if (results.length === 0) {
        container.innerHTML = `<p>${t('noResults')}</p>`;
        return;
    }

    let html = `
        <table class="results-table">
            <thead>
                <tr>
                    <th>${t('date')}</th>
                    <th>${t('speed')} (${t('wpm')})</th>
                    <th>${t('errors')}</th>
                    <th>${t('difficulty')}</th>
                    <th>${t('language')}</th>
                </tr>
            </thead>
            <tbody>
    `;

    results.forEach(result => {
        const date = new Date(result.date).toLocaleDateString();
        const difficultyTranslated = t(result.difficulty);
        const languageDisplay = result.language === 'en' ? t('english') : t('russian');
        html += `
            <tr>
                <td>${date}</td>
                <td>${result.typingSpeed}</td>
                <td>${result.errors}</td>
                <td>${difficultyTranslated}</td>
                <td>${languageDisplay}</td>
            </tr>
        `;
    });

    html += '</tbody></table>';

    const avgSpeed = Math.round(results.reduce((sum, r) => sum + r.typingSpeed, 0) / results.length);
    const bestSpeed = Math.max(...results.map(r => r.typingSpeed));
    const totalTests = results.length;

    html += `
        <div style="margin-top: 20px; text-align: center;">
            <h3>Your Statistics</h3>
            <p><strong>Total Tests:</strong> ${totalTests}</p>
            <p><strong>Average Speed:</strong> ${avgSpeed} ${t('wpm')}</p>
            <p><strong>Best Speed:</strong> ${bestSpeed} ${t('wpm')}</p>
        </div>
    `;

    container.innerHTML = html;
}

function closeModal() {
    document.getElementById('myResultsModal').classList.add('hidden');
}

function setTextLanguage() {
    currentTextLanguage = document.getElementById('textLanguageSelect').value;
    localStorage.setItem('textLanguage', currentTextLanguage);
    isCustomTextMode = false;
    setDifficulty();
}

function setInterfaceLanguage() {
    const selectedLanguage = document.getElementById('interfaceLanguageSelect').value;
    changeInterfaceLanguage(selectedLanguage);
}

async function setDifficulty() {
    const difficulty = document.getElementById('difficultySelect').value;
    const language = currentTextLanguage;
    const url = `http://localhost:8080/texts?difficulty=${difficulty}&language=${language}`;

    try {
        document.getElementById('displayText').textContent = t('loadingText');

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
            document.getElementById('displayText').textContent = t('errorLoadingText');
        }
    } catch (error) {
        document.getElementById('displayText').textContent = t('errorLoadingText');
    }
}

async function showLeaderboard() {
    document.getElementById('leaderboardModal').classList.remove('hidden');
    await loadLeaderboard();
}

function closeLeaderboardModal() {
    document.getElementById('leaderboardModal').classList.add('hidden');
}

async function updateLeaderboard() {
    await loadLeaderboard();
}

async function loadLeaderboard() {
    const container = document.getElementById('leaderboardContainer');
    const difficulty = document.getElementById('leaderboardDifficulty').value;
    const language = document.getElementById('leaderboardLanguage').value;

    container.innerHTML = `<p>${t('loadingLeaderboard')}</p>`;

    try {
        let url = 'http://localhost:8080/leaderboard/top?limit=20';

        if (difficulty !== 'all' && language !== 'all') {
            url = 'http://localhost:8080/leaderboard/top?limit=20';
        } else if (difficulty !== 'all') {
            url = `http://localhost:8080/leaderboard/top/difficulty/${difficulty}?limit=20`;
        } else if (language !== 'all') {
            url = `http://localhost:8080/leaderboard/top/language/${language}?limit=20`;
        }

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error('Failed to fetch leaderboard');
        }

        const leaderboard = await response.json();
        displayLeaderboard(leaderboard, difficulty, language);

    } catch (error) {
        console.error('Error loading leaderboard:', error);
        container.innerHTML = `<p>${t('noLeaderboardData')}</p>`;
    }
}

function displayLeaderboard(leaderboard, filterDifficulty, filterLanguage) {
    const container = document.getElementById('leaderboardContainer');

    if (leaderboard.length === 0) {
        container.innerHTML = `<p>${t('noLeaderboardData')}</p>`;
        return;
    }

    let filteredLeaderboard = leaderboard;
    if (filterDifficulty !== 'all' && filterLanguage !== 'all') {
        // пока показываем общий рейтинг
    }

    let html = `
        <table class="leaderboard-table">
            <thead>
                <tr>
                    <th>${t('rank')}</th>
                    <th>${t('username')}</th>
                    <th>${t('bestWPM')}</th>
                    <th>${t('averageWPM')}</th>
                    <th>${t('totalTests')}</th>
                    <th>${t('lastTest')}</th>
                </tr>
            </thead>
            <tbody>
    `;

    filteredLeaderboard.forEach((entry, index) => {
        const rank = index + 1;
        const medalHtml = getRankMedal(rank);
        const date = new Date(entry.lastTestDate).toLocaleDateString();

        html += `
            <tr>
                <td class="rank-cell">${medalHtml}${rank}</td>
                <td class="username-cell">${entry.username}</td>
                <td class="wpm-cell">${entry.bestWPM}</td>
                <td>${entry.averageWPM}</td>
                <td>${entry.totalTests}</td>
                <td>${date}</td>
            </tr>
        `;
    });

    html += '</tbody></table>';

    if (filterDifficulty !== 'all' || filterLanguage !== 'all') {
        html += '<div style="margin-top: 15px; text-align: center; color: #6c757d;">';
        html += '<small>';
        if (filterDifficulty !== 'all') {
            html += `${t('difficulty')}: ${t(filterDifficulty)}`;
        }
        if (filterDifficulty !== 'all' && filterLanguage !== 'all') {
            html += ' | ';
        }
        if (filterLanguage !== 'all') {
            html += `${t('textLanguage')}: ${filterLanguage === 'en' ? t('english') : t('russian')}`;
        }
        html += '</small>';
        html += '</div>';
    }

    container.innerHTML = html;
}

function getRankMedal(rank) {
    if (rank === 1) {
        return '<span class="rank-medal rank-1">1</span>';
    } else if (rank === 2) {
        return '<span class="rank-medal rank-2">2</span>';
    } else if (rank === 3) {
        return '<span class="rank-medal rank-3">3</span>';
    }
    return '';
}

function switchTextMode() {
    const textMode = document.getElementById('textModeSelect').value;
    isCustomTextMode = textMode === 'custom';

    const presetControls = document.getElementById('presetControls');
    const customControls = document.getElementById('customTextControls');

    if (isCustomTextMode) {
        presetControls.classList.add('hidden');
        customControls.classList.remove('hidden');

        document.getElementById('displayText').textContent = t('pasteTextHere');
        testText = "";
    } else {
        presetControls.classList.remove('hidden');
        customControls.classList.add('hidden');

        getNewText();
    }

    resetTest();
}

async function getNewText() {
    isCustomTextMode = false;
    await setDifficulty();
    resetTest();
}

function openCustomTextModal() {
    document.getElementById('customTextModal').classList.remove('hidden');
    document.getElementById('customTextArea').focus();
}

function closeCustomTextModal() {
    document.getElementById('customTextModal').classList.add('hidden');
    document.getElementById('customTextArea').value = '';
    document.getElementById('customTextStatus').classList.add('hidden');
}

function useCustomText() {
    const customTextArea = document.getElementById('customTextArea');
    const customText = customTextArea.value.trim();

    if (!customText) {
        showCustomTextStatus('customTextEmpty', 'error');
        return;
    }

    if (customText.length < 10) {
        showCustomTextStatus('customTextTooShort', 'error');
        return;
    }

    isCustomTextMode = true;

    testText = customText;
    document.getElementById('displayText').textContent = customText;

    customTextArea.value = '';
    closeCustomTextModal();

    showSaveStatus('customTextLoaded', 'success');
    resetTest();
}

function goToRealTimeGame() {
    window.location.href = 'game.html';
}

function goToProgressAnalytics() {
    window.location.href = 'progress.html';
}
