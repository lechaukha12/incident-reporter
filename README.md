# Hệ thống Tuning OPS - Incident Reporter

Một ứng dụng web nội bộ giúp ghi nhận, theo dõi và quản lý các sự cố kỹ thuật (incidents) một cách tập trung, có hệ thống và tự động hóa, được xây dựng trên nền tảng Java Spring Boot và HTML/JS với Nginx hoặc Angular.

## 🎯 Vấn đề & Mục tiêu

### Vấn đề
Tại môi trường microservices phức tạp của ứng dụng mobile banking, việc xử lý sự cố đang gặp các thách thức:

- **Phản ứng chậm**: Sự cố chỉ được phát hiện khi có người dùng phàn nàn hoặc DevOps phải truy vấn log/trace thủ công
- **Quy trình rời rạc**: Thông tin về sự cố bị phân tán qua nhiều kênh chat, không có một nơi ghi nhận tập trung
- **Mất thời gian**: Đội ngũ tốn nhiều thời gian để tìm hiểu "chuyện gì đã xảy ra" thay vì tập trung vào việc khắc phục
- **Khó rút kinh nghiệm**: Không có dữ liệu lịch sử để phân tích, tìm ra nguyên nhân gốc rễ và ngăn ngừa các sự cố tương tự

### Mục tiêu
- Tập trung hóa toàn bộ thông tin về sự cố vào một nơi duy nhất
- Tự động hóa việc tạo sự cố ngay khi hệ thống giám sát phát hiện dấu hiệu bất thường
- Chuẩn hóa quy trình xử lý sự cố từ lúc phát hiện đến khi giải quyết xong
- Cung cấp dữ liệu để phân tích và cải tiến, giảm thiểu thời gian khắc phục (MTTR)

## ✨ Các tính năng cốt lõi

- **Quản lý Incident Toàn diện**: Tạo, cập nhật, phân công và theo dõi incident theo vòng đời (OPEN → IN_PROGRESS → RESOLVED → CLOSED)
- **Phân loại theo Mức độ**: Gán mức độ nghiêm trọng (CRITICAL/HIGH/MEDIUM/LOW) để ưu tiên xử lý
- **Nhật ký hành động (Timeline)**: Ghi lại toàn bộ diễn biến, quyết định và hành động xử lý theo thời gian thực
- **Tự động tạo Incident**: Tích hợp với hệ thống cảnh báo (Grafana, Prometheus) qua Webhook
- **Thông báo tức thì**: Tích hợp với Slack/Teams để gửi thông báo về các cập nhật quan trọng
- **Giao diện trực quan**: Dashboard hiển thị danh sách incident, cho phép lọc và tìm kiếm dễ dàng

## � Vấn Đề Hiện Tại & Đề Xuất Khắc Phục

### ⚠️ Lỗi Nghiêm Trọng - Không Cập Nhật Được 4 Trường Dữ Liệu

**Mô tả lỗi:**
Khi cập nhật incident qua API PUT `/api/incidents/{id}`, có 4 trường cụ thể **KHÔNG** được cập nhật:
- `title` (Tiêu đề sự cố)
- `description` (Mô tả) 
- `affectedService` (Dịch vụ bị ảnh hưởng)
- `reportedBy` (Người báo cáo)

**Trạng thái:** ❌ Chưa giải quyết được

**Các trường CÓ THỂ cập nhật bình thường:**
- `status`, `severityLevel`, `assignee`, `notes`, `rootCause`

**Điều tra đã thực hiện:**
1. ✅ **Database Schema**: Đã validate, cấu trúc database chính xác
2. ✅ **Direct SQL**: Test trực tiếp UPDATE statement - hoạt động bình thường  
3. ✅ **JPA/Hibernate Logging**: Đã enable SQL logging và parameter binding
4. ✅ **Entity Manager**: Đã thử `clear()`, `flush()`, reload entity
5. ✅ **Native SQL Workaround**: Thử cả `EntityManager.createNativeQuery()` và `@Query` annotation
6. ✅ **Enhanced Debugging**: Thêm extensive logging và error handling

**Phát hiện quan trọng:**
- Backend nhận đúng request và DTO có đủ data
- Hibernate thực hiện UPDATE query với đầy đủ parameters  
- Database structure hoàn toàn chính xác
- Chỉ 4/8 fields bị ảnh hưởng - không phải ngẫu nhiên
- Có thể liên quan đến JPA entity mapping hoặc Spring configuration sâu bên trong

**Đề xuất khắc phục:**

#### Ưu tiên 1: Kiểm tra Entity Mapping
```bash
# Kiểm tra file Incident.java
backend/src/main/java/com/nganhang/sentinel/model/Incident.java
```
- Review các annotation `@Column`, `@Table`
- Tìm các constraint hoặc mapping đặc biệt cho 4 fields này
- Kiểm tra có getter/setter đặc biệt không

