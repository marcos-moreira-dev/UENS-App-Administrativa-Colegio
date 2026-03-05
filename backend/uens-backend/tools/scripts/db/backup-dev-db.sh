#!/usr/bin/env bash
set -euo pipefail

DB_CONTAINER="${DB_CONTAINER:-uens_db}"
DB_NAME="${DB_NAME:-SchoolManagerNinitosSonadores}"
DB_USER="${DB_USER:-postgres}"
OUT_FILE="${1:-backup_${DB_NAME}_$(date +%Y%m%d_%H%M%S).sql}"

docker exec "$DB_CONTAINER" pg_dump -U "$DB_USER" -d "$DB_NAME" > "$OUT_FILE"
echo "Backup generado: $OUT_FILE"
