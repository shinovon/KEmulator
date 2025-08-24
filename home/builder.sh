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

CALL_PWD="$PWD"
cd "$KEM_DIR" || exit
"$JAVA" "-Xmx512M" "-Dfile.encoding=UTF-8" "-Djava.library.path=$KEM_DIR" -jar "$KEM_DIR/builder.jar" "$CALL_PWD" "$@"
exit $?
