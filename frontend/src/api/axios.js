import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api', // Sử dụng proxy đã cấu hình trong vite.config.js
  timeout: 10000, 
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.error("Phiên đăng nhập hết hạn.");
          localStorage.removeItem('token');
          window.location.href = '/auth/login';
          break;
        case 403:
          console.error("Bạn không có quyền truy cập tính năng này.");
          break;
        case 404:
          console.error("Không tìm thấy tài nguyên yêu cầu.");
          break;
        case 500:
          console.error("Lỗi hệ thống từ Backend.");
          break;
        default:
          console.error("Lỗi không xác định:", error.response.data.message);
      }
    } else {
      console.error("Không thể kết nối đến máy chủ. Vui lòng kiểm tra IntelliJ.");
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;