let timer;
let startTime;
let testRunning = false;
const testTextEasy = "Easy sample text for typing...";
const testTextMedium = "This is a medium difficulty text for testing your typing skills...";
const testTextHard = "This is a hard difficulty text with more complex sentences, punctuation, and grammar...";
let testText = testTextEasy; // Начальный уровень

document.getElementById('displayText').textContent = testText;
document.getElementById('startBtn').addEventListener('click', startTest);
document.getElementById('resetBtn').addEventListener('click', resetTest);
document.getElementById('inputText').addEventListener('input', checkTyping);
document.getElementById('retryBtn').addEventListener('click', resetTest);
document.getElementById('difficultySelect').addEventListener('change', setDifficulty);

function startTest() {
    if (testRunning) return;

    testRunning = true;
    startTime = new Date().getTime();

    document.getElementById('inputText').value = '';
    document.getElementById('timeDisplay').textContent = '0';
    document.getElementById('speedDisplay').textContent = '0';
    document.getElementById('errorDisplay').textContent = '0';
    document.getElementById('finalResults').classList.add('hidden');

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
}

function checkTyping() {
    const inputText = document.getElementById('inputText').value;
    const elapsedTime = (new Date().getTime() - startTime) / 60000;

    const wordCount = inputText.trim().split(/\s+/).length;
    const wpm = Math.round(wordCount / elapsedTime);
    document.getElementById('speedDisplay').textContent = isNaN(wpm) ? 0 : wpm;

    const errors = calculateErrors(inputText, testText);
    document.getElementById('errorDisplay').textContent = errors;

    if (inputText === testText) {
        clearInterval(timer);
        testRunning = false;
        document.getElementById('finalSpeed').textContent = wpm;
        document.getElementById('finalErrors').textContent = errors;
        document.getElementById('finalResults').classList.remove('hidden');
    }
}

function calculateErrors(input, original) {
    let errorCount = 0;
    const inputWords = input.trim().split('');
    const originalWords = original.trim().split('');

    inputWords.forEach((char, index) => {
        if (char !== originalWords[index]) {
            errorCount++;
        }
    });

    return errorCount;
}

function setDifficulty() {
    const difficulty = document.getElementById('difficultySelect').value;

    switch (difficulty) {
        case 'easy':
            testText = testTextEasy;
            break;
        case 'medium':
            testText = testTextMedium;
            break;
        case 'hard':
            testText = testTextHard;
            break;
    }

    document.getElementById('displayText').textContent = testText;
}
