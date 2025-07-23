#!/bin/bash
# KEmulator nnx64 starter

JAVA="java"
XMX="-Xmx512M"

SELF=`realpath $0`
SELF_DIR=`dirname $SELF`
KEMULATOR_JAR="$SELF_DIR/KEmulator.jar"
JAVA_VER=$($JAVA -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{sub("^$", "0", $2); print $1$2}')
ARGS="$XMX -Djna.nosys=true -Djava.library.path=$SELF_DIR -Dfile.encoding=UTF-8 -javaagent:$KEMULATOR_JAR -XX:+IgnoreUnrecognizedVMOptions -XstartOnFirstThread"

if [ $JAVA_VER -ge 90 ]
then
  ARGS="$ARGS --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.ref=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED"
fi

if [ -z $1 ]
then
  "$JAVA" $ARGS -jar "$KEMULATOR_JAR" -s
else
  FULL_JAR=$(realpath "$1")
  "$JAVA" $ARGS -jar "$KEMULATOR_JAR" -s -jar "$FULL_JAR"
fi
