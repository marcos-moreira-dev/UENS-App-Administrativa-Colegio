#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/../../.."

echo "Recreando base local de desarrollo..."
docker compose down -v
docker compose up -d db
docker compose ps
