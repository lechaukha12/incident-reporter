#!/bin/bash

# Script để triển khai hoặc cập nhật các image Incident Reporter trên Kubernetes
# Cách sử dụng: ./update-k8s-images.sh [namespace]

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Mặc định namespace là incident-system nếu không có đối số được cung cấp
NAMESPACE=${1:-incident-system}

echo -e "${YELLOW}Cập nhật triển khai Incident Reporter trong namespace ${NAMESPACE}...${NC}"

# Kiểm tra xem namespace có tồn tại không
kubectl get namespace ${NAMESPACE} &> /dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}Namespace ${NAMESPACE} không tồn tại. Tạo namespace...${NC}"
    kubectl apply -f k8s/namespace.yaml
    if [ $? -ne 0 ]; then
        echo -e "${RED}Không thể tạo namespace. Thoát.${NC}"
        exit 1
    fi
fi

# Cập nhật hoặc tạo các triển khai
echo -e "${YELLOW}Áp dụng cấu hình PostgreSQL...${NC}"
kubectl apply -f k8s/postgres-secret.yaml -n ${NAMESPACE}
kubectl apply -f k8s/postgres-pvc.yaml -n ${NAMESPACE}
kubectl apply -f k8s/postgres-deployment.yaml -n ${NAMESPACE}

echo -e "${YELLOW}Áp dụng cấu hình Backend...${NC}"
kubectl apply -f k8s/backend-deployment.yaml -n ${NAMESPACE}

echo -e "${YELLOW}Áp dụng cấu hình Frontend...${NC}"
kubectl apply -f k8s/frontend-deployment.yaml -n ${NAMESPACE}

echo -e "${YELLOW}Áp dụng cấu hình Ingress...${NC}"
kubectl apply -f k8s/ingress.yaml -n ${NAMESPACE}

# Kiểm tra trạng thái triển khai
echo -e "${YELLOW}Kiểm tra trạng thái các pod...${NC}"
kubectl get pods -n ${NAMESPACE}

echo -e "${YELLOW}Kiểm tra trạng thái các dịch vụ...${NC}"
kubectl get services -n ${NAMESPACE}

echo -e "${YELLOW}Kiểm tra trạng thái ingress...${NC}"
kubectl get ingress -n ${NAMESPACE}

echo -e "${GREEN}Triển khai hoàn tất. Vui lòng kiểm tra trạng thái các pod để đảm bảo tất cả đều đang chạy.${NC}"
echo -e "${GREEN}Để xem logs của backend: kubectl logs -f deployment/backend -n ${NAMESPACE}${NC}"
echo -e "${GREEN}Để xem logs của frontend: kubectl logs -f deployment/frontend -n ${NAMESPACE}${NC}"
