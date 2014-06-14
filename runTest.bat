@echo off

set CLASSPATH=.\lib\commons-io-2.2.jar
set CLASSPATH=%CLASSPATH%;.\lib\hamcrest-core-1.3.jar
set CLASSPATH=%CLASSPATH%;.\lib\jna-4.1.0.jar
set CLASSPATH=%CLASSPATH%;.\lib\junit-4.11.jar
set CLASSPATH=%CLASSPATH%;.\lib\slf4j-api-1.7.7.jar
set CLASSPATH=%CLASSPATH%;.\lib\slf4j-simple-1.7.7.jar
set CLASSPATH=%CLASSPATH%;.\dist\0.2\TCC4Java-0.2.jar
set CLASSPATH=%CLASSPATH%;.\dist\0.2\TCC4Java-0.2-test.jar

set TESTS=AllTests
IF NOT "%1"=="" (
	set TESTS="%1"
)
java -cp %CLASSPATH% org.junit.runner.JUnitCore cn.com.tcc.%TESTS%
pause
