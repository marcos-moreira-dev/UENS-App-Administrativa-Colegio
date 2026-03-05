#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
PING_URL="$BASE_URL/api/v1/system/ping"

echo "Smoke test: $PING_URL"

if command -v curl >/dev/null 2>&1; then
  curl --fail --silent --show-error "$PING_URL" >/dev/null
  echo "OK"
else
  echo "curl no esta instalado."
  exit 1
fi
