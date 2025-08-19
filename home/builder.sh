#!/bin/bash
# KEmulator nnmod x64 build system xdg/linux / macos starter script

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
KEM_JAR="$KEM_DIR/builder.jar"

# Hotspot configuration
ARGS=(
  "-Xmx512M"
  "-Djna.nosys=true"
  "-Djava.library.path=$KEM_DIR"
  "-Dfile.encoding=UTF-8"
)
if [ "$JAVA_VER" -ge 90 ]
then
  echo "JDK 1.8 or lower is required! Exiting."
  exit 1
fi

if [[ "$1" == "--help" ]]; then
  cd "$KEM_DIR" || exit
  "$JAVA" "${ARGS[@]}" -jar "$KEM_JAR" --help
  exit 0
fi

CALL_PWD="$PWD"
cd "$KEM_DIR" || exit
"$JAVA" "${ARGS[@]}" -jar "$KEM_JAR" "$CALL_PWD" "$@"
exit $?
