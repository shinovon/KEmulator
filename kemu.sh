#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JSON_MODE=0
COMMAND_JSON="null"

json_escape() {
	local value="$1"
	value="${value//\\/\\\\}"
	value="${value//\"/\\\"}"
	value="${value//$'\n'/\\n}"
	value="${value//$'\r'/\\r}"
	value="${value//$'\t'/\\t}"
	printf '%s' "$value"
}

for arg in "$@"; do
	if [[ "$arg" == "--json" ]]; then
		JSON_MODE=1
	elif [[ "$COMMAND_JSON" == "null" && "$arg" != --* ]]; then
		COMMAND_JSON="\"$(json_escape "$arg")\""
	fi
done

emit_bootstrap_error() {
	local code="$1"
	local message="$2"

	if [[ "$JSON_MODE" -eq 1 ]]; then
		printf '{"ok":false,"command":%s,"error":{"code":"%s","message":"%s"}}\n' \
			"$COMMAND_JSON" \
			"$(json_escape "$code")" \
			"$(json_escape "$message")"
	else
		echo "$message" >&2
	fi

	exit 4
}

if [[ -x "$ROOT_DIR/jre/bin/java" ]]; then
	JAVA_BIN="$ROOT_DIR/jre/bin/java"
else
	JAVA_BIN="$(command -v java || true)"
fi

if [[ -z "${JAVA_BIN:-}" ]]; then
	emit_bootstrap_error "JAVA_NOT_FOUND" "Could not find java."
fi

if [[ -f "$ROOT_DIR/KEmulator.jar" ]]; then
	RUNTIME_ROOT="$ROOT_DIR"
elif [[ -f "$ROOT_DIR/dist/release-linux/KEmulator.jar" ]]; then
	RUNTIME_ROOT="$ROOT_DIR/dist/release-linux"
else
	emit_bootstrap_error "NO_RUNTIME" "Could not find KEmulator.jar. Run ./build-release.sh first."
fi

CLASSPATH="$RUNTIME_ROOT/KEmulator.jar:$RUNTIME_ROOT/lib/*"

exec "$JAVA_BIN" \
	-Dkemu.root="$ROOT_DIR" \
	-Dkemu.runtime.root="$RUNTIME_ROOT" \
	-Dkemu.bootstrap.java="$JAVA_BIN" \
	-Dkemu.bootstrap.runtime="release" \
	-Dkemu.bootstrap.availableRuntimes="release" \
	-Dkemu.bootstrap.classpath="$CLASSPATH" \
	-cp "$CLASSPATH" \
	emulator.cli.KemuMain "$@"
