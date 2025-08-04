export const environment = {
  production: false,
  apiUrl: window.location.hostname === 'localhost' ? 'http://localhost:8080/api' : 'http://backend:8080/api'
};
