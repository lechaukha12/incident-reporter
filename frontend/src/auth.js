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
    
    // User info is now handled by individual pages' loadUserInfo() function
    // No need to add user info to navbar here anymore
});

// Note: addUserInfoToNavbar function removed 
// User info is now handled by individual pages with round avatars
