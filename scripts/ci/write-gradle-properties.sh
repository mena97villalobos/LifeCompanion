#!/usr/bin/env bash
set -euo pipefail

gradleUserHome="${GRADLE_USER_HOME:-$HOME/.gradle}"
mkdir -p "$gradleUserHome"

propsFile="$gradleUserHome/gradle.properties"
touch "$propsFile"

append_prop() {
  local key="$1"
  local value="$2"
  # Ensure we don't duplicate lines across steps
  if grep -q "^${key}=" "$propsFile"; then
    sed -i.bak "/^${key}=.*/d" "$propsFile" && rm -f "${propsFile}.bak"
  fi
  printf "%s=%s\n" "$key" "$value" >>"$propsFile"
}

require_env() {
  local name="$1"
  if [[ -z "${!name:-}" ]]; then
    echo "Missing required env var: $name" >&2
    exit 1
  fi
}

# Remote service config required by :data:remote (BuildKonfig)
require_env "EXCHANGE_BASE_URL"
require_env "EXCHANGE_API_KEY"
require_env "MINIO_ENDPOINT"
require_env "MINIO_ENDPOINT_ACCESS_KEY"
require_env "MINIO_ENDPOINT_SECRET_KEY"
require_env "MINIO_BUCKET_NAME"

append_prop "exchangeBaseUrl" "${EXCHANGE_BASE_URL}"
append_prop "exchangeApiKey" "${EXCHANGE_API_KEY}"
append_prop "minioEndpoint" "${MINIO_ENDPOINT}"
append_prop "minioEndpointAccessKey" "${MINIO_ENDPOINT_ACCESS_KEY}"
append_prop "minioEndpointSecretKey" "${MINIO_ENDPOINT_SECRET_KEY}"
append_prop "minioBucketName" "${MINIO_BUCKET_NAME}"

# Optional build behavior toggles
if [[ -n "${REMOTE_HTTP_LOGGING:-}" ]]; then
  append_prop "remote.http.logging" "${REMOTE_HTTP_LOGGING}"
fi

echo "Wrote Gradle properties to: $propsFile"
