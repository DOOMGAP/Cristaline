#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

cleanup() {
  docker compose down -v --remove-orphans >/dev/null 2>&1 || true
}

cd "${PROJECT_DIR}"
trap cleanup EXIT

export APP_SEED_ENABLED="${APP_SEED_ENABLED:-false}"
export POSTGRES_PORT="${POSTGRES_PORT:-55432}"
export PGADMIN_PORT="${PGADMIN_PORT:-18081}"
export KAFKA_PORT="${KAFKA_PORT:-29092}"
export KAFKA_EXTERNAL_PORT="${KAFKA_EXTERNAL_PORT:-39092}"
export KAFKA_CONSOLE_PORT="${KAFKA_CONSOLE_PORT:-18082}"

docker compose up --build -d

for _ in $(seq 1 60); do
  if curl -fsS "http://127.0.0.1:4200/games" >/dev/null && curl -fsS "http://127.0.0.1:8080/games" >/dev/null; then
    break
  fi
  sleep 2
done

curl -fsS "http://127.0.0.1:4200/games" >/dev/null
curl -fsS "http://127.0.0.1:8080/games" >/dev/null

cd "${PROJECT_DIR}/front"
npx playwright test
