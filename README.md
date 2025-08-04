# Hệ thống Tuning OPS

Một ứng dụng web nội bộ giúp ghi nhận, theo dõi và quản lý các sự cố kỹ thuật (incidents) một cách tập trung, có hệ thống và tự động hóa, được xây dựng trên nền tảng Java Spring Boot và HTML/JS với Nginx hoặc Angular.

## 🎯 Vấn đề & Mục tiêu

### Vấn đề

Hệ thống OPS của ngân hàng đang đối mặt với những thách thức trong việc quản lý và theo dõi sự cố:

- **Thiếu tập trung**: Thông tin sự cố phân tán qua nhiều nền tảng (chat, email, ticket) gây khó khăn trong việc theo dõi và điều phối.

- **Phát hiện chậm**: Nhiều sự cố chỉ được phát hiện sau khi đã ảnh hưởng đến khách hàng, khi người dùng báo cáo vấn đề qua các kênh hỗ trợ.

- **Khó theo dõi tiến độ**: Không có công cụ thống nhất để theo dõi trạng thái xử lý sự cố từ khi phát hiện đến khi giải quyết.

- **Báo cáo không đồng nhất**: Thiếu chuẩn hóa trong việc phân loại mức độ nghiêm trọng và ghi nhận diễn biến xử lý sự cố.

### Mục tiêu

Hệ thống Tuning OPS được xây dựng để cải thiện quy trình quản lý sự cố với các mục tiêu cụ thể:

- **Tạo nền tảng tập trung**: Xây dựng một nền tảng duy nhất để ghi nhận, quản lý và theo dõi toàn bộ sự cố kỹ thuật.

- **Chuẩn hóa quy trình**: Áp dụng quy trình xử lý sự cố chuẩn với 4 trạng thái rõ ràng (Đang điều tra → Đã xác định → Đang theo dõi → Đã giải quyết).

- **Phân loại hiệu quả**: Phân loại sự cố theo mức độ nghiêm trọng (Nghiêm trọng, Cao, Trung bình, Thấp) để ưu tiên nguồn lực xử lý hợp lý.

- **Lưu trữ lịch sử**: Ghi lại đầy đủ tiến trình xử lý, các quyết định và hành động thông qua hệ thống timeline.

- **Hỗ trợ ra quyết định**: Cung cấp thống kê và báo cáo để giúp đội ngũ kỹ thuật đánh giá hiệu quả xử lý sự cố và cải thiện quy trình.

## ✨ Các tính năng cốt lõi

- **Dashboard tổng quan**: Hiển thị thống kê tổng số sự cố, số sự cố đang hoạt động và đã giải quyết.

- **Quản lý sự cố toàn diện**: Cho phép tạo, cập nhật và theo dõi các sự cố với đầy đủ thông tin (tiêu đề, mô tả, trạng thái, mức độ nghiêm trọng, người phụ trách).

- **Phân loại sự cố**: Hỗ trợ phân loại theo mức độ nghiêm trọng với 4 cấp độ rõ ràng:
  - **CRITICAL** (Nghiêm trọng): Ảnh hưởng đến nhiều người dùng
  - **HIGH** (Cao): Vấn đề chức năng quan trọng
  - **MEDIUM** (Trung bình): Vấn đề chức năng nhỏ  
  - **LOW** (Thấp): Tác động tối thiểu

- **Quản lý trạng thái**: Theo dõi sự cố qua các trạng thái (Đang điều tra, Đã xác định, Đang theo dõi, Đã giải quyết).

- **Timeline cập nhật**: Ghi lại lịch sử các hành động và cập nhật cho mỗi sự cố theo thời gian.

- **Chỉnh sửa sự cố**: Giao diện modal hiện đại cho phép chỉnh sửa hoàn chỉnh thông tin sự cố, bao gồm cập nhật mức độ nghiêm trọng với dropdown menu trực quan.

- **Giao diện thân thiện**: Thiết kế trực quan, dễ sử dụng với cả phiên bản web đơn giản và phiên bản đầy đủ.

## 🛠️ Ngăn xếp Công nghệ (Tech Stack)

| Phần | Công nghệ | Lý do lựa chọn |
|------|-----------|----------------|
| Frontend | HTML/JS + Nginx hoặc Angular | Cung cấp cả phiên bản đơn giản (HTML/JS) và phiên bản đầy đủ (Angular) tùy theo nhu cầu triển khai |
| Backend | Java + Spring Boot | Hệ sinh thái vững chắc, hiệu năng cao, bảo mật tốt, được tin dùng trong ngành tài chính |
| Database | PostgreSQL | Hệ CSDL quan hệ mạnh mẽ, ổn định, hỗ trợ tốt các kiểu dữ liệu phức tạp |
| Triển khai | Docker & Docker Compose | Đóng gói toàn bộ ứng dụng, dễ dàng thiết lập môi trường và triển khai nhất quán |

