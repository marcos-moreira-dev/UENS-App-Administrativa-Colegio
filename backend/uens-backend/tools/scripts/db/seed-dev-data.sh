#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(dirname "$0")/../../.."
SEED_FILE="$ROOT_DIR/tools/db/seed-dev-data.sql"

if [ ! -f "$SEED_FILE" ]; then
  echo "No existe $SEED_FILE"
  echo "Crea ese archivo SQL para cargar datos de desarrollo."
  exit 1
fi

DB_CONTAINER="${DB_CONTAINER:-uens_db}"
DB_NAME="${DB_NAME:-SchoolManagerNinitosSonadores}"
DB_USER="${DB_USER:-postgres}"

docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" < "$SEED_FILE"
echo "Seed aplicado desde $SEED_FILE"
