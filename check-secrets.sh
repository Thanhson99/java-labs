#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$ROOT_DIR"

echo "Scanning tracked files for obvious secret patterns..."

if ! command -v rg >/dev/null 2>&1; then
  echo "ripgrep (rg) is required for check-secrets.sh" >&2
  exit 1
fi

PATTERN='(api[_-]?key|secret|token|password|passwd|private[_-]?key|BEGIN [A-Z ]*PRIVATE KEY|aws_access_key_id|aws_secret_access_key|x-api-key|authorization:[[:space:]]*bearer|jdbc:[^[:space:]]+://)'

if git grep -nI -E "$PATTERN" -- \
  . \
  ':(exclude)README.md' \
  ':(exclude).gitignore' \
  ':(exclude)**/.gitignore' \
  ':(exclude)check-secrets.sh' \
  ':(exclude)spring/mvnw' \
  ':(exclude)spring/mvnw.cmd'; then
  echo
  echo "Potentially sensitive content found. Review before pushing."
  exit 1
fi

echo "No obvious secret patterns found in tracked files."
