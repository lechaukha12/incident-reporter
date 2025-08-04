# Kubernetes Deployment cho Incident Reporter

Các file manifest để deploy ứng dụng Incident Reporter lên Kubernetes cluster.

## 📋 Danh sách file

- `namespace.yaml` - Tạo namespace riêng cho ứng dụng
- `postgres-secret.yaml` - Secret chứa thông tin đăng nhập PostgreSQL
- `postgres-pvc.yaml` - Persistent Volume Claim cho PostgreSQL
- `postgres-deployment.yaml` - Deployment và Service cho PostgreSQL
- `backend-deployment.yaml` - Deployment và Service cho Backend (Spring Boot)
- `frontend-deployment.yaml` - Deployment và Service cho Frontend (Nginx)
- `deploy.sh` - Script tự động deploy toàn bộ ứng dụng
- `cleanup.sh` - Script xóa toàn bộ ứng dụng

## 🚀 Cách deploy

### Deploy tự động (khuyên dùng):
```bash
# Deploy toàn bộ ứng dụng
./k8s/deploy.sh
```

### Deploy thủ công:
```bash
# 1. Tạo namespace
kubectl apply -f k8s/namespace.yaml

# 2. Tạo secrets và storage
kubectl apply -f k8s/postgres-secret.yaml
kubectl apply -f k8s/postgres-pvc.yaml

# 3. Deploy PostgreSQL
kubectl apply -f k8s/postgres-deployment.yaml

# 4. Deploy Backend
kubectl apply -f k8s/backend-deployment.yaml

# 5. Deploy Frontend
kubectl apply -f k8s/frontend-deployment.yaml
```

## 🔍 Kiểm tra trạng thái

```bash
# Xem pods
kubectl get pods -n incident-reporter

# Xem services
kubectl get services -n incident-reporter

# Xem logs backend
kubectl logs -f deployment/backend -n incident-reporter

# Xem logs frontend
kubectl logs -f deployment/frontend -n incident-reporter
```

## 🌐 Truy cập ứng dụng

```bash
# Lấy thông tin service frontend
kubectl get service frontend-service -n incident-reporter

# Nếu dùng LoadBalancer, lấy external IP
kubectl get service frontend-service -n incident-reporter -o jsonpath='{.status.loadBalancer.ingress[0].ip}'

# Nếu dùng minikube
minikube service frontend-service -n incident-reporter
```

## 🧹 Xóa ứng dụng

```bash
# Xóa toàn bộ ứng dụng
./k8s/cleanup.sh
```

## ⚙️ Cấu hình

### Images sử dụng:
- **Frontend**: `lechaukha12/incident-reporter-frontend:latest`
- **Backend**: `lechaukha12/incident-reporter-backend:latest`
- **Database**: `postgres:14-alpine`

### Ports:
- **Frontend**: 80 (LoadBalancer)
- **Backend**: 8080 (ClusterIP)
- **PostgreSQL**: 5432 (ClusterIP)

### Credentials PostgreSQL:
- **User**: postgres
- **Password**: postgres
- **Database**: sentinel

## 📝 Lưu ý

1. Frontend service dùng type `LoadBalancer` - nếu cluster không hỗ trợ, có thể đổi thành `NodePort`
2. Backend có health check endpoint tại `/actuator/health`
3. PostgreSQL data được lưu trong PVC với dung lượng 1Gi
4. Tất cả resource được tạo trong namespace `incident-reporter`
