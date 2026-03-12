#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$ROOT_DIR"

echo "Scanning tracked files for obvious secret patterns..."

if ! command -v rg >/dev/null 2>&1; then
  echo "ripgrep (rg) is required for check-secrets.sh" >&2
  exit 1
fi

PATTERN='([A-Za-z0-9_]*(secret|token|password|passwd|api[_-]?key|private[_-]?key)[A-Za-z0-9_]*[[:space:]]*[:=][[:space:]]*["'"'"'"][^"'"'"''"'"'[:space:]]{8,}["'"'"'"]|BEGIN [A-Z ]*PRIVATE KEY|authorization:[[:space:]]*bearer[[:space:]]+[A-Za-z0-9._-]{10,}|jdbc:[^[:space:]]+://[^[:space:]]+:[^[:space:]]+@)'

if git grep -nI -E "$PATTERN" -- \
  . \
  ':(exclude)README.md' \
  ':(exclude).env.example' \
  ':(exclude).env.*.example' \
  ':(exclude)**/.env.example' \
  ':(exclude)**/.env.*.example' \
  ':(exclude)basic/mvnw' \
  ':(exclude)basic/mvnw.cmd' \
  ':(exclude).gitignore' \
  ':(exclude)**/.gitignore' \
  ':(exclude)check-secrets.sh' \
  ':(exclude)spring/mvnw' \
  ':(exclude)spring/mvnw.cmd' \
  ':(exclude)**/src/test/**'; then
  echo
  echo "Potentially sensitive content found. Review before pushing."
  exit 1
fi

echo "No obvious secret patterns found in tracked files."
