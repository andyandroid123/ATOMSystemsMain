@echo off 
set pgpassword=manager
set FECHA=%DATE% %TIME%
set FECHA=%FECHA:/=%
set FECHA=%FECHA::=-%
set FECHA=%FECHA:.=-%

@echo Procesando Backup (C:)...
"C:\Program Files\PostgreSQL\9.4\bin\"pg_dump.exe --host localhost --port 5432 --username postgres --format custom --blobs --verbose --file "C:\BACKUP\LIDER%FECHA%.backup" "LIDER"
@echo Backup Finalizado

@echo Procesando Backup (E:)...
"C:\Program Files\PostgreSQL\9.4\bin\"pg_dump.exe --host localhost --port 5432 --username postgres --format custom --blobs --verbose --file "D:\BACKUP\LIDERSA\LIDER%FECHA%.backup" "LIDER"
@echo Backup Finalizado

@echo Procesando Clasificación Automática de Clientes...
"C:\Program Files\PostgreSQL\9.4\bin\"psql.exe --command "SELECT * FROM actualiza_clasificacion_cliente()" --host localhost --port 5432 --username postgres "LIDER"
@echo Proceso de Clasificación Automática de Clientes Finalizado...

@echo Procesando Backup (Nueva Esperana E:)...
"C:\Program Files\PostgreSQL\9.4\bin\"pg_dump.exe --host 192.168.5.230 --port 5432 --username postgres --format custom --blobs --verbose --file "D:\BACKUP\LIDERT4\LIDERT4%FECHA%.backup" "LIDERT4"
@echo Backup Finalizado

@echo Procesando Giro Diario...
"C:\Program Files\PostgreSQL\9.4\bin\"psql.exe --command "SELECT * FROM giro_diario()" --host localhost --port 5432 --username postgres "LIDER"
@echo Giro Diario Finalizado
