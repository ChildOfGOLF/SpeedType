let stompClient = null;
let currentGame = null;
let gameTimer = null;
let startTime = null;
let currentUser = null;
let isTyping = false;

document.addEventListener('DOMContentLoaded', function() {
    checkAuth();
    initializeWebSocket();
    checkForActiveGame();
});

function checkAuth() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (!token || !username) {
        window.location.href = 'login.html';
        return;
    }

    currentUser = username;
    document.getElementById('username').textContent = username;
}

async function checkForActiveGame() {
    try {
        const response = await fetch('http://localhost:8080/api/games/my-active', {
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });

        if (response.ok) {
            const gameState = await response.json();
            if (gameState) {
                showActiveGameInfo(gameState);
            }
        }
    } catch (error) {
        console.log('Нет активных игр');
    }
}

function showActiveGameInfo(gameState) {
    currentGame = gameState;

    document.getElementById('stopCurrentGameBtn').style.display = 'block';
    document.getElementById('currentGameInfo').style.display = 'block';
    document.getElementById('currentGameCode').textContent = gameState.gameCode;
    document.getElementById('currentGameStatus').textContent = getStatusText(gameState.status);

    subscribeToGame(gameState.gameCode);
}

function hideActiveGameInfo() {
    document.getElementById('stopCurrentGameBtn').style.display = 'none';
    document.getElementById('currentGameInfo').style.display = 'none';
    currentGame = null;
}

function getStatusText(status) {
    const statusTexts = {
        'WAITING_FOR_PLAYER': 'Ожидание игрока',
        'READY_TO_START': 'Готова к старту',
        'IN_PROGRESS': 'Идет игра',
        'FINISHED': 'Завершена',
        'CANCELLED': 'Отменена'
    };
    return statusTexts[status] || status;
}

async function stopCurrentGame() {
    if (!currentGame) {
        showError('Нет активной игры для остановки');
        return;
    }

    if (confirm(`Вы уверены, что хотите остановить игру ${currentGame.gameCode}? Результат не будет засчитан.`)) {
        try {
            const response = await fetch(`http://localhost:8080/api/games/${currentGame.gameCode}/cancel`, {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                }
            });

            if (response.ok) {
                showSuccess('Игра остановлена');
                hideActiveGameInfo();

                if (stompClient && stompClient.connected) {
                    stompClient.send(`/app/game/${currentGame.gameCode}/cancel`, {}, JSON.stringify({
                        username: currentUser,
                        reason: 'cancelled_from_menu'
                    }));
                }
            } else {
                const error = await response.text();
                showError('Ошибка остановки игры: ' + error);
            }
        } catch (error) {
            showError('Ошибка остановки игры: ' + error.message);
        }
    }
}

function initializeWebSocket() {
    const socket = new SockJS('http://localhost:8080/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('WebSocket Connected: ' + frame);

        if (currentGame && currentGame.gameCode) {
            console.log('Подписываемся на игру:', currentGame.gameCode);
            subscribeToGame(currentGame.gameCode);
        }
    }, function (error) {
        console.error('WebSocket connection error:', error);
        showError('Ошибка подключения к серверу');
    });
}

async function createGame() {
    try {
        const response = await fetch('http://localhost:8080/api/games/create', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token'),
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const gameState = await response.json();
            currentGame = gameState;
            showGameLobby(gameState);
            subscribeToGame(gameState.gameCode);
        } else {
            const error = await response.text();
            showError(error);
        }
    } catch (error) {
        showError('Ошибка создания игры: ' + error.message);
    }
}

function showJoinGameModal() {
    document.getElementById('joinGameModal').style.display = 'block';
    document.getElementById('gameCodeInput').focus();
}

function closeJoinGameModal() {
    document.getElementById('joinGameModal').style.display = 'none';
    document.getElementById('gameCodeInput').value = '';
}

