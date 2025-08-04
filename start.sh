#!/bin/bash
# Script khởi chạy Hệ thống Tuning OPS với Docker Compose

echo "Khởi chạy Hệ thống Tuning OPS..."

# Kiểm tra Docker và Docker Compose
if ! command -v docker &> /dev/null; then
    echo "Docker không được cài đặt. Vui lòng cài đặt Docker trước."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose không được cài đặt. Vui lòng cài đặt Docker Compose trước."
    exit 1
fi

# Chuyển đến thư mục dự án
cd "$(dirname "$0")"

# Dừng các container đang chạy (nếu có)
echo "Dừng các container đang chạy (nếu có)..."
docker-compose down

# Build và khởi chạy ứng dụng
echo "Build và khởi chạy ứng dụng..."
docker-compose up -d --build

# Kiểm tra trạng thái
echo "Kiểm tra trạng thái các container..."
docker-compose ps

# Chờ backend khởi động
echo "Chờ backend khởi động..."
echo "Sẽ thử kết nối đến backend trong 30 giây..."
sleep 10

# Đếm thời gian chờ
attempts=0
max_attempts=20
until $(curl --output /dev/null --silent --head --fail http://localhost:8080/api/incidents); do
    if [ ${attempts} -eq ${max_attempts} ]; then
        echo "Backend không khởi động sau ${max_attempts} lần thử. Vui lòng kiểm tra logs."
        echo "Để xem logs của backend, chạy: docker-compose logs backend"
        exit 1
    fi
    
    printf '.'
    attempts=$(($attempts+1))
    sleep 1
done

echo -e "\nBackend đã sẵn sàng!"
echo "API Backend: http://localhost:8080/api/incidents"
echo "Frontend: http://localhost:4200"
echo "API Test: http://localhost:4200/api-test.html"
echo ""
echo "Để dừng hệ thống, chạy: docker-compose down"
echo "Để xem logs, chạy: docker-compose logs -f"
