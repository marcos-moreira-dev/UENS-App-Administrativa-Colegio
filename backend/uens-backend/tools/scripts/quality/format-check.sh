#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/../../.."

# Verifica whitespace y conflictos de merge accidentales.
git diff --check

echo "format-check OK"
