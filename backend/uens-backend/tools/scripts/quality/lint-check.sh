#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/../../.."

# Sin linter dedicado aun: se usa compilacion de tests como baseline.
./mvnw -DskipTests test-compile

echo "lint-check OK"
