## Запуск приложения
1) Собрать проект - mvn clean package
2) Запустить docker - docker compose up --build

## Использование
- POST http://localhost:8090/api/v1/users/login - Вход, получение токена

- POST http://localhost:8090/api/v1/users/register - Регистрация пользователя

- POST/PUT/POST/DELETE - Доступны только для пользователей с ролью Admin

- GET/PUT/POST/DELETE http://localhost:8090/api/v1/actors (CRUD для таблицы Актеров) 
- GET/PUT/POST/DELETE http://localhost:8090/api/v1/genres (CRUD для таблицы Жанров)
- GET/PUT/POST/DELETE http://localhost:8090/api/v1/movies (CRUD для таблицы Фильмов)
- GET/PUT/POST/DELETE http://localhost:8090/api/v1/reviews (CRUD для таблицы Отзывов)
  
- POST http://localhost:8090/api/v1/reviews (Доступен пользователям с ролью User)

