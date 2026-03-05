#!/usr/bin/env bash
set -euo pipefail

# Asegurate de tener .env
if [ ! -f .env ]; then
  echo "Falta .env (copia .env.example a .env)"
  exit 1
fi

# Export .env variables so Spring Boot can read them.
set -a
. ./.env
set +a

# Corre con perfil dev
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.profiles=dev