## 🏗️ Cấu trúc Thư mục

```
incident-reporter/
├── .gitignore
├── docker-compose.yml
├── README.md
├── start.sh
├── api-test.html
├── backend/
│   ├── Dockerfile
│   ├── src/main/java/com/nganhang/sentinel/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/ 
│   │   ├── repository/
│   │   ├── service/
│   │   └── SentinelApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/
│   │       └── V1__create_incidents_table.sql
│   └── pom.xml
└── frontend/
    ├── src/
    │   ├── app/
    │   ├── assets/
    │   ├── environments/
    │   ├── fallback-index.html
    │   ├── api-test-standalone.html
    │   └── connection-test.html
    ├── angular.json
    ├── nginx.conf
    ├── Dockerfile
    ├── Dockerfile.nginx
    └── package.json
```

## 🔌 API Endpoints

- **POST /api/incidents**: Tạo một incident mới (có thể được gọi từ webhook).
- **GET /api/incidents**: Lấy danh sách incidents (hỗ trợ filter, sort, phân trang).
- **GET /api/incidents/{id}**: Lấy chi tiết một incident và timeline của nó.
- **PUT /api/incidents/{id}**: Cập nhật trạng thái, mức độ nghiêm trọng, người phụ trách.
- **POST /api/incidents/{id}/updates**: Thêm một ghi nhận mới vào timeline của incident.
- **GET /api/incidents/stats**: Lấy thống kê tổng quan về các sự cố.

## � Trạng thái Hệ thống

### ✅ Tính năng đã triển khai và hoạt động:

1. **Hệ thống phân loại mức độ nghiêm trọng mới**: 
   - Backend hoàn toàn sử dụng enum CRITICAL/HIGH/MEDIUM/LOW
   - Frontend hiển thị và gửi trực tiếp các giá trị enum mới
   - Loại bỏ hoàn toàn hệ thống SEV1-SEV4 cũ

2. **Chức năng chỉnh sửa sự cố**:
   - Modal edit với giao diện thân thiện
   - API PUT endpoint hoạt động hoàn hảo
   - Cập nhật được tất cả thông tin: title, description, status, severity, assignee

3. **API REST hoàn chỉnh**:
   - POST /api/incidents - Tạo incident mới ✅
   - GET /api/incidents - Lấy danh sách incidents ✅  
   - GET /api/incidents/{id} - Lấy chi tiết incident ✅
   - PUT /api/incidents/{id} - Cập nhật incident ✅
   - POST /api/incidents/{id}/updates - Thêm timeline update ✅

4. **Containerization hoàn chỉnh**:
   - Backend: Java Spring Boot container ✅
   - Frontend: Nginx container với HTML/JS ✅  
   - Database: PostgreSQL container ✅
   - Tất cả services đã rebuild và hoạt động với enum mới

### 🔧 Cần cải thiện trong tương lai:

- Giao diện Angular để thay thế HTML/JS
- Hệ thống thông báo real-time
- Dashboard analytics nâng cao
- Export báo cáo PDF/Excel

## �🚀 Bắt đầu Nhanh (Getting Started)

### Yêu cầu:

- Docker & Docker Compose
- Git (tùy chọn)

### Các bước cài đặt:

1. **Clone repository:**
   ```bash
   git clone <your-repository-url>
   cd incident-reporter
   ```

2. **Chạy ứng dụng với Docker Compose:**
   ```bash
   docker-compose up -d
   ```
   
   Lệnh này sẽ build các images và khởi chạy tất cả các services (frontend, backend, db).

3. **Truy cập ứng dụng:**
   - Frontend: http://localhost:4200 (Simple HTML/JS interface)
   - Backend API: http://localhost:8080
   - API Test: http://localhost:4200/api-test.html

## 🔧 Cấu hình và Triển khai

### Cấu hình Frontend:

Frontend có hai phiên bản triển khai:

1. **Phiên bản HTML/JS với Nginx (mặc định - đang sử dụng):**
   - Sử dụng `Dockerfile.nginx` để build
   - Phục vụ trên port 4200
   - Đơn giản, nhẹ nhàng và dễ triển khai
   - Giao diện fallback-index.html với modal edit hoàn chỉnh

2. **Phiên bản Angular (tùy chọn - chưa triển khai):**
   - Sử dụng `Dockerfile` để build
   - Yêu cầu Node.js để phát triển
   - Cung cấp trải nghiệm người dùng phong phú hơn

### Cấu hình Backend:

- Cấu hình được đặt trong `backend/src/main/resources/application.properties`
- Spring Boot REST API chạy trên port 8080
- Sử dụng Flyway để quản lý database migrations

### Kết nối API:

API URL được tự động cấu hình dựa trên môi trường:
- Môi trường phát triển (localhost): `http://localhost:8080/api`
- Môi trường Docker: `http://backend:8080/api`

