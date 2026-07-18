#!/usr/bin/env bash
set -euo pipefail

BASE_URL="http://localhost:8080/api/v1"

echo "==> GET /games"
curl -i "$BASE_URL/games"
#curl -i "http://localhost:8080/api/v1/games"

echo "\n==> GET /games/1"
curl -i "$BASE_URL/games/1"

echo "\n==> POST /games"
curl -i -X POST "$BASE_URL/games" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Terminal Test Game",
    "description": "Created from bash",
    "developmentYear": 2026,
    "score": 8.7
  }'

echo "\n==> PUT /games/1"
curl -i -X PUT "$BASE_URL/games/1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Terminal Game",
    "description": "Updated from bash",
    "developmentYear": 2026,
    "score": 9.2
  }'

# echo "\n==> DELETE /games/1"
# curl -i -X DELETE "$BASE_URL/games/1"