async function joinGameByCode() {
    const gameCode = document.getElementById('gameCodeInput').value.trim().toUpperCase();

    if (!gameCode || gameCode.length !== 6) {
        showError('Введите корректный код игры (6 символов)');
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/games/${gameCode}/join`, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token'),
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const gameState = await response.json();
            currentGame = gameState;
            closeJoinGameModal();
            showGameLobby(gameState);
            subscribeToGame(gameCode);
        } else {
            const error = await response.text();
            showError(error);
        }
    } catch (error) {
        showError('Ошибка присоединения к игре: ' + error.message);
    }
}

async function showWaitingGames() {
    try {
        const response = await fetch('http://localhost:8080/api/games/waiting', {
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });

        if (response.ok) {
            const games = await response.json();
            displayWaitingGames(games);
        } else {
            showError('Ошибка загрузки игр');
        }
    } catch (error) {
        showError('Ошибка: ' + error.message);
    }
}

function displayWaitingGames(games) {
    const gamesList = document.getElementById('gamesList');
    gamesList.innerHTML = '';

    if (games.length === 0) {
        gamesList.innerHTML = '<p>Нет доступных игр</p>';
    } else {
        games.forEach(game => {
            const gameItem = document.createElement('div');
            gameItem.className = 'game-item';
            gameItem.innerHTML = `
                <div class="game-item-info">
                    <div><strong>Код:</strong> ${game.gameCode}</div>
                    <div><strong>Создатель:</strong> ${game.player1.username}</div>
                    <div><strong>Текст:</strong> ${game.text.content.substring(0, 50)}...</div>
                </div>
                <button onclick="joinSpecificGame('${game.gameCode}')">Присоединиться</button>
            `;
            gamesList.appendChild(gameItem);
        });
    }

    document.getElementById('gameMenu').style.display = 'none';
    document.getElementById('waitingGames').style.display = 'block';
}

async function joinSpecificGame(gameCode) {
    document.getElementById('gameCodeInput').value = gameCode;
    await joinGameByCode();
    hideWaitingGames();
}

function hideWaitingGames() {
    document.getElementById('waitingGames').style.display = 'none';
    document.getElementById('gameMenu').style.display = 'block';
}

function showGameLobby(gameState) {
    document.getElementById('gameMenu').style.display = 'none';
    document.getElementById('waitingGames').style.display = 'none';
    document.getElementById('gameLobby').style.display = 'block';

    document.getElementById('lobbyGameCode').textContent = gameState.gameCode;
    document.getElementById('player1Name').textContent = gameState.player1.username;
    document.getElementById('player2Name').textContent = gameState.player2 ? gameState.player2.username : 'Ожидание...';
    document.getElementById('textPreview').textContent = gameState.textContent;

    const startBtn = document.getElementById('startGameBtn');
    startBtn.disabled = !gameState.player2;

    if (gameState.player2) {
        startBtn.textContent = 'Начать игру';
    }
}

function subscribeToGame(gameCode) {
    console.log('Попытка подписки на игру:', gameCode);

    if (stompClient && stompClient.connected) {
        console.log('WebSocket подключен, подписываемся на /topic/game/' + gameCode);
        stompClient.subscribe(`/topic/game/${gameCode}`, function (message) {
            console.log('Получено сообщение из WebSocket:', message.body);
            const update = JSON.parse(message.body);
            handleGameUpdate(update);
        });
    } else {
        console.log('WebSocket не подключен, попробуем позже');
        setTimeout(() => {
            if (stompClient && stompClient.connected) {
                subscribeToGame(gameCode);
            }
        }, 1000);
    }
}

function handleGameUpdate(update) {
    console.log('Получено WebSocket обновление:', update);
    
    if (update.type === 'GAME_UPDATE') {
        handleGameStateUpdate(update.payload);
    } else if (update.type === 'PROGRESS_UPDATE') {
        updateGameProgress(update.payload);
    } else if (update.type === 'PLAYER_JOIN') {
        if (currentGame) {
            checkForActiveGame();
        }
    }
}

function updateGameProgress(gameState) {
    console.log('Обновление прогресса игры:', gameState);

    if (!gameState || !currentGame) {
        return;
    }

    currentGame = gameState;

    if (document.getElementById('gameArea').style.display === 'block') {
        const player1Progress = document.getElementById('player1Progress');
        const player2Progress = document.getElementById('player2Progress');

        if (gameState.player1) {
            updatePlayerProgress(gameState.player1, player1Progress);
        }

        if (gameState.player2) {
            updatePlayerProgress(gameState.player2, player2Progress);
        }
    }

    if (gameState.status === 'FINISHED') {
        console.log('Игра завершена, показываем результаты');
        showGameResults(gameState);
    }
}

function handleGameStateUpdate(gameState) {
    console.log('Обновление состояния игры:', gameState);
    currentGame = gameState;
    
    if (gameState.status === 'READY_TO_START') {
        showGameLobby(gameState);
        showActiveGameInfo(gameState);
    } else if (gameState.status === 'IN_PROGRESS') {
        startGameplay(gameState);
    } else if (gameState.status === 'FINISHED' || gameState.status === 'CANCELLED') {
        showGameResults(gameState);
        hideActiveGameInfo();
    }
}

async function startGame() {
    try {
        const response = await fetch(`http://localhost:8080/api/games/${currentGame.gameCode}/start`, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });

        if (!response.ok) {
            const error = await response.text();
            showError(error);
        }
    } catch (error) {
        showError('Ошибка запуска игры: ' + error.message);
    }
}

