#!/usr/bin/env bash
# Creates the orderpulse DB credentials directly in the cluster using
# `kubectl create secret`, so the password is never written into a
# tracked YAML file. Run this once before applying the rest of the manifests.
#
# Usage:
#   DB_USER=orderpulse_user DB_PASSWORD='S3cure!Pass' ./scripts/create-secrets.sh

set -euo pipefail

NAMESPACE="orderpulse"
DB_USER="${DB_USER:-orderpulse_user}"
DB_PASSWORD="${DB_PASSWORD:?Set DB_PASSWORD env var before running this script}"

kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic orderpulse-db-secret \
  --namespace "$NAMESPACE" \
  --from-literal=POSTGRES_USER="$DB_USER" \
  --from-literal=POSTGRES_PASSWORD="$DB_PASSWORD" \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic orderpulse-api-secret \
  --namespace "$NAMESPACE" \
  --from-literal=DB_USERNAME="$DB_USER" \
  --from-literal=DB_PASSWORD="$DB_PASSWORD" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "Secrets created/updated in namespace '$NAMESPACE'."
