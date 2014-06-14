@echo off

D:
cd D:\eclipse\workspace\TCC4Java

set JAVA_HOME=D:\Program Files\java\jdk1.6.0_26
set ANT_HOME=D:\apache-ant-1.8.2

set path=%JAVA_HOME%\bin;%ANT_HOME%\bin;%path%
ant
pause