function startGameplay(gameState) {
    document.getElementById('gameLobby').style.display = 'none';
    document.getElementById('gameArea').style.display = 'block';

    initializeGameArea(gameState);
    setupTextDisplay(gameState.textContent);
    startGameTimer();

    const userInput = document.getElementById('userInput');
    userInput.disabled = false;
    userInput.focus();
    userInput.addEventListener('input', handleTyping);
}

function initializeGameArea(gameState) {
    const player1Progress = document.getElementById('player1Progress');
    const player2Progress = document.getElementById('player2Progress');

    player1Progress.querySelector('.player-name').textContent = gameState.player1.username;
    player2Progress.querySelector('.player-name').textContent = gameState.player2.username;

    if (gameState.player1.username === currentUser) {
        player1Progress.classList.add('current-user');
        player2Progress.classList.add('opponent');
    } else {
        player2Progress.classList.add('current-user');
        player1Progress.classList.add('opponent');
    }

    updatePlayerProgress(gameState.player1, player1Progress);
    updatePlayerProgress(gameState.player2, player2Progress);
}

function setupTextDisplay(text) {
    const textDisplay = document.getElementById('textToType');
    textDisplay.innerHTML = '';

    for (let i = 0; i < text.length; i++) {
        const span = document.createElement('span');
        span.textContent = text[i];
        span.id = `char-${i}`;
        textDisplay.appendChild(span);
    }
}

function handleTyping(event) {
    if (!currentGame || currentGame.status !== 'IN_PROGRESS') return;

    const userInput = event.target.value;
    const textContent = currentGame.textContent;

    if (!isTyping && userInput.length > 0) {
        isTyping = true;
        startTime = Date.now();
    }

    updateTextHighlighting(userInput, textContent);

    const stats = calculateTypingStats(userInput, textContent);
    sendProgressUpdate(userInput.length, stats.wpm, stats.accuracy);

    if (userInput === textContent) {
        finishGame();
    }
}

function updateTextHighlighting(userInput, textContent) {
    for (let i = 0; i < textContent.length; i++) {
        const charElement = document.getElementById(`char-${i}`);

        if (i < userInput.length) {
            if (userInput[i] === textContent[i]) {
                charElement.className = 'char-correct';
            } else {
                charElement.className = 'char-incorrect';
            }
        } else if (i === userInput.length) {
            charElement.className = 'char-current';
        } else {
            charElement.className = '';
        }
    }
}

