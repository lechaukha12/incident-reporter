#!/bin/bash
# Script test API cho Sentinel

echo "Bắt đầu test API Sentinel..."

# URL base của API
API_URL="http://localhost:8080/api/incidents"

# Tạo incident mới
echo -e "\n1. Tạo incident mới"
CREATE_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Lỗi thanh toán trên ứng dụng mobile banking",
    "description": "Người dùng báo cáo không thể thực hiện thanh toán qua ứng dụng mobile banking từ 14:30",
    "severityLevel": "SEV1",
    "affectedService": "Payment Service",
    "reportedBy": "Customer Support"
  }' \
  $API_URL)

# Trích xuất ID của incident vừa tạo
INCIDENT_ID=$(echo $CREATE_RESPONSE | grep -o '"id":[0-9]*' | cut -d ':' -f 2)
echo "Đã tạo incident với ID: $INCIDENT_ID"

# Lấy thông tin incident vừa tạo
echo -e "\n2. Lấy thông tin incident vừa tạo"
curl -s -X GET "$API_URL/$INCIDENT_ID" | json_pp

# Thêm cập nhật vào timeline
echo -e "\n3. Thêm cập nhật vào timeline"
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Đã xác định nguyên nhân: API Gateway timeout khi gọi đến Payment Service",
    "updateType": "ROOT_CAUSE",
    "createdBy": "Technical Team"
  }' \
  "$API_URL/$INCIDENT_ID/updates" | json_pp

# Cập nhật trạng thái sự cố
echo -e "\n4. Cập nhật trạng thái sự cố"
curl -s -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IDENTIFIED",
    "severityLevel": "SEV2"
  }' \
  "$API_URL/$INCIDENT_ID" | json_pp

# Thêm một cập nhật nữa
echo -e "\n5. Thêm cập nhật hành động khắc phục"
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Đã tăng timeout config cho API Gateway và restart service",
    "updateType": "ACTION_TAKEN",
    "createdBy": "DevOps Team"
  }' \
  "$API_URL/$INCIDENT_ID/updates" | json_pp

# Cập nhật trạng thái monitoring
echo -e "\n6. Cập nhật trạng thái theo dõi"
curl -s -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "status": "MONITORING"
  }' \
  "$API_URL/$INCIDENT_ID" | json_pp

# Thêm cập nhật giải quyết
echo -e "\n7. Thêm cập nhật giải quyết"
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hệ thống đã hoạt động bình thường trở lại sau khi cấu hình lại timeout",
    "updateType": "RESOLUTION",
    "createdBy": "DevOps Team"
  }' \
  "$API_URL/$INCIDENT_ID/updates" | json_pp

# Đánh dấu sự cố đã giải quyết
echo -e "\n8. Đánh dấu sự cố đã giải quyết"
curl -s -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "status": "RESOLVED",
    "resolved": true
  }' \
  "$API_URL/$INCIDENT_ID" | json_pp

# Lấy danh sách tất cả sự cố
echo -e "\n9. Lấy danh sách tất cả sự cố"
curl -s -X GET "$API_URL?size=5" | json_pp

# Lấy thống kê
echo -e "\n10. Lấy thống kê sự cố"
curl -s -X GET "$API_URL/statistics" | json_pp

# Test tạo sự cố từ webhook
echo -e "\n11. Test tạo sự cố từ webhook giám sát"
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "title": "[ALERT] CPU Usage > 90% on Payment Service",
    "description": "CPU usage has exceeded threshold of 90% for more than 5 minutes",
    "severity": "high",
    "service": "Payment Service",
    "reportedBy": "Prometheus"
  }' \
  "$API_URL/webhook" | json_pp

# Test filter
echo -e "\n12. Test filter theo trạng thái RESOLVED"
curl -s -X GET "$API_URL?status=RESOLVED" | json_pp

echo -e "\n13. Test filter theo mức độ nghiêm trọng SEV1"
curl -s -X GET "$API_URL?severityLevel=SEV1" | json_pp

echo -e "\n14. Test tìm kiếm theo từ khóa"
curl -s -X GET "$API_URL?search=payment" | json_pp

echo -e "\nHoàn thành test API!"
