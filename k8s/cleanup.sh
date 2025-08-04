#!/bin/bash

# Cleanup Incident Reporter from Kubernetes

echo "🧹 Cleaning up Incident Reporter from Kubernetes..."

# Delete deployments and services
echo "🗑️ Deleting deployments and services..."
kubectl delete -f k8s/frontend-deployment.yaml --ignore-not-found
kubectl delete -f k8s/backend-deployment.yaml --ignore-not-found
kubectl delete -f k8s/postgres-deployment.yaml --ignore-not-found

# Delete PVC and secrets
echo "🗑️ Deleting storage and secrets..."
kubectl delete -f k8s/postgres-pvc.yaml --ignore-not-found
kubectl delete -f k8s/postgres-secret.yaml --ignore-not-found

# Delete namespace (this will delete everything in the namespace)
echo "🗑️ Deleting namespace..."
kubectl delete -f k8s/namespace.yaml --ignore-not-found

echo "✅ Cleanup completed!"
