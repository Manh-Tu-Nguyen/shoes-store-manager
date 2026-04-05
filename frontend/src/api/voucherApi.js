import axiosInstance from './axios';

const voucherApi = {
  // Lấy toàn bộ danh sách mã giảm giá
  getAll: () => axiosInstance.get('/vouchers'),

  // Xem chi tiết một mã cụ thể (dùng khi cần chỉnh sửa)
  getById: (id) => axiosInstance.get(`/vouchers/${id}`),

  // Tạo mới chương trình khuyến mãi
  create: (data) => axiosInstance.post('/vouchers', data),

  // Cập nhật thông tin voucher (thời hạn, số lượng, điều kiện)
  update: (id, data) => axiosInstance.put(`/vouchers/${id}`, data),

  // Vô hiệu hóa voucher (Xóa mềm - Chuyển trạng thái ngừng áp dụng)
  delete: (id) => axiosInstance.delete(`/vouchers/${id}`),
};

export default voucherApi;