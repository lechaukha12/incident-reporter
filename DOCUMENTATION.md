# 📖 Incident Reporter System Documentation v1.3.0

## 🎯 Tổng quan hệ thống
**Incident Reporter** là hệ thống quản lý sự cố (issue) chuyên nghiệp được xây dựng với Spring Boot và frontend HTML/CSS/JavaScript. Hệ thống hỗ trợ theo dõi, phân tích và quản lý các sự cố trong môi trường doanh nghiệp.

---

## 🏗️ Kiến trúc hệ thống

### **Backend Architecture**
- **Framework**: Spring Boot 2.7.x
- **Database**: PostgreSQL 14
- **ORM**: JPA/Hibernate  
- **Migration**: Flyway
- **Containerization**: Docker & Docker Compose

### **Frontend Architecture**
- **Technology**: HTML5, CSS3, JavaScript (ES6+)
- **UI Framework**: Bootstrap 5.3.2
- **Icons**: Font Awesome 6.4.0, Bootstrap Icons
- **Charts**: Chart.js
- **Web Server**: Nginx Alpine

### **Deployment Architecture**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   Database      │
│   (Nginx)       │    │  (Spring Boot)  │    │  (PostgreSQL)   │
│   Port: 80      │────│   Port: 8080    │────│   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

---

## 🚀 Features đã implement

### 1. **Authentication & Authorization System** 🔐
#### **Login Page**
- **Design**: Màu vàng chủ đạo (#ffde00) với gradient background
- **UI Features**: 
  - Animated floating shapes
  - Responsive design (mobile/desktop)
  - Loading states và error handling
  - Professional branding với logo Tuning OPS
- **URL**: `/login.html`

#### **Authentication API**
- **Endpoint**: `POST /api/auth/login`
- **Token-based authentication** với UUID tokens
- **In-memory token storage** (production nên dùng Redis)
- **Role-based access control**

#### **Default Users**
```javascript
// Admin user
Username: admin
Password: admin123
Role: ADMIN

// Regular user  
Username: user1
Password: user123
Role: USER

// Manager user
Username: manager1  
Password: manager123
Role: MANAGER
```

#### **Authorization Framework**
- **auth.js**: JavaScript utilities cho authentication
- **requireAuth()**: Auto redirect to login page
- **hasRole(), hasAnyRole()**: Role-based permissions
- **authFetch()**: Auto-attach Bearer token
- **User dropdown**: Navbar với logout functionality

### 2. **Dashboard & Analytics** 📊
#### **Dashboard Page** (`/dashboard.html`)
- **Metrics Cards**: Tổng số issue, đang hoạt động, đã giải quyết, thời gian TB
- **Charts**:
  - **Pie Chart**: Phân bố theo độ nghiêm trọng (responsive sizing)
  - **Line Chart**: Xu hướng 7 ngày qua
  - **Bar Chart**: Top dịch vụ bị ảnh hưởng  
  - **Heatmap**: Issues theo giờ trong ngày (24h format)
- **API Integration**: `/api/analytics/dashboard`
- **Real-time Updates**: Auto-refresh data

### 3. **Incident Management** 📝
#### **Main Issue List** (`/index.html`)
- **CRUD Operations**: Create, Read, Update, Delete issues
- **Smart Table**: 
  - **Clickable rows** (Option 2 implemented) - click anywhere on row to view details
  - Removed "Xem chi tiết" button for cleaner UI
  - Action buttons: Edit, Resolve (with event.stopPropagation)
- **Issue Fields**:
  - ID, Title, Description
  - Status: INVESTIGATING, IDENTIFIED, MONITORING, RESOLVED
  - Severity: CRITICAL, HIGH, MEDIUM, LOW
  - Assigned user, Reporter, Service affected
  - Creation time, Resolution time
  - Notes, Root cause

#### **Issue Detail Page** (`/incident-detail.html`)
- **Detailed View**: Full issue information
- **Timeline**: Chronological updates và changes
- **Edit Capability**: In-place editing
- **Status Updates**: Real-time status changes

### 4. **Advanced Search & Filter** 🔍
#### **Global Search**
- **Search fields**: Title, description, assignee, service
- **Real-time filtering**: Instant results as you type
- **Search statistics**: Results count display

#### **Advanced Filters**
- **Status Filter**: Multi-select với tất cả status options
- **Severity Filter**: Multi-select độ nghiêm trọng
- **Assignee Filter**: Dropdown populated từ user database
- **Date Range**: Từ ngày - đến ngày với date pickers
- **Clear Filters**: Reset về trạng thái mặc định

#### **Sorting**
- **Sort by**: Title, Status, Severity, Date, Assignee
- **Sort direction**: Ascending/Descending
- **Visual indicators**: Sort arrows in column headers

### 5. **User Management** 👥
#### **Users Page** (`/users.html`)
- **User CRUD**: Create, Read, Update, Delete users
- **User Fields**:
  - Username (unique), Password
  - Full name, Email, Phone number
  - Department, Role (ADMIN/MANAGER/USER)
  - Status (ACTIVE/INACTIVE/SUSPENDED)
  - Last login tracking
- **User Statistics**: Total, Active, Inactive, Admin counts
- **Search Users**: Real-time user search
- **Modal Forms**: Bootstrap modals cho create/edit

#### **User Roles**
```java
ADMIN     - Full system access
MANAGER   - Team management + issue oversight  
USER      - Basic issue reporting và updates
```

### 6. **Enhanced UX/UI** 🎨
#### **Design System**
- **Color Palette**:
  - Primary Blue: #0171bb
  - Secondary Yellow: #ffde00
  - Success Green: #28a745
  - Warning Orange: #fd7e14
  - Danger Red: #dc3545

#### **Interactive Elements**
- **Hover Effects**: Cards, buttons, table rows
- **Loading Spinners**: API calls và data loading
- **Toast Notifications**: Success/error messages
- **Responsive Tables**: Mobile-friendly scrolling
- **Modal Dialogs**: Create/edit forms

#### **Navigation**
- **Clean Navbar Design**: Streamlined navigation without redundant system title
- **Standardized Navigation Buttons**: Consistent sizing, spacing, and hover effects
- **Round User Avatar**: Role-based color-coded avatar with dropdown menu
- **Active States**: Current page highlighting
- **Professional Interactions**: Smooth animations and visual feedback

---

## 🗄️ Database Schema

### **Users Table**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    department VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT NOW(),
    last_login_at TIMESTAMP
);
```

### **Incidents Table**
```sql
CREATE TABLE incidents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'INVESTIGATING',
    severity_level VARCHAR(50) NOT NULL,
    affected_service VARCHAR(255),
    assignee VARCHAR(255),
    reported_by VARCHAR(255),
    resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    notes TEXT,
    root_cause TEXT
);
```

### **Incident Updates Table (Timeline)**
```sql
CREATE TABLE incident_updates (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT REFERENCES incidents(id),
    message TEXT NOT NULL,
    update_type VARCHAR(50) NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW()
);
```

---

## 🔧 API Endpoints

### **Authentication Endpoints**
```bash
POST   /api/auth/login      # User login
POST   /api/auth/logout     # User logout  
GET    /api/auth/me         # Current user info
```

### **Incident Endpoints**
```bash
GET    /api/incidents                    # List all incidents (paginated)
POST   /api/incidents                    # Create new incident
GET    /api/incidents/{id}               # Get incident details
PUT    /api/incidents/{id}               # Update incident
DELETE /api/incidents/{id}               # Delete incident
POST   /api/incidents/{id}/resolve       # Mark as resolved
GET    /api/incidents/stats              # Incident statistics
```

### **User Endpoints**
```bash
GET    /api/users           # List all users
POST   /api/users           # Create new user
GET    /api/users/{id}      # Get user details
PUT    /api/users/{id}      # Update user
DELETE /api/users/{id}      # Delete user
GET    /api/users/stats     # User statistics
```

### **Analytics Endpoints**
```bash
GET    /api/analytics/dashboard          # Dashboard metrics
```

---

## 🐳 Deployment

### **Docker Compose Setup**
```yaml
services:
  postgres:
    image: postgres:14-alpine
    environment:
      POSTGRES_DB: sentinel
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      
  frontend:
    build: 
      context: ./frontend
      dockerfile: Dockerfile.nginx
    ports:
      - "80:80"
    depends_on:
      - backend
