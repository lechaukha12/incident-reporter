# Incident Reporter - Phiên Bản Đã Sửa Lỗi

Ứng dụng báo cáo sự cố cho tổ chức. Phiên bản này đã sửa lỗi không nhất quán về giá trị enum SeverityLevel giữa frontend và backend.

## Thay Đổi Chính

1. **Tiêu Chuẩn Hóa Các Giá Trị Mức Độ Nghiêm Trọng**
   - Loại bỏ hoàn toàn các giá trị SEV1/SEV2/SEV3/SEV4 trong mã frontend
   - Sử dụng nhất quán giá trị CRITICAL/HIGH/MEDIUM/LOW từ enum của backend
   - Loại bỏ logic chuyển đổi giữa các định dạng khác nhau

2. **Cải Thiện Cấu Hình Nginx**
   - Sửa lỗi tên dịch vụ backend trong cấu hình Nginx
   - Đảm bảo định tuyến API chính xác từ frontend đến backend

3. **Build Multi-platform Images**
   - Xây dựng các image Docker đa nền tảng (linux/amd64, linux/arm64)
   - Cập nhật các tệp triển khai Kubernetes để sử dụng các image mới nhất

## Triển Khai

### Sử Dụng Docker Compose (Phát Triển Cục Bộ)

```bash
# Clone repository
git clone https://github.com/yourorg/incident-reporter.git
cd incident-reporter

# Start containers
docker-compose up -d
```

### Sử Dụng Kubernetes

```bash
# Clone repository
git clone https://github.com/yourorg/incident-reporter.git
cd incident-reporter/k8s

# Triển khai ứng dụng
./deploy.sh
```

## Cấu Trúc Dự Án

- `backend/` - API Spring Boot Java
- `frontend/` - Ứng dụng giao diện người dùng HTML/JavaScript 
- `k8s/` - File triển khai Kubernetes
- `docker-compose.yml` - Cấu hình để chạy môi trường phát triển cục bộ

## Tài Liệu API

### Sự Cố

| Endpoint | Phương Thức | Mô Tả |
|----------|--------|-------------|
| `/api/incidents` | GET | Lấy danh sách tất cả sự cố |
| `/api/incidents/{id}` | GET | Lấy chi tiết một sự cố |
| `/api/incidents` | POST | Tạo sự cố mới |
| `/api/incidents/{id}` | PUT | Cập nhật một sự cố |
| `/api/incidents/{id}` | DELETE | Xóa một sự cố |

### Mô Hình Dữ Liệu

**Incident (Sự cố)**

```json
{
  "id": "uuid-string",
  "title": "Tiêu đề sự cố",
  "description": "Mô tả chi tiết về sự cố",
  "status": "OPEN | IN_PROGRESS | RESOLVED | CLOSED",
  "severityLevel": "CRITICAL | HIGH | MEDIUM | LOW", 
  "affectedService": "Tên dịch vụ bị ảnh hưởng",
  "reportedBy": "Người báo cáo",
  "assignedTo": "Người được giao xử lý",
  "reportedAt": "2023-08-01T10:30:00Z",
  "updates": [
    {
      "id": "uuid-string",
      "content": "Nội dung cập nhật",
      "timestamp": "2023-08-01T14:25:00Z",
      "updatedBy": "Người cập nhật"
    }
  ]
}
```

## Công Nghệ Sử Dụng

- **Backend**: Java 11, Spring Boot
- **Frontend**: HTML, JavaScript, CSS
- **Cơ sở dữ liệu**: PostgreSQL
- **Triển khai**: Docker, Kubernetes, Nginx

## Người Đóng Góp

- Team Vietnamese IT Operations
