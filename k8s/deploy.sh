#!/bin/bash

# Deploy Incident Reporter to Kubernetes

echo "🚀 Deploying Incident Reporter to Kubernetes..."

# Apply namespace first
echo "📁 Creating namespace..."
kubectl apply -f k8s/namespace.yaml

# Apply secrets and PVC
echo "🔐 Creating secrets and storage..."
kubectl apply -f k8s/postgres-secret.yaml
kubectl apply -f k8s/postgres-pvc.yaml

# Deploy PostgreSQL
echo "🐘 Deploying PostgreSQL..."
kubectl apply -f k8s/postgres-deployment.yaml

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres -n incident-reporter --timeout=300s

# Deploy Backend
echo "☕ Deploying Backend..."
kubectl apply -f k8s/backend-deployment.yaml

# Wait for Backend to be ready
echo "⏳ Waiting for Backend to be ready..."
kubectl wait --for=condition=ready pod -l app=backend -n incident-reporter --timeout=300s

# Deploy Frontend
echo "🌐 Deploying Frontend..."
kubectl apply -f k8s/frontend-deployment.yaml

# Wait for Frontend to be ready
echo "⏳ Waiting for Frontend to be ready..."
kubectl wait --for=condition=ready pod -l app=frontend -n incident-reporter --timeout=300s

echo "✅ Deployment completed!"
echo ""
echo "📊 Check status:"
echo "kubectl get pods -n incident-reporter"
echo "kubectl get services -n incident-reporter"
echo ""
echo "🌐 Access the application:"
echo "kubectl get service frontend-service -n incident-reporter"
echo ""
echo "🔍 View logs:"
echo "kubectl logs -f deployment/backend -n incident-reporter"
echo "kubectl logs -f deployment/frontend -n incident-reporter"
