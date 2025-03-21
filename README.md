# SpeedType

SpeedType – это веб-приложение для тестирования скорости печати, аутентификации пользователей и хранения их результатов.

## Функциональность
- Регистрация и авторизация пользователей (JWT)
- Выбор уровня сложности текста
- Прохождение теста на скорость печати
- Сохранение результатов (скорость, точность, дата)
- Просмотр личной статистики
- Топ-10 лучших результатов

## Технологии
- **Backend:** Java, Spring Boot (Security, Data JPA), H2 Database, JWT
- **Frontend:** HTML, CSS, JavaScript (Fetch API для взаимодействия с сервером)
- **База данных:** H2

## Запуск проекта

### Клонирование репозитория
```bash
git clone https://github.com/ChildOfGOLF/SpeedType/.git
cd SpeedType
```

### Настройка переменных окружения
```properties
jwt.secret=your_secret_key
spring.datasource.url=jdbc:h2:mem:typingdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### Запуск сервера
```bash
mvn spring-boot:run
```

### Запуск фронтенда
Открой `index.html` в браузере.

### Доступ к API
- **Регистрация:** `POST /users/register`
- **Логин:** `POST /users/login`
- **Сохранение результата:** `POST /results`
- **Получение статистики:** `GET /results/user/{username}`
- **Получение текста для теста:** `GET /texts?difficulty=easy|medium|hard`


