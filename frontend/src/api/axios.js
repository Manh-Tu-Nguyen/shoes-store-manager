import axios from 'axios';

const axiosInstance = axios.create({
  // 1. CHỈ ĐỊNH ĐÍCH DANH NHÀ CỦA SPRING BOOT (CỔNG 8080)
  // Sau này khi đưa lên server thực tế (Production), bạn mới cần dùng file .env
  baseURL: 'http://localhost:8080/api', 
  timeout: 10000, 
  headers: {
    'Content-Type': 'application/json',
  },
});

// --- BỘ ĐÁNH CHẶN REQUEST (Trước khi gửi đi) ---
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    console.log("DEBUG - Axios Interceptor - Token đang gửi:", token);
    // Nếu có vé (token) trong ví, tự động kẹp vào Header
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
  
);

// --- BỘ ĐÁNH CHẶN RESPONSE (Sau khi nhận về) ---
axiosInstance.interceptors.response.use(
  (response) => {
    // Tự động bóc vỏ data, component Vue không cần gọi response.data.data nữa
    return response.data;
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.error("Phiên đăng nhập hết hạn hoặc không có quyền truy cập.");
          // Quét sạch két sắt
          localStorage.removeItem('token');
          localStorage.removeItem('role'); 
          localStorage.removeItem('user');           
          const currentPath = window.location.pathname;
          if (currentPath.startsWith('/admin') || currentPath.startsWith('/staff')) {
            window.location.href = '/login'; 
          }
          // Nếu ở trang chủ ('/'), kệ nó, không làm gì cả để khách vẫn xem được giao diện tĩnh.
          break;
        case 403:
          console.error("Bạn không có quyền truy cập tính năng này.");
          break;
        case 404:
          console.error("Không tìm thấy tài nguyên yêu cầu trên Backend.");
          break;
        case 500:
          console.error("Lỗi hệ thống từ Backend Java.");
          break;
        default:
          console.error("Lỗi không xác định:", error.response.data?.message);
      }
    } else {
      console.error("Không thể kết nối đến máy chủ. Spring Boot có thể đang tắt.");
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;