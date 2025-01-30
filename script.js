let timer;
let startTime;
let testRunning = false;
let testText = "";

document.getElementById('startBtn').addEventListener('click', startTest);
document.getElementById('resetBtn').addEventListener('click', resetTest);
document.getElementById('inputText').addEventListener('input', checkTyping);
document.getElementById('retryBtn').addEventListener('click', resetTest);
document.getElementById('difficultySelect').addEventListener('change', setDifficulty);

function startTest() {
    if (testRunning || !testText) return;

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

async function setDifficulty() {
    const difficulty = document.getElementById('difficultySelect').value;

    const url = `http://localhost:8080/texts?difficulty=${difficulty}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch text');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.length > 0) {
                const textContent = data[0].content;
                document.getElementById('displayText').textContent = textContent;
            } else {
                document.getElementById('displayText').textContent = 'No text available for this difficulty.';
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}

