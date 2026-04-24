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

detect_linux_swt_arch() {
	case "$(uname -m)" in
		x86_64|amd64)
			printf '%s\n' "x86_64"
			;;
		i386|i486|i586|i686)
			printf '%s\n' "x86"
			;;
		aarch64|arm64)
			printf '%s\n' "aarch64"
			;;
		armv6l|armv7l|armv8l|armhf)
			printf '%s\n' "armhf"
			;;
		*)
			return 1
			;;
	esac
}

detect_linux_lwjgl3_swt_jar() {
	case "$(uname -m)" in
		x86_64|amd64)
			printf '%s\n' "lwjgl3-swt-linux.jar"
			;;
		i386|i486|i586|i686)
			printf '%s\n' "lwjgl3-swt-linux-x86.jar"
			;;
		aarch64|arm64)
			printf '%s\n' "lwjgl3-swt-linux-arm64.jar"
			;;
		armv6l|armv7l|armv8l|armhf)
			printf '%s\n' "lwjgl3-swt-linux-arm32.jar"
			;;
		*)
			return 1
			;;
	esac
}

build_classpath() {
	local classpath="$RUNTIME_ROOT/KEmulator.jar"
	local swt_arch
	local swt_jar
	local lwjgl3_swt_jar
	local jar_file

	if [[ "$(uname -s)" == "Linux" ]]; then
		if ! swt_arch="$(detect_linux_swt_arch)"; then
			emit_bootstrap_error "MISSING_SWT" "Unsupported Linux architecture for SWT: $(uname -m)."
		fi

		swt_jar="$RUNTIME_ROOT/lib/swt-gtk-linux-$swt_arch.jar"
		if [[ ! -f "$swt_jar" ]]; then
			emit_bootstrap_error "MISSING_SWT" "Could not find SWT runtime jar: $swt_jar."
		fi

		classpath="$classpath:$swt_jar"

		if ! lwjgl3_swt_jar="$(detect_linux_lwjgl3_swt_jar)"; then
			emit_bootstrap_error "MISSING_SWT" "Unsupported Linux architecture for LWJGL SWT: $(uname -m)."
		fi

		lwjgl3_swt_jar="$RUNTIME_ROOT/lib/$lwjgl3_swt_jar"
		if [[ ! -f "$lwjgl3_swt_jar" ]]; then
			emit_bootstrap_error "MISSING_SWT" "Could not find LWJGL SWT runtime jar: $lwjgl3_swt_jar."
		fi

		classpath="$classpath:$lwjgl3_swt_jar"
	fi

	for jar_file in "$RUNTIME_ROOT"/lib/*.jar; do
		[[ -e "$jar_file" ]] || continue
		case "$(basename "$jar_file")" in
			swt-gtk-linux-*.jar|lwjgl3-swt-linux*.jar)
				continue
				;;
		esac

		classpath="$classpath:$jar_file"
	done

	printf '%s\n' "$classpath"
}

CLASSPATH="$(build_classpath)"

exec "$JAVA_BIN" \
	-Dkemu.root="$ROOT_DIR" \
	-Dkemu.runtime.root="$RUNTIME_ROOT" \
	-Dkemu.bootstrap.java="$JAVA_BIN" \
	-Dkemu.bootstrap.runtime="release" \
	-Dkemu.bootstrap.availableRuntimes="release" \
	-Dkemu.bootstrap.classpath="$CLASSPATH" \
	-cp "$CLASSPATH" \
	emulator.cli.KemuMain "$@"
