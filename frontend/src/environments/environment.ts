export const environment = {
  production: false,
  apiUrl: window.location.hostname === 'localhost' 
    ? 'http://localhost:8080/api' 
    : window.location.hostname === 'tunning-ops-sit.namabank.com.vn' 
      ? '/api' 
      : '/api'
};