```

### **Deployment Commands**
```bash
# Development
docker-compose up --build -d

# Production với specific tags
docker-compose -f docker-compose.prod.yml up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f [service-name]
```

---

## 📂 File Structure

```
incident-reporter/
├── backend/
│   ├── src/main/java/com/nganhang/sentinel/
│   │   ├── controller/           # REST Controllers
│   │   │   ├── AuthController.java
│   │   │   ├── IncidentController.java
│   │   │   ├── UserController.java
│   │   │   └── AnalyticsController.java
│   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── LoginRequestDTO.java
│   │   │   ├── LoginResponseDTO.java
│   │   │   ├── IncidentCreateDTO.java
│   │   │   └── UserResponseDTO.java
│   │   ├── model/                # JPA Entities
│   │   │   ├── User.java
│   │   │   ├── Incident.java
│   │   │   └── IncidentUpdate.java
│   │   ├── repository/           # Data Access Layer
│   │   │   ├── UserRepository.java
│   │   │   ├── IncidentRepository.java
│   │   │   └── IncidentUpdateRepository.java
│   │   ├── service/              # Business Logic
│   │   │   ├── AuthService.java
│   │   │   ├── IncidentService.java
│   │   │   ├── UserService.java
│   │   │   └── AnalyticsService.java
│   │   └── config/               # Configuration
│   │       └── CorsConfig.java
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/         # Flyway migrations
│           ├── V1__create_incidents_table.sql
│           ├── V2__update_severity_levels.sql
│           ├── V3__add_notes_and_root_cause.sql
│           ├── V4__create_users_table.sql
│           └── V5__create_admin_user.sql
├── frontend/
│   ├── src/
│   │   ├── index.html            # Main issue list
│   │   ├── login.html            # Authentication page
│   │   ├── dashboard.html        # Analytics dashboard
│   │   ├── users.html            # User management
│   │   ├── incident-detail.html  # Issue details
│   │   └── auth.js               # Authentication utilities
│   ├── nginx.conf                # Nginx configuration
│   └── Dockerfile.nginx          # Frontend container
├── docker-compose.yml            # Development deployment
└── README.md                     # Project documentation
```

---

## 🔒 Security Features

### **Authentication Security**
- **Token-based authentication** với UUID tokens
- **Session management** trong localStorage
- **Auto-redirect** for unauthenticated users
- **Password protection** (plaintext - cần upgrade BCrypt)
- **Role-based authorization** framework

### **Input Validation**
- **Frontend validation**: Required fields, email format
- **Backend validation**: JPA validation annotations
- **SQL Injection Protection**: JPA Prepared Statements
- **XSS Prevention**: Input sanitization

### **CORS Configuration**
- **Cross-Origin Resource Sharing** properly configured
- **API endpoint protection**
- **Development-friendly** CORS policy

---

## 📊 Performance Metrics

### **Database Performance**
- **Pagination**: Large dataset handling
- **Indexing**: Primary keys và foreign keys
- **Connection Pooling**: HikariCP default
- **Query Optimization**: JPA query methods

### **Frontend Performance**
- **Lazy Loading**: Charts load on demand
- **Caching**: Static assets caching
- **Minification**: Bootstrap và libraries từ CDN
- **Responsive Images**: Optimized loading

### **API Performance**
- **RESTful Design**: Stateless API calls
- **JSON Serialization**: Efficient data transfer
- **Error Handling**: Proper HTTP status codes
- **Logging**: Comprehensive error logging

---

## 🧪 Testing Strategy

### **Manual Testing Checklist**
- [ ] **Authentication Flow**: Login/logout functionality
- [ ] **CRUD Operations**: Create, read, update, delete incidents
- [ ] **Search & Filter**: All filter combinations
- [ ] **User Management**: Admin user operations
- [ ] **Dashboard Charts**: Data visualization accuracy
- [ ] **Responsive Design**: Mobile và desktop views
- [ ] **Error Handling**: Network failures, invalid inputs

### **API Testing**
```bash
# Test login
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Test incidents API
curl -X GET http://localhost/api/incidents \
  -H "Authorization: Bearer {token}"
