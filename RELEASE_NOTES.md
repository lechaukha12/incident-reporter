# Release Notes: Incident Reporter v2.0.0

## Tổng Quan

Phiên bản 2.0.0 của Incident Reporter tập trung vào việc tiêu chuẩn hóa các giá trị enum SeverityLevel giữa frontend và backend, cải thiện khả năng bảo trì và độ tin cậy của ứng dụng.

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

## Hướng Dẫn Nâng Cấp

### Triển Khai Docker Compose

```bash
# Pull phiên bản mới nhất
git pull origin main

# Khởi động lại các container
docker-compose down
docker-compose up -d
```

### Triển Khai Kubernetes

```bash
# Pull phiên bản mới nhất
git pull origin main

# Áp dụng các thay đổi
cd k8s
./deploy.sh
```

## Lỗi Đã Sửa

1. **Không Nhất Quán Về Giá Trị Enum SeverityLevel**
   - Vấn đề: Frontend sử dụng SEV1/SEV2/SEV3/SEV4 trong khi backend sử dụng CRITICAL/HIGH/MEDIUM/LOW
   - Giải pháp: Tiêu chuẩn hóa tất cả mã để sử dụng các giá trị từ enum của backend

2. **Lỗi Cấu Hình Nginx**
   - Vấn đề: Cấu hình Nginx trỏ đến "backend-service" thay vì "backend"
   - Giải pháp: Cập nhật cấu hình để sử dụng tên dịch vụ chính xác

## Lưu Ý Tương Thích

- **Thay Đổi Breaking**: Các client API tùy chỉnh sử dụng giá trị SEV1/SEV2/SEV3/SEV4 cần được cập nhật để sử dụng CRITICAL/HIGH/MEDIUM/LOW
- **Dữ Liệu Hiện Có**: Không cần di chuyển dữ liệu, vì database đã sử dụng các giá trị enum của backend

## Nhóm Phát Triển

- Team Vietnamese IT Operations
- Người liên hệ: tech-support@example.com
