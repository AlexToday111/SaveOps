# Runbook: сбой начисления процентов

## Симптомы

- Нет прироста `saveops_interest_accrued_total` после планового окна.
- В таблице `interest_accruals` нет записей за текущую дату.

## Проверки

1. Проверьте логи `interest-service`: `make logs service=interest-service`.
2. Проверьте Redis lock `interest:daily-accrual-lock:<date>`.
3. Проверьте наличие счетов в `tracked_accounts`.
4. Проверьте доступность `account-service` gRPC на `9091`.

## Действия

- Если lock завис, дождитесь TTL или удалите ключ только после проверки, что job не выполняется.
- Если нет tracked accounts, проверьте поток `account.opened` из RabbitMQ.
- Если gRPC недоступен, восстановите `account-service` и повторите job через временное изменение cron или ручной вызов сервисного метода в dev-среде.

