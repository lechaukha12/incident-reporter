// Authentication utilities
const AUTH_TOKEN_KEY = 'token';
const AUTH_USER_KEY = 'user';

function isAuthenticated() {
    return localStorage.getItem(AUTH_TOKEN_KEY) !== null;
}

function getAuthToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

function getCurrentUser() {
    const userStr = localStorage.getItem(AUTH_USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
}

function logout() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
    window.location.href = 'login.html';
}

function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

function hasRole(requiredRole) {
    const user = getCurrentUser();
    return user && user.role === requiredRole;
}

function hasAnyRole(roles) {
    const user = getCurrentUser();
    return user && roles.includes(user.role);
}

// Add auth header to fetch requests
function authFetch(url, options = {}) {
    const token = getAuthToken();
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }
    return fetch(url, options);
}

// Initialize auth check on page load
document.addEventListener('DOMContentLoaded', function() {
    // Skip auth check for login page
    if (window.location.pathname.includes('login.html')) {
        return;
    }
    
    // Require authentication for all other pages
    if (!requireAuth()) {
        return;
    }
    
    // Add user info to navbar if available
    const user = getCurrentUser();
    if (user) {
        addUserInfoToNavbar(user);
    }
});

function addUserInfoToNavbar(user) {
    // Find navbar nav element
    const navbarNav = document.querySelector('.navbar-nav.ms-auto');
    if (navbarNav) {
        // Add user dropdown
        const userDropdown = document.createElement('div');
        userDropdown.className = 'nav-item dropdown';
        userDropdown.innerHTML = `
            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                <i class="fas fa-user me-1"></i>${user.fullName || user.username}
            </a>
            <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#"><i class="fas fa-user me-2"></i>Hồ sơ</a></li>
                <li><a class="dropdown-item" href="#"><i class="fas fa-cog me-2"></i>Cài đặt</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#" onclick="logout()"><i class="fas fa-sign-out-alt me-2"></i>Đăng xuất</a></li>
            </ul>
        `;
        navbarNav.appendChild(userDropdown);
    }
}
