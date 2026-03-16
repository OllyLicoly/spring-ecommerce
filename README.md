# spring-ecommerce

Учебный e-commerce проект на Spring Boot: каталог товаров, корзина, заказы, админ-панель.

## Stack
Java 17, Spring Boot, Spring Security, Spring Data JPA, Thymeleaf, PostgreSQL, JUnit5, Testcontainers, GitHub Actions.

## 1) Start database
docker compose up -d

## 2) Run application
mvn spring-boot:run

Открыть в браузере:
http://localhost:8080

## 3) Demo users
(создаются автоматически при первом запуске)
Admin: admin / admin
User: user / user

## 4) Tests
mvn test

## 5) CI
Тесты автоматически запускаются в GitHub Actions на push/PR.
