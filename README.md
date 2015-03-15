# boot-ping-pong

Server and client for high load ping pong interactions.

Using Spring Boot, Spring MVC, Cassandra (embedded).

Сервер, обрабатывает  протокольную команду “PING <userId>”, на которую отвечет “PONG N”, где N количество раз, сколько этот клиент запрашивал PING.
Сервер должен отвечет на POST запросы по адресу http://localhost/handler, тип команды и аргументы пакуются в JSON внутри тела запроса.

## Описание протокола
В теле клиентского запроса должен быть JSON:

```json
{
  "command": "PING",
  "userId": "some_id"
}
```

Ответ от сервера представляет собой JSON вида (в случае ошибки сервер тело не присылает):

```json
{
  "pong": 1
}
```

## Описание сервера

Сервер использует Spring Boot с внедренным Tomcat. База данных - внедренная Cassandra (но можно настроить и на внешнюю).

Внедренная Cassandra хранит данные в папке server/target.

Настройки сервера в файле application.properties. Там прописан порт (80), настройки Кассандры и логгирования.

## How to run

### Server
From server folder execute: mvn spring-boot:run

### Client
From client folder execute: mvn spring-boot:run

## Requirements

Java 1.8, Maven 3.2
