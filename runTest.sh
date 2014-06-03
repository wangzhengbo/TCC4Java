#!/usr/bin/env bash

CLASSPATH=./lib/commons-io-2.4.jar:./lib/hamcrest-core-1.3.jar:./lib/junit-4.11.jar:./lib/slf4j-api-1.7.7.jar:./lib/slf4j-simple-1.7.7.jar:./dist/0.1/TCC4Java-0.1.jar:./dist/0.1/TCC4Java-0.1-test.jar

if [ "$(uname -s)" == "GNU/kFreeBSD" ]; then
  LIB_JNA=./lib/kFreeBSD/jna-3.2.7.jar
elif [ "$(uname -s)" == "NetBSD" ]; then
  LIB_JNA=./lib/NetBSD/jna-4.1.0.jar
elif [ "$(uname -s)" == "DragonFly" ]; then
  LIB_JNA=./lib/DragonFly/jna-4.1.0.jar
fi

if [ "$LIB_JNA" == "" ]; then
  LIB_JNA=./lib/jna-4.1.0.jar
fi

CLASSPATH=$LIB_JNA:$CLASSPATH

TESTS=AllTests
if [ "$1" != "" ]; then
  TESTS="$1"
fi
java -cp $CLASSPATH org.junit.runner.JUnitCore cn.com.tcc.$TESTS
