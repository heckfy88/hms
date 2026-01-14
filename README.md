# Hotel Management System - Микросервисная архитектура

Проект управления отелями и бронирования номеров с распределенной архитектурой. 

## Технологический стек

- Java 17
- Spring Boot 3.3.5
- Spring Cloud 2023.0.3
- Spring Security + JWT
- Spring Data JPA + H2 (in-memory)
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- OpenFeign для межсервисного взаимодействия
- Spring Retry для устойчивости
- Lombok
- JUnit 5 + MockMvc

## Архитектура системы

Система состоит из 4 модулей:

1. **Service Discovery** (8761) - Service Discovery
2. **Gateway** (8084) - Точка входа, маршрутизация
3. **Hotel Management** (8082) - Управление отелями и номерами
4. **Booking** (8083) - Бронирования, аутентификация пользователей

Сперва запускается Service Discovery, затем сервисы, затем Gateway

В проекте добавлен swagger, написаны интеграционные тесты, реализована аутентификация пользователей 
через oauth2 resource server по канонам последнего спринга.   

В качестве БД используется H2 - in-memory.
Распределение по номерам равномерное, завязывается на количество заселений.