@echo off

set TCC4JAVA_VERSION=0.3

set CLASSPATH=.\lib\commons-io-2.2.jar
set CLASSPATH=%CLASSPATH%;.\lib\hamcrest-core-1.3.jar
set CLASSPATH=%CLASSPATH%;.\lib\jna-4.1.0.jar
set CLASSPATH=%CLASSPATH%;.\lib\junit-4.11.jar
set CLASSPATH=%CLASSPATH%;.\lib\slf4j-api-1.7.7.jar
set CLASSPATH=%CLASSPATH%;.\lib\slf4j-simple-1.7.7.jar
set CLASSPATH=%CLASSPATH%;.\dist\%TCC4JAVA_VERSION%\TCC4Java-%TCC4JAVA_VERSION%.jar
set CLASSPATH=%CLASSPATH%;.\dist\%TCC4JAVA_VERSION%\TCC4Java-%TCC4JAVA_VERSION%-test.jar

set TESTS=AllTests
IF NOT "%1"=="" (
	set TESTS="%1"
)
java -cp %CLASSPATH% org.junit.runner.JUnitCore cn.com.tcc.%TESTS%
pause
