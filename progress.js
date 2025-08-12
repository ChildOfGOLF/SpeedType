const API_BASE_URL = "http://localhost:8080";
let currentUserId = null;
let progressChart = null;
let allProgressData = [];

document.addEventListener('DOMContentLoaded', function() {
    checkAuth();
    initializeCharts();
});

function checkAuth() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (!token || !username) {
        alert('Пожалуйста, войдите в систему');
        window.location.href = 'login.html';
        return;
    }

    document.getElementById('currentUser').textContent = username;
    getCurrentUserId(username);
}

async function getCurrentUserId(username) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/auth/profile`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const user = await response.json();
            currentUserId = user.id;
            loadUserProgress();
        } else {
            throw new Error('Failed to get user data');
        }
    } catch (error) {
        console.error('Error getting user ID:', error);
        showError('Ошибка загрузки данных пользователя');
    }
}

async function loadUserProgress() {
    if (!currentUserId) return;

    try {
        showLoading();

        const summaryResponse = await fetch(`${API_BASE_URL}/auth/user/${currentUserId}/summary`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });

        if (summaryResponse.ok) {
            const summary = await summaryResponse.json();
            updateSummaryCards(summary);
        }

        const progressResponse = await fetch(`${API_BASE_URL}/auth/user/${currentUserId}`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });

        if (progressResponse.ok) {
            allProgressData = await progressResponse.json();
            updateCharts(allProgressData);
            updateActivityStats(allProgressData);
        }

        hideLoading();
    } catch (error) {
        console.error('Error loading progress:', error);
        showError('Ошибка загрузки данных прогресса');
        hideLoading();
    }
}

function updateSummaryCards(summary) {
    document.getElementById('avgSpeed').textContent = Math.round(summary.averageSpeed);
    document.getElementById('maxSpeed').textContent = summary.maxSpeed;
    document.getElementById('improvement').textContent = `${summary.improvementPercent > 0 ? '+' : ''}${summary.improvementPercent}%`;
    document.getElementById('totalTests').textContent = summary.totalTests;

    const improvementElement = document.getElementById('improvement');
    const improvementCard = improvementElement.closest('.summary-card');
    if (summary.improvementPercent > 0) {
        improvementCard.style.background = 'linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%)';
    } else if (summary.improvementPercent < 0) {
        improvementCard.style.background = 'linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%)';
    }
}

function initializeCharts() {
    const progressCtx = document.getElementById('progressChart').getContext('2d');
    progressChart = new Chart(progressCtx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Typing Speed (WPM)',
                data: [],
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                borderWidth: 3,
                tension: 0.4,
                fill: true,
                pointBackgroundColor: '#667eea',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: 'Typing Speed Over Time'
                },
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Words Per Minute'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Date'
                    }
                }
            }
        }
    });
}

function updateCharts(data) {
    if (data.length === 0) {
        showNoDataMessage();
        return;
    }

    data.sort((a, b) => new Date(a.date) - new Date(b.date));

    const labels = data.map(item => formatDate(item.date));
    const speeds = data.map(item => item.speed);

    progressChart.data.labels = labels;
    progressChart.data.datasets[0].data = speeds;
    progressChart.update();
}

function updateActivityStats(data) {
    if (data.length === 0) return;

    const dayCount = {};
    data.forEach(item => {
        const day = formatDate(item.date);
        dayCount[day] = (dayCount[day] || 0) + 1;
    });

    const mostActiveDay = Object.keys(dayCount).reduce((a, b) => dayCount[a] > dayCount[b] ? a : b);
    document.getElementById('mostActiveDay').textContent = mostActiveDay;

    const totalWeeks = Math.max(1, Math.ceil(data.length / 7));
    const avgTestsPerWeek = Math.round(data.length / totalWeeks);
    document.getElementById('avgTestsPerWeek').textContent = avgTestsPerWeek;

    const streaks = calculateStreaks(data);
    document.getElementById('currentStreak').textContent = `${streaks.current} дней`;
    document.getElementById('bestStreak').textContent = `${streaks.best} дней`;
}

function calculateStreaks(data) {
    if (data.length === 0) return { current: 0, best: 0 };

    const dates = [...new Set(data.map(item => formatDate(item.date)))].sort();
    let currentStreak = 0;
    let bestStreak = 0;
    let tempStreak = 1;

    for (let i = 1; i < dates.length; i++) {
        const prevDate = new Date(dates[i - 1]);
        const currDate = new Date(dates[i]);
        const diffDays = (currDate - prevDate) / (1000 * 60 * 60 * 24);

        if (diffDays === 1) {
            tempStreak++;
        } else {
            bestStreak = Math.max(bestStreak, tempStreak);
            tempStreak = 1;
        }
    }

    bestStreak = Math.max(bestStreak, tempStreak);

    const today = new Date();
    const lastTestDate = new Date(dates[dates.length - 1]);
    const daysSinceLastTest = (today - lastTestDate) / (1000 * 60 * 60 * 24);

    if (daysSinceLastTest <= 1) {
        currentStreak = tempStreak;
    }

    return { current: currentStreak, best: bestStreak };
}

async function applyFilters() {
    if (!currentUserId) return;

    const difficulty = document.getElementById('difficultyFilter').value;
    const language = document.getElementById('languageFilter').value;
    const timeRange = document.getElementById('timeRange').value;

    let url = `${API_BASE_URL}/auth/user/${currentUserId}?`;
    const params = new URLSearchParams();

    if (difficulty) params.append('difficulty', difficulty);
    if (language) params.append('language', language);

    if (timeRange) {
        const endDate = new Date();
        const startDate = new Date();
        startDate.setDate(startDate.getDate() - parseInt(timeRange));
        params.append('startDate', startDate.toISOString());
        params.append('endDate', endDate.toISOString());
    }

    try {
        showLoading();
        const response = await fetch(url + params.toString(), {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });

        if (response.ok) {
            const filteredData = await response.json();
            updateCharts(filteredData);
        }
        hideLoading();
    } catch (error) {
        console.error('Error applying filters:', error);
        showError('Ошибка применения фильтров');
        hideLoading();
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function showLoading() {
    document.querySelector('.chart-container').innerHTML = '<div class="loading">Загрузка данных...</div>';
}

function hideLoading() {
    document.querySelector('.chart-container').innerHTML = '<canvas id="progressChart"></canvas>';

    initializeCharts();
    if (allProgressData.length > 0) {
        updateCharts(allProgressData);
    }
}

function showError(message) {
    document.querySelector('.chart-container').innerHTML = `<div class="error">${message}</div>`;
}

function showNoDataMessage() {
    document.querySelector('.chart-container').innerHTML = '<div class="error">Нет данных для отображения. Пройдите несколько тестов, чтобы увидеть статистику.</div>';
}

function goBack() {
    window.location.href = 'index.html';
}
