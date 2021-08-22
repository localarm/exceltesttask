Проект собирается в fat jar. Для запуска используются следующие аргументы:

-l=логин для покдлючения к БД

-u=jdbcurl

-d=директория, гед располагаются папки To_load и Loaded(не обязательно, по умолчанию, проверяет в той же папке)

-p

Например, 

**java -jar exceltesttask-1.0-SNAPSHOT-all.jar -l=testuser -u=jdbc:postgresql://localhost:5432/test_task -p**
