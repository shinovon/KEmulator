#!/bin/bash
# KEmulator nnx64 starter

JAVA="java"
XMX="-Xmx512M"

SELF=`realpath $0`
SELF_DIR=`dirname $SELF`
KEMULATOR_JAR="$SELF_DIR/KEmulator.jar"
JAVA_VER=$($JAVA -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{sub("^$", "0", $2); print $1$2}')

if [ -z $1 ]
then
  if [ $JAVA_VER -ge 90 ]
  then
    "$JAVA" -XX:+IgnoreUnrecognizedVMOptions -XstartOnFirstThread \
    "-Djava.library.path=$SELF_DIR" "-javaagent:$KEMULATOR_JAR" \
    --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
    --add-opens java.base/java.lang.ref=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED \
	-Djna.nosys=true -Dfile.encoding=UTF-8 "$XMX" \
	-jar "$KEMULATOR_JAR" -s
  else
    "$JAVA" -XX:+IgnoreUnrecognizedVMOptions -XstartOnFirstThread \
    "-Djava.library.path=$SELF_DIR" "-javaagent:$KEMULATOR_JAR" \
    -Djna.nosys=true -Dfile.encoding=UTF-8 "$XMX" \
	-jar "$KEMULATOR_JAR" -s
  fi
else
  FULL_JAR=$(realpath "$1")
  if [ $JAVA_VER -ge 90 ]
  then
    "$JAVA" -XX:+IgnoreUnrecognizedVMOptions -XstartOnFirstThread \
    "-Djava.library.path=$SELF_DIR" "-javaagent:$KEMULATOR_JAR" \
    --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
    --add-opens java.base/java.lang.ref=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED \
	-Djna.nosys=true -Dfile.encoding=UTF-8 "$XMX" \
	-jar "$KEMULATOR_JAR" -s -jar "$FULL_JAR"
  else
    "$JAVA" -XX:+IgnoreUnrecognizedVMOptions -XstartOnFirstThread \
    "-Djava.library.path=$SELF_DIR" "-javaagent:$KEMULATOR_JAR" \
    -Djna.nosys=true -Dfile.encoding=UTF-8 "$XMX" \
    -jar "$KEMULATOR_JAR" -s -jar "$FULL_JAR"
  fi
fi