#### Ưu tiên 2: Rebuild Entity từ đầu
```java
// Tạo lại entity với mapping đơn giản nhất
@Entity
@Table(name = "incidents")
public class Incident {
    @Column(name = "title", length = 255)
    private String title;
    // ...
}
```

#### Ưu tiên 3: Workaround tạm thời
```java
// Implement separate update methods cho từng nhóm fields
public void updateBasicFields(Long id, String title, String description, String affectedService, String reportedBy) {
    // Use pure JDBC or separate native update
}
```

#### Ưu tiên 4: Configuration Review
```properties
# Kiểm tra application.properties
spring.jpa.hibernate.ddl-auto=
spring.jpa.database-platform=
logging.level.org.hibernate.SQL=DEBUG
```

### �📋 Thay Đổi Gần Đây

**Phiên bản hiện tại đã sửa các lỗi quan trọng:**

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

4. **Enhanced Debugging Infrastructure**
   - Thêm comprehensive logging cho JPA/Hibernate operations
   - Implement error handling và transaction debugging
   - Tạo workaround bằng native SQL queries

**Cách reproduce lỗi:**
```bash
# 1. Khởi động ứng dụng
docker-compose up -d

# 2. Kiểm tra incident hiện tại
curl "http://localhost:8080/api/incidents"

# 3. Thử cập nhật incident (VD: id=4)
curl -X PUT "http://localhost:8080/api/incidents/4" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "NEW TITLE TEST",
    "description": "New description test", 
    "affectedService": "New Service Test",
    "reportedBy": "New Reporter Test",
    "severityLevel": "HIGH",
    "assignee": "New Assignee"
  }'

# 4. Kiểm tra kết quả - title, description, affectedService, reportedBy sẽ KHÔNG thay đổi
# Chỉ có severityLevel và assignee được cập nhật
```

**Debug tools sẵn có:**
```bash
# Xem logs backend
docker logs sentinel-backend --tail 50

# Xem logs chi tiết JPA/Hibernate
docker logs sentinel-backend 2>&1 | grep "Hibernate:"

# Kiểm tra database trực tiếp  
docker exec -it sentinel-postgres psql -U incident_user -d incident_db
\d incidents;
SELECT * FROM incidents WHERE id = 4;
```

## 🛠️ Ngăn xếp Công nghệ (Tech Stack)

| Phần | Công nghệ | Lý do lựa chọn |
|------|-----------|----------------|
| **Frontend** | HTML/JS + Nginx hoặc Angular | Cung cấp cả phiên bản đơn giản (HTML/JS) và phiên bản đầy đủ (Angular) tùy theo nhu cầu triển khai |
| **Backend** | Java 11 + Spring Boot | Hệ sinh thái vững chắc, hiệu năng cao, bảo mật tốt, được tin dùng trong ngành tài chính |
| **Database** | PostgreSQL | Hệ CSDL quan hệ mạnh mẽ, ổn định, hỗ trợ tốt các kiểu dữ liệu phức tạp |
| **Triển khai** | Docker, Kubernetes, Nginx | Đóng gói toàn bộ ứng dụng, dễ dàng thiết lập môi trường và triển khai nhất quán |

## 🚀 Bắt đầu Nhanh (Getting Started)

### Yêu cầu hệ thống:
- Docker & Docker Compose
- Git (tùy chọn)
- Kubernetes cluster (cho triển khai production)

### Cài đặt và chạy với Docker Compose (Development)

```bash
# Clone repository
git clone https://github.com/lechaukha12/incident-reporter.git
cd incident-reporter

# Khởi chạy tất cả services
docker-compose up -d
```

**Truy cập ứng dụng:**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080
- API Test: http://localhost:4200/api-test.html

### Triển khai với Kubernetes (Production)

```bash
# Clone repository  
git clone https://github.com/lechaukha12/incident-reporter.git
cd incident-reporter/k8s

# Triển khai tự động
./deploy.sh

# Hoặc triển khai thủ công
kubectl apply -f namespace.yaml
kubectl apply -f postgres-secret.yaml
kubectl apply -f postgres-pvc.yaml
kubectl apply -f postgres-deployment.yaml
kubectl apply -f backend-deployment.yaml
kubectl apply -f frontend-deployment.yaml
```

**Kiểm tra trạng thái:**
```bash
# Xem pods
kubectl get pods -n incident-reporter

# Truy cập ứng dụng (nếu dùng minikube)
minikube service frontend-service -n incident-reporter
```

Chi tiết về Kubernetes deployment: [k8s/README.md](k8s/README.md)

## 🏗️ Cấu trúc Dự án

