# Backend for a game

* new round starts every second, each one must be counted consistently, it finishes with 
    a random digit 1 or 0;
* new session adds to the account 1000 units;
* any time any session can subscribe for participating in the next round, when it sends GET request, 
    after receiving one -1 unit is subtracted;
* when the next gotten number equals 1 then all clients add +2 units to their accounts in this round,
    else 0 - none

It is required to store accounts, values and ids of rounds in MySQL,
and to inform a client over SSE about changing of the account and the last thrown digit.

# Russian

сделать backend (spring-boot) для игры:

- раз в секунду начинется новый раунд, раунды должны быть пронумерованы последовательно, исходом 
    раунда явлется случайное число 1 или 0;
- каждой новой сессии дается начальный баланс - 1000 условных единиц;
- в любой момент любая из сессий может подписаться на участие в очередном раунде, для чего она отправляет 
    соотв. GET запрос, по получении которого с баланса списывается 1 условная единица;
- если следующее выброшенное число равно 1, то участвущие в этом раунде получают +2 единицы на 
    баланс, если 0 - ничего.

балансы необходимо хранить в mysql, значения и номера раундов - тоже, а об изменениях балансов и 
последнем выброшенном случайном числе - информировать сессии через EventSource (SSE).
