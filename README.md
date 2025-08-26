# SpeedType

SpeedType — это веб-приложение для тестирования скорости печати с поддержкой многопользовательских онлайн-игр, статистики и админ-панели. Проект полностью контейнеризирован с помощью Docker.

## Функциональность
- Регистрация и авторизация пользователей (JWT)
- Выбор уровня сложности текста
- Прохождение теста на скорость печати (одиночно и онлайн с соперником)
- Сохранение результатов
- Просмотр личной статистики
- Топ-10 лучших результатов
- Админ-панель для управления пользователями и текстами
- Многопользовательские игры через WebSocket
- Автоматическое закрытие неактивных игр

## Технологии
- **Backend:** Java, Spring Boot (Security, Data JPA, WebSocket), PostgreSQL, Redis, JWT
- **Frontend:** HTML, CSS, JavaScript (Fetch API, WebSocket)
- **Docker:** docker-compose для запуска всех сервисов

## Быстрый старт (Docker)

### Клонирование репозитория
```bash
git clone https://github.com/ChildOfGOLF/SpeedType.git
cd SpeedType
```

### Запуск через Docker Compose
```bash
docker-compose up --build
```

- Приложение будет доступно по адресу: http://localhost (фронтенд)
- Backend API: http://localhost:8080
- Админ-панель: http://localhost/admin/admin.html

### Переменные окружения

Для backend (SpeedTypeAPI/.env):
```
SERVER_PORT=8080
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379
DB_URL=jdbc:postgresql://postgres:5432/speedtype
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=your_secret_key
```

## Структура проекта
- `frontend/` — клиентская часть (HTML, CSS, JS)
- `SpeedTypeAPI/` — серверная часть (Spring Boot, Java)
- `docker-compose.yml` — запуск всех сервисов

## Миграции и тестовые данные
- SQL-скрипты для создания таблиц и тестовых данных находятся в `SpeedTypeAPI/src/main/resources/`

## Примечания
- Для работы WebSocket и Redis ничего дополнительно настраивать не нужно — всё запускается через Docker.
- Для доступа к админ-панели используйте пользователя с ролью ADMIN.

