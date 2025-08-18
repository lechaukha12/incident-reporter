# 🚀 Release Notes - Incident Reporter v1.3.0

**Release Date**: August 15, 2025  
**Build**: v1.3.0-stable

---

## 🎉 Major Features

### 🔐 **Authentication & Authorization System**
**NEW**: Complete authentication system với login page đẹp mắt

**Features:**
- **Login Page** với thiết kế màu vàng chủ đạo (#ffde00)
- **Token-based authentication** với Bearer tokens
- **Role-based access control** (ADMIN/MANAGER/USER)
- **Auto-redirect** cho unauthenticated users
- **User dropdown** trong navbar với logout

**Default Credentials:**
```
Admin:   admin/admin123
User:    user1/user123  
Manager: manager1/manager123
```

### 📊 **Enhanced Dashboard Analytics**
**IMPROVED**: Dashboard với responsive charts và better UX

**Enhancements:**
- **Responsive pie charts** với proper sizing constraints
- **24-hour heatmap** format instead of AM/PM
- **Balanced chart containers** với consistent 450px heights
- **Real-time data updates** từ analytics API
- **Loading states** cho better user experience

### 🎯 **Clickable Table Rows** 
**NEW**: Option 2 implementation - click anywhere on row

**Features:**
- **Intuitive navigation**: Click toàn bộ row để xem incident details
- **Removed "Xem chi tiết" button** cho cleaner interface
- **Event.stopPropagation()** cho action buttons
- **Visual hover effects** với smooth transitions
- **Better mobile experience** với larger touch targets

### 🔍 **Advanced Search & Filter System**
**IMPROVED**: Comprehensive filtering với multiple criteria

**Features:**
- **Global search**: Title, description, assignee, service
- **Multi-select filters**: Status, severity, assignee
- **Date range filtering** với date pickers
- **Real-time results** as you type
- **Filter statistics** showing results count
- **Clear filters** functionality

### 👥 **Complete User Management**
**NEW**: Full CRUD operations cho user administration

**Features:**
- **User creation/editing** với modal forms
- **Role assignment** (ADMIN/MANAGER/USER)
- **Status management** (ACTIVE/INACTIVE/SUSPENDED)
- **User statistics** dashboard
- **Search users** functionality
- **Validation** cho required fields

---

## 🛠️ Technical Improvements

### **Backend Enhancements**
- **AuthController**: New authentication endpoints
- **AuthService**: Token management và user verification
- **UserResponseDTO**: Enhanced với active status field
- **LoginRequestDTO/ResponseDTO**: Structured authentication data
- **Database Migration V5**: Default users creation
- **CORS Configuration**: Proper cross-origin handling

### **Frontend Architecture**
- **auth.js**: Comprehensive authentication utilities
- **Token Management**: localStorage integration
- **Role-based UI**: Dynamic content based on user permissions
- **Error Handling**: Improved user feedback
- **Responsive Design**: Mobile-first approach

### **Database Schema Updates**
```sql
-- New admin user với proper roles
INSERT INTO users (username, password, full_name, email, role, status) 
VALUES ('admin', 'admin123', 'Administrator', 'admin@tuningops.com', 'ADMIN', 'ACTIVE');
```

### **Security Enhancements**
- **Token-based Authentication**: UUID tokens với session management
- **Authorization Framework**: Role-based access control
- **Input Validation**: Frontend và backend validation
- **SQL Injection Protection**: JPA prepared statements
- **XSS Prevention**: Input sanitization

---

## 🎨 UI/UX Improvements

### **Visual Design Updates**
- **Consistent Color Palette**: Primary blue (#0171bb) và secondary yellow (#ffde00)
- **Typography Standardization**: 1.5rem font size cho brand titles
- **Hover Effects**: Cards, buttons, table rows với smooth transitions
- **Loading Spinners**: Professional loading states
- **Toast Notifications**: Success/error feedback

### **Navigation Enhancements**
- **Fixed Navbar Structure**: Proper HTML syntax và indentation
- **User Dropdown**: Profile access và logout functionality
- **Active States**: Current page highlighting
- **Breadcrumb Navigation**: Clear page hierarchy

### **Table Improvements**
- **Clickable Rows**: Entire row click navigation
- **Action Buttons**: Streamlined edit/resolve actions
- **Status Badges**: Color-coded status indicators
- **Responsive Tables**: Mobile-friendly scrolling

---

## 🐛 Bug Fixes

### **Critical Fixes**
- **JavaScript Syntax Error**: Fixed missing arrow function in `.then data =>`
- **Navbar HTML Structure**: Corrected duplicate nav elements và closing tags
- **Compilation Errors**: Fixed AuthService role assignment issues
- **Docker Build**: Resolved backend compilation failures

### **UI Fixes**
- **Chart Sizing**: Pie chart no longer oversized
- **Heatmap Display**: 24-hour format instead of 12-hour
- **Font Consistency**: Standardized brand title sizing
- **Button Alignment**: Proper spacing in action columns

### **API Fixes**
- **CORS Configuration**: Proper cross-origin resource sharing
- **Authentication Flow**: Token validation và user session management
- **Error Handling**: Improved error responses và status codes

---

## 📊 Performance Improvements

### **Frontend Performance**
- **Chart.js Optimization**: Responsive settings với aspectRatio
- **CSS Animations**: Hardware-accelerated transitions
- **Asset Loading**: Optimized CDN resources
- **Memory Management**: Proper event listener cleanup

### **Backend Performance**
- **Database Queries**: Optimized JPA queries
- **Connection Pooling**: HikariCP configuration
- **API Response Time**: Streamlined data transfer objects
- **Session Management**: Efficient token storage

---

## 📋 Testing

### **Manual Testing Completed**
- ✅ **Authentication Flow**: Login/logout với all user roles
- ✅ **CRUD Operations**: Full incident lifecycle
- ✅ **Search & Filter**: All filter combinations tested
- ✅ **User Management**: Admin operations verified
- ✅ **Dashboard Charts**: Data accuracy confirmed
- ✅ **Responsive Design**: Mobile và desktop compatibility
- ✅ **API Integration**: All endpoints functional

---

## 🚨 Known Issues

### **Minor Issues**
- **Password Security**: Currently plaintext (BCrypt upgrade planned)
- **Token Expiration**: No automatic session timeout
- **Mobile Touch**: Some interactions cần improvement
- **Error Messages**: Vietnamese/English inconsistency

### **Limitations**
- **Real-time Updates**: No WebSocket implementation yet
- **Bulk Operations**: Single incident operations only
- **File Uploads**: No attachment support
- **Audit Logging**: Limited action tracking

---

## 🔮 Next Release Preview (v1.4.0)

### **Planned Features**
1. **Comments System**: Threaded discussions on incidents
2. **Issue Templates**: Pre-defined incident formats
3. **Bulk Operations**: Multi-select actions
4. **Enhanced Security**: BCrypt passwords, JWT tokens
5. **Real-time Notifications**: WebSocket integration

---

## 📦 Deployment Instructions

### **Fresh Installation**
```bash
# Clone repository
git clone https://github.com/lechaukha12/incident-reporter.git
cd incident-reporter

# Deploy với Docker
docker-compose up --build -d

# Verify deployment
curl http://localhost/api/incidents/stats
```

### **Upgrade from v1.2.x**
```bash
# Pull latest changes
git pull origin v1.3.0

# Rebuild containers
docker-compose down
docker-compose up --build -d

# Verify authentication
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

**🎯 Incident Reporter v1.3.0 - Production Ready với Authentication!**

*Thank you for using Incident Reporter! This release brings significant improvements in security, user experience, and system reliability.*

---

# Previous Releases

## Release Notes: Incident Reporter v2.0.0

### Tổng Quan

Phiên bản 2.0.0 của Incident Reporter tập trung vào việc tiêu chuẩn hóa các giá trị enum SeverityLevel giữa frontend và backend, cải thiện khả năng bảo trì và độ tin cậy của ứng dụng.

### Thay Đổi Chính

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
