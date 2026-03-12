#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_PORT=8089
DEFAULT_JAVA_VERSION="${JAVA_VERSION:-17}"

load_env_file() {
  local env_file="$1"

  if [[ ! -f "$env_file" ]]; then
    return 0
  fi

  echo "Loading env from $env_file"
  set -a
  # shellcheck disable=SC1090
  source "$env_file"
  set +a
}

usage() {
  cat <<'EOF'
Usage:
  ./run.sh basic
  ./run.sh spring
  ./run.sh notification-service

Optional:
  JAVA_VERSION=17 ./run.sh spring
  PORT=8089 ./run.sh spring
EOF
}

extract_java_major() {
  local java_bin="$1"
  "$java_bin" -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F. '{if ($1 == 1) print $2; else print $1}'
}

use_java_version() {
  local desired="$1"
  local resolved_java_home=""

  if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/java" ]]; then
    if [[ "$(extract_java_major "${JAVA_HOME}/bin/java")" == "$desired" ]]; then
      resolved_java_home="$JAVA_HOME"
    fi
  fi

  if [[ -z "$resolved_java_home" ]] && command -v /usr/libexec/java_home >/dev/null 2>&1; then
    resolved_java_home="$(/usr/libexec/java_home -v "$desired" 2>/dev/null || true)"
  fi

  if [[ -z "$resolved_java_home" ]] && command -v java >/dev/null 2>&1; then
    if [[ "$(extract_java_major "$(command -v java)")" == "$desired" ]]; then
      resolved_java_home="$(cd "$(dirname "$(dirname "$(command -v java)")")" && pwd)"
    fi
  fi

  if [[ -z "$resolved_java_home" ]]; then
    echo "Could not find JDK $desired. Install it or run with JAVA_VERSION=<installed-version>." >&2
    exit 1
  fi

  export JAVA_HOME="$resolved_java_home"
  export PATH="$JAVA_HOME/bin:$PATH"
  echo "Using Java $desired from $JAVA_HOME"
}

is_port_free() {
  local port="$1"

  if command -v lsof >/dev/null 2>&1; then
    ! lsof -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
    return
  fi

  if command -v nc >/dev/null 2>&1; then
    ! nc -z localhost "$port" >/dev/null 2>&1
    return
  fi

  return 0
}

find_available_port() {
  local start_port="$1"
  local port="$start_port"
  local max_tries=50

  for _ in $(seq 1 "$max_tries"); do
    if is_port_free "$port"; then
      echo "$port"
      return 0
    fi
    port=$((port + 1))
  done

  echo "Could not find a free port starting from $start_port." >&2
  exit 1
}

open_browser() {
  local url="$1"

  if command -v open >/dev/null 2>&1; then
    open "$url" >/dev/null 2>&1 || true
  elif command -v xdg-open >/dev/null 2>&1; then
    xdg-open "$url" >/dev/null 2>&1 || true
  fi
}

wait_and_open() {
  local url="$1"
  local attempts=60

  for _ in $(seq 1 "$attempts"); do
    if command -v curl >/dev/null 2>&1 && curl -fsS "$url" >/dev/null 2>&1; then
      open_browser "$url"
      return 0
    fi
    sleep 1
  done

  echo "Spring app did not respond at $url within 60 seconds." >&2
  return 1
}

run_basic() {
  use_java_version "$DEFAULT_JAVA_VERSION"
  cd "$ROOT_DIR/basic"
  chmod +x ./mvnw
  ./mvnw -q -DskipTests compile
  java -cp target/classes com.example.javalabs.basic.LearningApp
}

run_spring() {
  local selected_port

  use_java_version "$DEFAULT_JAVA_VERSION"
  selected_port="$(find_available_port "${PORT:-$DEFAULT_PORT}")"
  export PORT="$selected_port"

  cd "$ROOT_DIR/spring"
  echo "Starting Spring on http://localhost:$PORT/"
  wait_and_open "http://localhost:$PORT/" &
  local helper_pid=$!

  cleanup() {
    kill "$helper_pid" >/dev/null 2>&1 || true
  }

  trap cleanup EXIT INT TERM
  ./mvnw spring-boot:run
}

run_notification_service() {
  local selected_port

  use_java_version "$DEFAULT_JAVA_VERSION"
  selected_port="$(find_available_port "${PORT:-8099}")"
  export PORT="$selected_port"

  cd "$ROOT_DIR/notification-service"
  echo "Starting notification-service on http://localhost:$PORT/"
  wait_and_open "http://localhost:$PORT/api/notifications/healthz" &
  local helper_pid=$!

  cleanup() {
    kill "$helper_pid" >/dev/null 2>&1 || true
  }

  trap cleanup EXIT INT TERM
  chmod +x ./mvnw
  ./mvnw spring-boot:run
}

main() {
  load_env_file "$ROOT_DIR/.env"
  load_env_file "$ROOT_DIR/spring/.env"
  load_env_file "$ROOT_DIR/notification-service/.env"

  if [[ $# -ne 1 ]]; then
    usage
    exit 1
  fi

  case "$1" in
    basic)
      run_basic
      ;;
    spring)
      run_spring
      ;;
    notification-service)
      run_notification_service
      ;;
    *)
      usage
      exit 1
      ;;
  esac
}

main "$@"
