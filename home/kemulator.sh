#!/bin/bash
# KEmulator nnmod x64 xdg/linux / macos starter script. Allows to run empty emulator, JAR file or IDE integration.

# any available from PATH
JAVA="java"
# maybe we are on typical linux distro and there is jre 1.8?
if [ -x "/usr/lib/jvm/java-8-openjdk/bin/java" ]; then
    JAVA="/usr/lib/jvm/java-8-openjdk/bin/java"
fi
if [ -x "/usr/lib/jvm/java-1.8-openjdk/bin/java" ]; then
    JAVA="/usr/lib/jvm/java-1.8-openjdk/bin/java"
fi
JAVA_VER=$($JAVA -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{sub("^$", "0", $2); print $1$2}')

# absolute paths to this starter
# this works even if runned as `bash starter.sh`
if command -v realpath >/dev/null 2>&1; then
  SELF=$(realpath "$0")
else
  SELF="$0"
fi
SELF_DIR=$(dirname "$SELF")

# path to kem
if [[ "$SELF_DIR" =~ ^/(bin|sbin|usr)(/|$) ]]; then
	# starter is in /usr/, "installation" expected
	KEM_DIR="/usr/share/kemulator"
else
	KEM_DIR="$SELF_DIR"
fi
KEM_JAR="$KEM_DIR/KEmulator.jar"

# Hotspot configuration
XMX="-Xmx512M"
ARGS=(
  "$XMX"
  "-Djna.nosys=true"
  "-Djava.library.path=$KEM_DIR"
  "-Dfile.encoding=UTF-8"
  "-javaagent:$KEMULATOR_JAR"
  "-XX:+IgnoreUnrecognizedVMOptions"
)
if [[ "$OSTYPE" == "darwin"* ]]
then
	 ARGS+=(
    "-XstartOnFirstThread"
  )
fi
if [ "$JAVA_VER" -ge 90 ]
then
  ARGS+=(
    "--add-opens" "java.base/java.lang=ALL-UNNAMED"
    "--add-opens" "java.base/java.lang.reflect=ALL-UNNAMED"
    "--add-opens" "java.base/java.lang.ref=ALL-UNNAMED"
    "--add-opens" "java.base/java.io=ALL-UNNAMED"
    "--add-opens" "java.base/java.util=ALL-UNNAMED"
    "--enable-native-access=ALL-UNNAMED"
  )
fi

if [ -z "$1" ]; then
    # run without arguments
	cd "$KEM_DIR" || exit
	"$JAVA" "${ARGS[@]}" -jar "$KEM_JAR" -s
	exit $?
elif [[ "$1" == "-new-project" || "$1" == "-restore" || "$1" == "-convert" || "$1" == "-edit" || "$1" == "-build" || "$1" == "-publish" ]]; then
    # utils for IntelliJ IDEA
	CALL_PWD="$PWD"
	cd "$KEM_DIR" || exit
	"$JAVA" "${ARGS[@]}" -jar "$KEM_JAR" "$1" "$CALL_PWD" -s
	exit $?
elif [[ "$1" == -* ]]; then
	# direct configuration (jad, classpath, etc)
	echo "When using direct emulator configuration, please pass absolute paths!"
	#TODO invent something to resolve jads/cp paths
	cd "$KEM_DIR" || exit
	"$JAVA" "${ARGS[@]}" -jar "$KEM_JAR" -s "$@"
	exit $?
else
    # JAR file for launch
	USER_JAR=$(realpath "$1")
	cd "$KEM_DIR" || exit
	shift
	"$JAVA" "${ARGS[@]}" -jar "$KEM_JAR" -jar "$USER_JAR" -s "$@"
	exit $?
fi
