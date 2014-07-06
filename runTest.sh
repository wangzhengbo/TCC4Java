#!/usr/bin/env bash

TCC4JAVA_VERSION=0.3

CLASSPATH=./lib/commons-io-2.2.jar
CLASSPATH=$CLASSPATH:./lib/hamcrest-core-1.3.jar
CLASSPATH=$CLASSPATH:./lib/junit-4.11.jar
CLASSPATH=$CLASSPATH:./lib/slf4j-api-1.7.7.jar
CLASSPATH=$CLASSPATH:./lib/slf4j-simple-1.7.7.jar
CLASSPATH=$CLASSPATH:./dist/$TCC4JAVA_VERSION/TCC4Java-$TCC4JAVA_VERSION.jar
CLASSPATH=$CLASSPATH:./dist/$TCC4JAVA_VERSION/TCC4Java-$TCC4JAVA_VERSION-test.jar

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
