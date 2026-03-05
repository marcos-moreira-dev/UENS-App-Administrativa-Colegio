#requires -Version 5.1
$ErrorActionPreference = "Stop"

docker compose up -d db
docker compose ps
