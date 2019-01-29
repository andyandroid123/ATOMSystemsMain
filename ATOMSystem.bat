@echo off
PING 127.0.0.1 -n 1 | FIND "TTL=" >NUL
IF ERRORLEVEL 1 GOTO Next

   @echo CASA DE BEBIDAS "LA ROSARINA" 
   @echo Abriendo su SISTEMA DE GESTION, aguarde... 

REM ====================================
REM Para soporte favor comunicarse con =
REM Ing. Andrés Melgarejo ==============
REM Cel: 0985 488 553 ==================		       
REM CDE - PARAGUAY =====================
REM ====================================

   cd\ATOMSystemsMain
   java -Xmx1024m -jar ATOMSystems-Main.jar
   
