# Backend for a game

* new round starts every second, each one must be counted consistently, it finishes with 
    random digit 1 or 0;
* new session gets to balance 1000 units;
* any time any session can subscribe on participating in next round, if it sends GET request, 
    after receiving one -1 unit is subtracted;
* if next gotten number is equal 1 then all participants add +2 units to balance in this round,
    else 0 - none

It is required to store balance in MySQL, values and numbers of rounds either,
and inform client over SSE about changing balance and last thrown digit of round.

# ***

сделать backend (spring-boot) для игры:

- раз в секунду начинется новый раунд, раунды 
    должны быть пронумерованы последовательно, исходом 
    раунда явлется случайное число 1 или 0;
- каждой новой сессии дается начальный баланс - 1000 условных единиц;
- в любой момент любая из сессий может подписаться 
    на участие в очередном раунде, для чего она отправляет 
    соотв. GET запрос, по получении которого с баланса 
    списывается 1 условная единица;
- если следующее выброшенное число равно 1, то 
    участвущие в этом раунде получают +2 единицы на 
    баланс, если 0 - ничего.

балансы необходимо хранить в mysql, значения и 
номера раундов - тоже, а об изменениях балансов и 
последнем выброшенном случайном числе - информировать 
сессии через EventSource (SSE).