```
incident-reporter/
├── backend/                 # Spring Boot API
│   ├── src/main/java/com/nganhang/sentinel/
│   │   ├── config/         # Cấu hình CORS, Security
│   │   ├── controller/     # REST Controllers
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── model/         # JPA Entities
│   │   ├── repository/    # Data Access Layer
│   │   ├── service/       # Business Logic
│   │   └── SentinelApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/  # Flyway migrations
│   └── Dockerfile
├── frontend/               # HTML/JS + Nginx hoặc Angular
│   ├── src/
│   │   ├── app/          # Angular components (nếu dùng Angular)
│   │   ├── assets/       # Static assets
│   │   ├── environments/ # Cấu hình môi trường
│   │   └── *.html        # HTML pages
│   ├── nginx.conf        # Nginx configuration
│   └── Dockerfile.nginx  # Docker image cho production
├── k8s/                   # Kubernetes manifests
└── docker-compose.yml     # Development setup
```

## 📚 Tài Liệu API

### Endpoints chính

| Endpoint | Phương Thức | Mô Tả |
|----------|--------|-------------|
| `/api/incidents` | GET | Lấy danh sách tất cả sự cố (hỗ trợ filter, sort, phân trang) |
| `/api/incidents/{id}` | GET | Lấy chi tiết một sự cố và timeline |
| `/api/incidents` | POST | Tạo sự cố mới (có thể từ webhook) |
| `/api/incidents/{id}` | PUT | Cập nhật một sự cố |
| `/api/incidents/{id}` | DELETE | Xóa một sự cố |
| `/api/incidents/{id}/updates` | POST | Thêm cập nhật vào timeline |
| `/api/incidents/stats` | GET | Lấy thống kê tổng quan |

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
  "notes": "Ghi chú về nguyên nhân gốc",
  "rootCause": "Phân tích nguyên nhân gốc rễ",
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

## 🧪 Kiểm thử và Debug

### Công cụ kiểm thử có sẵn:
- **api-test.html**: Trang kiểm tra kết nối API đơn giản
- **api-test-standalone.html**: Công cụ kiểm tra API độc lập  
- **connection-test.html**: Kiểm tra kết nối giữa frontend và backend
- **debug-helper.html**: Công cụ debug và troubleshooting

### Chạy tests:
```bash
# Backend unit tests
cd backend
./mvnw test

# Test API endpoints
./test-api.sh
```

## 🔧 Cấu hình và Phát triển

### Cấu hình Frontend:

**Phiên bản HTML/JS với Nginx (mặc định):**
- Sử dụng `Dockerfile.nginx` để build
- Phục vụ trên port 4200  
- Đơn giản, nhẹ nhàng và dễ triển khai

**Phiên bản Angular (tùy chọn):**
- Sử dụng `Dockerfile` để build
- Yêu cầu Node.js để phát triển
- Cung cấp trải nghiệm người dùng phong phú hơn

### Cấu hình Backend:
- Cấu hình trong `backend/src/main/resources/application.properties`
- Spring Boot REST API chạy trên port 8080
- Sử dụng Flyway để quản lý database migrations

### Kết nối API:
API URL được tự động cấu hình dựa trên môi trường:
- Môi trường phát triển: `http://localhost:8080/api`
- Môi trường Docker: `http://backend:8080/api`

## 📝 Phát triển tính năng mới

### ⚠️ LƯU Ý QUAN TRỌNG CHO DEVELOPERS

**Trước khi phát triển tính năng mới, vui lòng:**
1. **Không sử dụng 4 fields có vấn đề** trong logic nghiệp vụ mới cho đến khi được fix
2. **Test kỹ lưỡng** các API update operations
3. **Backup database** trước khi thử nghiệm fix

**Fields an toàn để sử dụng:**
- ✅ `status`, `severityLevel`, `assignee` 
- ✅ `notes`, `rootCause`
- ✅ `createdAt`, `updatedAt`, `resolvedAt`
- ✅ `resolved` (boolean)

**Fields có vấn đề - tránh dựa vào:**
- ❌ `title`, `description`, `affectedService`, `reportedBy`

### Backend:
1. Thêm model/entity trong `model/`
2. Tạo repository trong `repository/`  
3. Triển khai logic nghiệp vụ trong `service/`
4. Cung cấp RESTful API trong `controller/`

### Frontend:
- **HTML/JS**: Chỉnh sửa các file HTML trong `frontend/src/`
- **Angular**: Phát triển components trong `frontend/src/app/`

### Database migrations:
Thêm file migration trong `backend/src/main/resources/db/migration/` với quy ước `V{n}__description.sql`

## 🤝 Đóng góp

Nếu bạn muốn đóng góp cho dự án, vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/amazing-feature`)
3. Commit thay đổi (`git commit -m 'Add some amazing feature'`)
4. Push lên branch (`git push origin feature/amazing-feature`)
5. Mở Pull Request

## 📄 License

Dự án này thuộc về **Team Vietnamese IT Operations**.

## 📞 Hỗ trợ

- **Repository**: https://github.com/lechaukha12/incident-reporter
- **Issues**: Báo cáo lỗi hoặc đề xuất tính năng tại GitHub Issues
- **Kubernetes Images**: 
  - Frontend: `lechaukha12/incident-reporter-frontend:latest`
  - Backend: `lechaukha12/incident-reporter-backend:latest`

---

🚀 **Happy Incident Managing!** 🚀
