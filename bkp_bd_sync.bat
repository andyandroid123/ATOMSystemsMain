@echo off

SET pgpassword=manager
SET PG_VERSION=9.4
SET PG_PATH="C:\Program Files\PostgreSQL\%PG_VERSION%\bin\"
SET PG_IP=localhost
SET PG_DB=LIDER

SET VITAL_RIP=143.255.143.158
SET VITAL_RUSER=ftpserver
SET VITAL_RPASS=123456

SET FECHA=%DATE%_%TIME%_
SET FECHA=%FECHA:/=%
SET FECHA=%FECHA::=-%
SET FECHA=%FECHA:.=-%
SET DAY=%DATE%
SET DAY=%DAY:~0,2%

SET BKP_DIR="C:\BACKUP\"
SET VITAL_RNAME="%PG_DB%_%DAY%"
@echo "Procesando backup en %BKP_DIR% (%PG_DB%)"
	cd %BKP_DIR%
	%PG_PATH%\pg_dump.exe --host %PG_IP% --port 5432 --username postgres --format custom --blobs --verbose --file "%PG_DB%_%FECHA%.backup" "%PG_DB%"
@echo "Backup en %BKP_DIR% Finalizado (%PG_DB%)"

SET BKP_DIR="E:\BACKUP\LIDERSA\"
SET VITAL_RNAME="%PG_DB%_%DAY%"
@echo "Procesando backup en %BKP_DIR% (%PG_DB%)"
	cd %BKP_DIR%
	%PG_PATH%\pg_dump.exe --host %PG_IP% --port 5432 --username postgres --format custom --blobs --verbose --file "%PG_DB%_%FECHA%.backup" "%PG_DB%"
@echo "Backup en %BKP_DIR% Finalizado (%PG_DB%)"
@echo "Enviando backup de %BKP_DIR% (%PG_DB%)"
	"C:\putty\pscp.exe" -scp -P 2222 -pw %VITAL_RPASS% "%PG_DB%_%FECHA%.backup" %VITAL_RUSER%@%VITAL_RIP%:/mnt/slave_disk/BACKUP_Clientes/LIDER/MATRIZ/%VITAL_RNAME%.backup
@echo "Backup en %BKP_DIR% Enviado (%PG_DB%)"

@echo "Procesando Clasificacion Automatica de Clientes...(%PG_DB%)"
	%PG_PATH%\psql.exe --command "SELECT * FROM actualiza_clasificacion_cliente()" --host %PG_IP% --port 5432 --username postgres "%PG_DB%"
@echo "Proceso de Clasificacion Automatica de Clientes Finalizado...(%PG_DB%)"

@echo "Procesando Giro Diario...(%PG_DB%)"
	%PG_PATH%\psql.exe --command "SELECT * FROM giro_diario()" --host %PG_IP% --port 5432 --username postgres "%PG_DB%"
@echo Giro Diario Finalizado (%PG_DB%)

