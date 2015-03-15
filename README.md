# boot-ping-pong

Server for high load ping pong interactions and client for testing

Сервер, обрабатывает  протокольную команду “PING <userId>”, на которую отвечет “PONG N”, где N количество раз, сколько этот клиент запрашивал PING.
Сервер должен отвечет на POST запросы по адресу http://localhost/handler, тип команды и аргументы пакуются в JSON внутри тела запроса.
