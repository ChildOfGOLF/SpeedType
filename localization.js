const translations = {
    en: {
        title: "SpeedType - Typing Speed Test",
        welcome: "Welcome",
        login: "Login",
        register: "Register",
        logout: "Logout",
        myResults: "My Results",
        interface: "Interface",

        difficulty: "Difficulty",
        textLanguage: "Language",
        easy: "Easy",
        medium: "Medium",
        hard: "Hard",
        startTest: "Start Test",
        reset: "Reset",
        tryAgain: "Try Again",

        time: "Time (s)",
        speed: "Speed (WPM)",
        errors: "Errors",
        testComplete: "Test Complete!",
        yourTypingSpeed: "Your typing speed",
        totalErrors: "Total errors",

        loadingText: "Loading text...",
        typeHere: "Type the text above here...",
        loadingResults: "Loading your results...",
        errorLoadingText: "Error loading text. Please try again.",
        errorSavingResult: "Error saving result. Please check your connection.",
        connectionError: "Connection error. Please try again.",

        date: "Date",
        wpm: "WPM",
        difficulty: "Difficulty",
        language: "Language",
        noResults: "No results found.",

        english: "English",
        russian: "Russian"
    },

    ru: {
        title: "SpeedType - Тест Скорости Печати",
        welcome: "Добро пожаловать",
        login: "Войти",
        register: "Регистрация",
        logout: "Выйти",
        myResults: "Мои Результаты",
        interface: "Интерфейс",

        difficulty: "Сложность",
        textLanguage: "Язык",
        easy: "Легкий",
        medium: "Средний",
        hard: "Сложный",
        startTest: "Начать Тест",
        reset: "Сброс",
        tryAgain: "Попробовать Снова",

        time: "Время (с)",
        speed: "Скорость (сл/мин)",
        errors: "Ошибки",
        testComplete: "Тест Завершен!",
        yourTypingSpeed: "Ваша скорость печати",
        totalErrors: "Всего ошибок",

        loadingText: "Загрузка текста...",
        typeHere: "Введите текст выше здесь...",
        loadingResults: "Загрузка ваших результатов...",
        errorLoadingText: "Ошибка загрузки текста. Попробуйте еще раз.",
        errorSavingResult: "Ошибка сохранения результата. Проверьте соединение.",
        connectionError: "Ошибка соединения. Попробуйте еще раз.",

        date: "Дата",
        wpm: "сл/мин",
        difficulty: "Сложность",
        language: "Язык",
        noResults: "Результаты не найдены.",

        english: "Английский",
        russian: "Русский"
    }
};

let currentLanguage = localStorage.getItem('interfaceLanguage') || 'en';

function t(key) {
    return translations[currentLanguage][key] || key;
}

function changeInterfaceLanguage(lang) {
    currentLanguage = lang;
    localStorage.setItem('interfaceLanguage', lang);
    updateInterface();
}

function updateInterface() {
    document.title = t('title');

    const elements = document.querySelectorAll('[data-translate]');
    elements.forEach(element => {
        const key = element.getAttribute('data-translate');
        if (element.tagName === 'INPUT' && element.type === 'text') {
            element.placeholder = t(key);
        } else if (element.tagName === 'TEXTAREA') {
            element.placeholder = t(key);
        } else if (element.tagName === 'OPTION') {
            element.textContent = t(key);
        } else {
            element.textContent = t(key);
        }
    });

    const langSelect = document.getElementById('interfaceLanguageSelect');
    if (langSelect) {
        langSelect.value = currentLanguage;
    }
}

function initLocalization() {
    updateInterface();
}