```

---

## 🚨 Known Issues & Limitations

### **Security Limitations**
1. **Password Storage**: Plaintext passwords (cần BCrypt)
2. **Token Storage**: In-memory (production cần Redis)
3. **Session Timeout**: No automatic token expiration
4. **HTTPS**: Development sử dụng HTTP

### **Performance Limitations**
1. **Pagination**: Large datasets chưa optimize hoàn toàn
2. **Real-time Updates**: Chưa có WebSocket
3. **Caching**: No application-level caching
4. **Database Indexing**: Cần optimize cho search queries

### **UI/UX Limitations**
1. **Mobile Experience**: Cần improve touch interactions
2. **Accessibility**: ARIA labels và keyboard navigation
3. **Dark Mode**: Chưa support dark theme
4. **Internationalization**: English/Vietnamese mixed

---

## 🔮 Roadmap v1.4.0

### **Priority Features**
1. **Comments System** 🗨️
   - Threaded comments on incidents
   - Real-time notifications
   - File attachments support

2. **Issue Templates** 📋
   - Pre-defined incident templates
   - Template management
   - Quick incident creation

3. **Bulk Operations** ⚡
   - Multi-select incidents
   - Bulk status updates
   - Bulk assignment

4. **Enhanced Security** 🔐
   - BCrypt password hashing
   - JWT tokens với expiration
   - Rate limiting
   - Session management

### **Future Enhancements**
- **Mobile App**: React Native/Flutter
- **Real-time Notifications**: WebSocket integration
- **Advanced Analytics**: Machine learning insights
- **Integration APIs**: Slack, Teams, Email notifications
- **Workflow Automation**: Custom incident workflows
- **Audit Logging**: Complete action tracking

---

## 📞 Support & Maintenance

### **Development Team**
- **Backend Developer**: Spring Boot, PostgreSQL, APIs
- **Frontend Developer**: HTML/CSS/JavaScript, UI/UX
- **DevOps Engineer**: Docker, deployment, monitoring

### **Monitoring & Logs**
```bash
# Application logs
docker logs sentinel-backend -f

# Database logs  
docker logs sentinel-postgres -f

# Nginx logs
docker logs sentinel-frontend -f
```

### **Backup Strategy**
- **Database Backup**: PostgreSQL dumps
- **Code Repository**: Git version control
- **Container Images**: Docker registry

---

*Last Updated: August 15, 2025 | Version: 1.3.0*