function calculateTypingStats(userInput, textContent) {
    if (!startTime || userInput.length === 0) {
        return { wpm: 0, accuracy: 100 };
    }

    const timeElapsed = (Date.now() - startTime) / 1000 / 60; // в минутах
    const wordsTyped = userInput.length / 5;
    const wpm = Math.round(wordsTyped / timeElapsed);

    let correctChars = 0;
    for (let i = 0; i < userInput.length; i++) {
        if (userInput[i] === textContent[i]) {
            correctChars++;
        }
    }

    const accuracy = Math.round((correctChars / userInput.length) * 100);

    return { wpm: isNaN(wpm) ? 0 : wpm, accuracy: isNaN(accuracy) ? 100 : accuracy };
}

function sendProgressUpdate(position, wpm, accuracy) {
    if (stompClient && stompClient.connected) {
        stompClient.send(`/app/game/${currentGame.gameCode}/progress`, {}, JSON.stringify({
            username: currentUser,
            currentPosition: position,
            wpm: wpm,
            accuracy: accuracy
        }));
    }
}

function updatePlayerProgress(playerData, progressElement) {
    if (!playerData) return;

    const progressFill = progressElement.querySelector('.progress-fill');
    const wpmElement = progressElement.querySelector('.wpm');
    const accuracyElement = progressElement.querySelector('.accuracy');

    const textLength = currentGame.textContent.length;
    const progressPercent = (playerData.currentPosition / textLength) * 100;

    progressFill.style.width = `${Math.min(progressPercent, 100)}%`;
    wpmElement.textContent = `${Math.round(playerData.wpm)} WPM`;
    accuracyElement.textContent = `${Math.round(playerData.accuracy)}%`;
}

function startGameTimer() {
    const timerElement = document.getElementById('gameTimer');
    const gameStartTime = Date.now();

    gameTimer = setInterval(() => {
        const elapsed = Math.floor((Date.now() - gameStartTime) / 1000);
        const minutes = Math.floor(elapsed / 60);
        const seconds = elapsed % 60;
        timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    }, 1000);
}

function finishGame() {
    const userInput = document.getElementById('userInput');
    userInput.disabled = true;

    if (gameTimer) {
        clearInterval(gameTimer);
        gameTimer = null;
    }

    const textLength = currentGame.textContent.length;
    const stats = calculateTypingStats(userInput.value, currentGame.textContent);
    sendProgressUpdate(textLength, stats.wpm, stats.accuracy);

    document.getElementById('gameStatus').textContent = 'Ожидание результатов...';
}

function stopGame() {
    if (confirm('Вы уверены, что хотите остановить игру? Результат не будет засчитан.')) {
        if (gameTimer) {
            clearInterval(gameTimer);
            gameTimer = null;
        }

        const userInput = document.getElementById('userInput');
        userInput.disabled = true;

        document.getElementById('gameStatus').textContent = 'Игра остановлена';

        setTimeout(() => {
            leaveGame();
        }, 2000);

        if (stompClient && stompClient.connected && currentGame) {
            stompClient.send(`/app/game/${currentGame.gameCode}/leave`, {}, JSON.stringify({
                username: currentUser,
                reason: 'stopped'
            }));
        }
    }
}

