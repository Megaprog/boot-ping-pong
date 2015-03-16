# boot-ping-pong

Server and client for high load ping pong interactions.

Using Spring Boot, Spring MVC, Cassandra (embedded), streaming-cassandra library for binding (see
    https://github.com/Megaprog/streaming-cassandra).

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

Настройки сервера в файле server/src/main/resources/application.properties. Там прописан порт (80), настройки Кассандры и логгирования.

## Описание клиента

Клиент делает запросы на серввер (возможно в несколько потоков) и выводит в консоль результаты.
Сначала генерятся идентификаторы пользователей, затем каждый поток случайным образом выбирает id пользователя и делает запрос на сервер.
Полученные ответы подсчитываются. Когда количество потоков больше одного, возможно, что информация на клиенте и на сервере на некоторое время
разойдется. На сервере данные всегда точные, а на клиент информация приходит с некоторой задержкой.

Настройки клиента в файле client/src/main/resources/application.properties

url=http://localhost/handler - куда делать запросы

attempts=10 - сколько запросов делает каждый поток

threads=1 - сколько запускать потоков

users=1 - сколько сгенерировать индетификаторов пользователей

## How to run

### Server
From server folder execute: mvn spring-boot:run

### Client
From client folder execute: mvn spring-boot:run

## Requirements

Java 1.8, Maven 3.2
