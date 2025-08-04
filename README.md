Hệ thống Quản lý Sự cố - Sentinel
Một ứng dụng web nội bộ giúp ghi nhận, theo dõi và quản lý các sự cố kỹ thuật (incidents) một cách tập trung, có hệ thống và tự động hóa, được xây dựng trên nền tảng Java Spring Boot và Angular.

🎯 Vấn đề & Mục tiêu
Vấn đề

Tại môi trường microservices phức tạp của ứng dụng mobile banking, việc xử lý sự cố đang gặp các thách thức:

Phản ứng chậm: Sự cố chỉ được phát hiện khi có người dùng phàn nàn hoặc DevOps phải truy vấn log/trace thủ công.

Quy trình rời rạc: Thông tin về sự cố bị phân tán qua nhiều kênh chat, không có một nơi ghi nhận tập trung.

Mất thời gian: Đội ngũ tốn nhiều thời gian để tìm hiểu "chuyện gì đã xảy ra" thay vì tập trung vào việc khắc phục.

Khó rút kinh nghiệm: Không có dữ liệu lịch sử để phân tích, tìm ra nguyên nhân gốc rễ và ngăn ngừa các sự cố tương tự trong tương lai.

Mục tiêu

Dự án Sentinel được xây dựng để giải quyết các vấn đề trên, với mục tiêu:

Tập trung hóa toàn bộ thông tin về sự cố vào một nơi duy nhất.

Tự động hóa việc tạo sự cố ngay khi hệ thống giám sát phát hiện dấu hiệu bất thường.

Chuẩn hóa quy trình xử lý sự cố từ lúc phát hiện đến khi giải quyết xong.

Cung cấp dữ liệu để phân tích và cải tiến, giảm thiểu thời gian khắc phục (MTTR) và tăng độ ổn định cho hệ thống.

✨ Các tính năng cốt lõi
Quản lý Incident Toàn diện: Tạo, cập nhật, phân công và theo dõi incident theo vòng đời (Investigating -> Identified -> Monitoring -> Resolved).

Phân loại theo Mức độ: Gán mức độ nghiêm trọng (Severity Level SEV1 -> SEV4) để ưu tiên xử lý.

Nhật ký hành động (Timeline): Ghi lại toàn bộ diễn biến, quyết định và hành động xử lý theo thời gian thực.

Tự động tạo Incident: Tích hợp với hệ thống cảnh báo (Grafana, Prometheus) qua Webhook để tự động tạo incident.

Thông báo tức thì: Tích hợp với Slack/Teams để gửi thông báo về các cập nhật quan trọng của incident đến kênh chat tương ứng.

Giao diện trực quan: Dashboard hiển thị danh sách incident, cho phép lọc và tìm kiếm dễ dàng.

🛠️ Ngăn xếp Công nghệ (Tech Stack)
Phần

Công nghệ

Lý do lựa chọn

Frontend

Angular + Ant Design (NG-ZORRO)

Framework mạnh mẽ, cấu trúc rõ ràng, phù hợp cho các ứng dụng doanh nghiệp phức tạp.

Backend

Java + Spring Boot

Hệ sinh thái vững chắc, hiệu năng cao, bảo mật tốt, được tin dùng trong ngành tài chính.

Database

PostgreSQL

Hệ CSDL quan hệ mạnh mẽ, ổn định, hỗ trợ tốt các kiểu dữ liệu phức tạp.

Triển khai

Docker & Docker Compose

Đóng gói toàn bộ ứng dụng, dễ dàng thiết lập môi trường và triển khai nhất quán.

🏗️ Cấu trúc Thư mục
sentinel/
├── .gitignore
├── docker-compose.yml
├── README.md
├── backend/
│   ├── src/main/java/com/nganhang/sentinel/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── model/ (or entity/)
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
    │   │   ├── core/
    │   │   ├── features/
    │   │   ├── shared/
    │   │   ├── app-routing.module.ts
    │   │   ├── app.component.ts
    │   │   └── app.module.ts
    │   ├── assets/
    │   ├── environments/
    │   └── index.html
    ├── angular.json
    └── package.json

🔌 Thiết kế API (Endpoints chính)
POST /api/incidents: Tạo một incident mới (có thể được gọi từ webhook).

GET /api/incidents: Lấy danh sách incidents (hỗ trợ filter, sort, phân trang).

GET /api/incidents/{id}: Lấy chi tiết một incident và timeline của nó.

PUT /api/incidents/{id}: Cập nhật trạng thái, mức độ nghiêm trọng, người phụ trách.

POST /api/incidents/{id}/updates: Thêm một ghi nhận mới vào timeline của incident.

🚀 Bắt đầu Nhanh (Getting Started)
Yêu cầu:

Git

Docker & Docker Compose

JDK (để build backend)

Node.js & npm/yarn (để build frontend)

Các bước:

Clone repository:

git clone <your-repository-url>
cd sentinel

Cấu hình môi trường:
Sao chép application.properties.example thành application.properties trong backend và environment.ts.example thành environment.ts trong frontend, sau đó điền các thông tin cần thiết.

Chạy ứng dụng với Docker Compose:
Lệnh này sẽ build các images và khởi chạy tất cả các services (frontend, backend, db).

docker-compose up -d --build

Truy cập ứng dụng:

Frontend: http://localhost:4200 (port mặc định của Angular)

Backend API: http://localhost:8080 (port mặc định của Spring Boot)