function showGameResults(gameState) {
    document.getElementById('userInput').style.display = 'none';
    document.getElementById('textToType').style.display = 'none';
    document.getElementById('gameStatus').textContent = 'Игра завершена';

    document.getElementById('gameResults').style.display = 'block';

    const winnerText = document.getElementById('winnerText');
    const finalStats = document.getElementById('finalStats');

    if (gameState.winnerUsername === currentUser) {
        winnerText.textContent = 'Поздравляем! Вы выиграли!';
        winnerText.style.color = '#28a745';
    } else if (gameState.winnerUsername) {
        winnerText.textContent = `Победитель: ${gameState.winnerUsername}`;
        winnerText.style.color = '#dc3545';
    } else {
        winnerText.textContent = 'Игра завершена';
        winnerText.style.color = '#ffc107';
    }

    const currentPlayerData = gameState.player1.username === currentUser ? gameState.player1 : gameState.player2;
    const opponentData = gameState.player1.username === currentUser ? gameState.player2 : gameState.player1;

    finalStats.innerHTML = `
        <div class="results-section">
            <h4>Ваши результаты:</h4>
            <div>Скорость: ${Math.round(currentPlayerData.wpm)} WPM</div>
            <div>Точность: ${Math.round(currentPlayerData.accuracy)}%</div>
            <div>Позиция: ${currentPlayerData.currentPosition}/${currentGame.textContent.length} символов</div>
        </div>
        <div class="results-section">
            <h4>Результаты соперника:</h4>
            <div>Скорость: ${Math.round(opponentData.wpm)} WPM</div>
            <div>Точность: ${Math.round(opponentData.accuracy)}%</div>
            <div>Позиция: ${opponentData.currentPosition}/${currentGame.textContent.length} символов</div>
        </div>
    `;

    if (gameTimer) {
        clearInterval(gameTimer);
        gameTimer = null;
    }

    const stopBtn = document.getElementById('stopGameBtn');
    if (stopBtn) {
        stopBtn.style.display = 'none';
    }
}

function copyGameCode() {
    const gameCode = document.getElementById('lobbyGameCode').textContent;
    navigator.clipboard.writeText(gameCode).then(() => {
        showSuccess('Код игры скопирован!');
    });
}

function leaveGame() {
    currentGame = null;
    isTyping = false;
    startTime = null;

    if (gameTimer) {
        clearInterval(gameTimer);
        gameTimer = null;
    }

    document.getElementById('gameLobby').style.display = 'none';
    document.getElementById('gameArea').style.display = 'none';
    document.getElementById('gameResults').style.display = 'none';
    document.getElementById('waitingGames').style.display = 'none';

    document.getElementById('gameMenu').style.display = 'block';

    resetGameInterface();

    hideActiveGameInfo();
}

function resetGameInterface() {
    const userInput = document.getElementById('userInput');
    if (userInput) {
        userInput.value = '';
        userInput.disabled = true;
        userInput.style.display = 'block';
    }

    const textToType = document.getElementById('textToType');
    if (textToType) {
        textToType.innerHTML = '';
        textToType.style.display = 'block';
    }

    const gameStatus = document.getElementById('gameStatus');
    if (gameStatus) {
        gameStatus.textContent = 'Готовность...';
    }

    const stopBtn = document.getElementById('stopGameBtn');
    if (stopBtn) {
        stopBtn.style.display = 'block';
    }

    resetPlayersProgress();
}

function resetPlayersProgress() {
    const progressElements = document.querySelectorAll('.player-progress');
    progressElements.forEach(element => {
        element.classList.remove('current-user', 'opponent');

        const nameElement = element.querySelector('.player-name');
        if (nameElement) {
            nameElement.textContent = '';
        }

        const progressFill = element.querySelector('.progress-fill');
        if (progressFill) {
            progressFill.style.width = '0%';
        }

        const wpmElement = element.querySelector('.wmp');
        const accuracyElement = element.querySelector('.accuracy');
        if (wpmElement) wpmElement.textContent = '0 WPM';
        if (accuracyElement) accuracyElement.textContent = '100%';
    });
}

function newGame() {
    leaveGame();
}

function goToMain() {
    window.location.href = 'index.html';
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = 'login.html';
}

function showError(message) {
    alert('Ошибка: ' + message);
}

function showSuccess(message) {
    alert(message);
}

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        if (document.getElementById('joinGameModal').style.display === 'block') {
            closeJoinGameModal();
        }
    }

    if (event.key === 'Enter' && document.getElementById('joinGameModal').style.display === 'block') {
        joinGameByCode();
    }
});