## 🧪 Kiểm thử

Dự án bao gồm các công cụ kiểm thử:

- **api-test.html**: Trang kiểm tra kết nối API đơn giản
- **api-test-standalone.html**: Công cụ kiểm tra API độc lập
- **connection-test.html**: Kiểm tra kết nối giữa frontend và backend
- **test-api.sh**: Script tự động kiểm tra các API endpoint (tạo, đọc, cập nhật sự cố và timeline)
  ```bash
  # Chạy script kiểm tra API
  ./test-api.sh
  ```

### Kiểm tra chức năng mới:

1. **Test severity levels mới**:
   ```bash
   # Tạo incident với CRITICAL severity
   curl -X POST http://localhost:8080/api/incidents \
     -H "Content-Type: application/json" \
     -d '{"title":"Test Critical","severityLevel":"CRITICAL","status":"INVESTIGATING"}'
   ```

2. **Test chức năng edit**:
   ```bash
   # Cập nhật incident
   curl -X PUT http://localhost:8080/api/incidents/1 \
     -H "Content-Type: application/json" \
     -d '{"severityLevel":"HIGH","status":"RESOLVED","assignee":"Admin"}'
   ```

Trong thư mục `backend/src/test` có các bài kiểm thử tự động cho backend:
- Unit tests cho Service layer
- Integration tests cho Controller layer
- Tất cả tests đã được cập nhật để sử dụng severity levels mới

## 📝 Phát triển

### Tính năng mới đã triển khai:

1. **Thay đổi hệ thống phân loại mức độ nghiêm trọng**:
   - **Hoàn toàn cập nhật** từ mã phân loại cũ (SEV1, SEV2, SEV3, SEV4) sang hệ thống mô tả rõ ràng (CRITICAL, HIGH, MEDIUM, LOW).
   - Cập nhật toàn bộ backend Java (Incident.java, Service, Controller) và frontend.
   - Tất cả API endpoints hiện sử dụng trực tiếp các giá trị mới.
   - Migration script V2__update_severity_levels.sql để cập nhật dữ liệu hiện có.

2. **Tính năng chỉnh sửa sự cố hoàn chỉnh**:
   - Modal chỉnh sửa trong giao diện với dropdown severity levels mới.
   - Hỗ trợ chỉnh sửa đầy đủ: tiêu đề, mô tả, trạng thái, mức độ nghiêm trọng, dịch vụ bị ảnh hưởng và người phụ trách.
   - API endpoint PUT /api/incidents/{id} hoạt động hoàn hảo với hệ thống enum mới.
   - Loại bỏ các hàm chuyển đổi không cần thiết, đơn giản hóa code.

### Thêm tính năng mới:

1. **Backend**: 
   - Thêm model/entity trong `backend/src/main/java/com/nganhang/sentinel/model`
   - Tạo repository trong `backend/src/main/java/com/nganhang/sentinel/repository`
   - Triển khai logic nghiệp vụ trong `backend/src/main/java/com/nganhang/sentinel/service`
   - Cung cấp RESTful API trong `backend/src/main/java/com/nganhang/sentinel/controller`

2. **Frontend**:
   - Với phiên bản HTML/JS: Chỉnh sửa `frontend/src/fallback-index.html`
   - Với phiên bản Angular: Phát triển trong `frontend/src/app`

### Thông tin kỹ thuật quan trọng:

1. **Mức độ nghiêm trọng**: 
   - **CRITICAL** (Nghiêm trọng): Sự cố ảnh hưởng đến nhiều người dùng
   - **HIGH** (Cao): Vấn đề chức năng quan trọng ảnh hưởng đến một số người dùng  
   - **MEDIUM** (Trung bình): Vấn đề chức năng nhỏ ảnh hưởng đến ít người dùng
   - **LOW** (Thấp): Tác động tối thiểu, thường cho cải tiến hoặc sửa lỗi không khẩn cấp
   
   *Lưu ý: Hệ thống đã cập nhật từ mã phân loại cũ (SEV1-SEV4) sang hệ thống mô tả rõ ràng hơn*

2. **Trạng thái sự cố**:
   - **INVESTIGATING**: Đang điều tra
   - **IDENTIFIED**: Đã xác định  
   - **MONITORING**: Đang theo dõi
   - **RESOLVED**: Đã giải quyết

### Quản lý Database:

Sử dụng Flyway migration để quản lý schema. Thêm file migration mới trong `backend/src/main/resources/db/migration` với quy ước đặt tên `V{n}__description.sql`.

## 🤝 Đóng góp

Nếu bạn muốn đóng góp cho dự án, vui lòng:
1. Fork repository
2. Tạo branch mới (`git checkout -b feature/amazing-feature`)
3. Commit thay đổi của bạn (`git commit -m 'Add some amazing feature'`)
4. Push lên branch (`git push origin feature/amazing-feature`)
5. Mở Pull Request
