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
        document.getElementById('finalSpeed').textContent = isNaN(wpm) ? 0 : wpm;
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
            console.log("Received data:", data);
        
            if (Array.isArray(data) && data.length > 0) {
                const randomIndex = Math.floor(Math.random() * data.length);
                const textContent = data[randomIndex].content;
                console.log("Selected text:", textContent);
        
                document.getElementById('displayText').textContent = textContent;
                testText = textContent;
            } 
            else if (data && typeof data === "object" && "content" in data) {
                console.log("Single object received:", data.content);
                document.getElementById('displayText').textContent = data.content;
                testText = data.content;
            } 
            else {
                document.getElementById('displayText').textContent = 'No text available for this difficulty.';
            }
        })
}



