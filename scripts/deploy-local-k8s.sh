#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 || ! $1 =~ ^[A-Za-z0-9_][A-Za-z0-9_.-]{0,127}$ ]]; then
  printf 'Usage: bash scripts/deploy-local-k8s.sh <local-image-tag>\n' >&2
  exit 2
fi

for command_name in kubectl docker curl; do
  if ! command -v "$command_name" >/dev/null 2>&1; then
    printf '%s is required for local Kubernetes deployment.\n' "$command_name" >&2
    exit 1
  fi
done

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
secret_file="${APP_SECRETS_FILE:-${repo_root}/application-k8s.env}"
deployment_file="${repo_root}/k8s/deployment.yaml"
image="folio-pro-api:$1"
port_forward_pid=""
port_forward_log=""

cleanup() {
  if [[ -n "$port_forward_pid" ]]; then
    kill "$port_forward_pid" 2>/dev/null || true
    wait "$port_forward_pid" 2>/dev/null || true
  fi
  if [[ -n "$port_forward_log" ]]; then
    rm -f "$port_forward_log"
  fi
}
trap cleanup EXIT

if [[ ! -s "$secret_file" ]]; then
  printf 'Missing or empty %s. Add database connection values as KEY=value entries.\n' "$secret_file" >&2
  exit 1
fi

current_context="$(kubectl config current-context)"
if [[ "$current_context" != "docker-desktop" ]]; then
  printf 'Refusing deployment: current kubectl context is %s, expected docker-desktop.\n' "$current_context" >&2
  exit 1
fi

if ! docker image inspect "$image" >/dev/null 2>&1; then
  printf 'Local image %s was not found. Build it using the README instructions first.\n' "$image" >&2
  exit 1
fi

kubectl apply -f "${repo_root}/k8s/00-namespace.yaml"
kubectl create secret generic folio-pro-secrets \
  --namespace dev \
  --from-env-file="$secret_file" \
  --dry-run=client \
  --output yaml | kubectl apply -f -

kubectl apply \
  -f "${repo_root}/k8s/service.yaml" \
  -f "${repo_root}/k8s/ingress.yaml"

sed "s|folio-pro-api:REPLACE_WITH_LOCAL_TAG|${image}|" "$deployment_file" \
  | kubectl apply -f -

kubectl rollout restart --namespace dev deployment/folio-pro-api
kubectl rollout status --namespace dev deployment/folio-pro-api

port_forward_log="$(mktemp)"
kubectl port-forward --address 127.0.0.1 --namespace dev \
  service/folio-pro-api 18081:80 >"$port_forward_log" 2>&1 &
port_forward_pid=$!

for attempt in {1..30}; do
  if grep -Fq 'Forwarding from 127.0.0.1:18081' "$port_forward_log"; then
    break
  fi
  if ! kill -0 "$port_forward_pid" 2>/dev/null; then
    cat "$port_forward_log" >&2
    printf 'Unable to start Kubernetes port-forward for readiness check.\n' >&2
    exit 1
  fi
  sleep 1
done

if ! grep -Fq 'Forwarding from 127.0.0.1:18081' "$port_forward_log"; then
  cat "$port_forward_log" >&2
  printf 'Timed out waiting for Kubernetes port-forward.\n' >&2
  exit 1
fi

for attempt in {1..30}; do
  if curl --connect-timeout 2 --max-time 5 --fail --silent --output /dev/null \
    http://127.0.0.1:18081/actuator/health/readiness; then
    printf 'Kubernetes readiness smoke test passed.\n'
    exit 0
  fi
  if ! kill -0 "$port_forward_pid" 2>/dev/null; then
    cat "$port_forward_log" >&2
    printf 'Kubernetes port-forward stopped before readiness succeeded.\n' >&2
    exit 1
  fi
  sleep 2
done

printf 'Kubernetes readiness smoke test failed.\n' >&2
exit 1
