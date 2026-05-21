package com.example.backend.service.order;

import com.example.backend.dto.order.OrderDTO;
import com.example.backend.dto.order.OrderDetailDTO;
import com.example.backend.dto.order.POSCheckoutDTO;
import com.example.backend.dto.product.ProductDetailDTO; // Đảm bảo đã import DTO này
import com.example.backend.dto.product.ColorDTO;
import com.example.backend.dto.product.SizeDTO;
import com.example.backend.entity.code.CodeType;
import com.example.backend.entity.order.Order;
import com.example.backend.entity.order.OrderDetail;
import com.example.backend.entity.product.ProductDetail;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.EmployeeRepository;
import com.example.backend.repository.order.OrderDetailRepository;
import com.example.backend.repository.order.OrderRepository;
import com.example.backend.repository.product.ProductDetailRepository;
import com.example.backend.service.code.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductDetailRepository productDetailRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final EmployeeRepository employeeRepository;
    private final com.example.backend.repository.auth.CustomerRepository customerRepository;

    // ==========================================
    // KHÂU ĐỌC DỮ LIỆU (READ) - BỔ SUNG MỚI
    // ==========================================

    // 1. Lấy toàn bộ danh sách hóa đơn, sắp xếp đơn mới nhất lên đầu
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // 2. Lấy danh sách sản phẩm chi tiết thuộc về một hóa đơn
    @Transactional(readOnly = true)
    public List<OrderDetailDTO> getOrderDetails(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn mang ID: " + orderId);
        }
        return orderDetailRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapDetailToDTO)
                .toList();
    }

    // ==========================================
    // CÁC HÀM TẠO / SỬA / XÓA (Giữ nguyên logic cũ của bạn)
    // ==========================================
    @Transactional
    public OrderDTO createOrder(OrderDTO dto, List<OrderDetailDTO> detailDTOs) {
        Order order = new Order();
        order.setCode(sequenceGeneratorService.generateCode(CodeType.ORDER));
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerPhone(dto.getCustomerPhone());
        order.setConsigneeName(dto.getConsigneeName());
        order.setConsigneePhone(dto.getConsigneePhone());
        order.setConsigneeAddress(dto.getConsigneeAddress());
        order.setShippingFee(dto.getShippingFee() != null ? dto.getShippingFee() : BigDecimal.ZERO);
        order.setVoucherDiscountValue(dto.getVoucherDiscountValue() != null ? dto.getVoucherDiscountValue() : BigDecimal.ZERO);
        order.setStatus(1);
        order.setOrderType("ONLINE");
        order.setTotalMoney(BigDecimal.ZERO);
        order.setTotalQuantity(0);
        order.setFinalAmount(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);
        for (OrderDetailDTO detailDTO : detailDTOs) {
            addOrUpdateOrderDetail(savedOrder.getId(), detailDTO);
        }
        return mapToDTO(orderRepository.findById(savedOrder.getId()).get());
    }

    @Transactional
    public OrderDTO addOrUpdateOrderDetail(Integer orderId, OrderDetailDTO detailDTO) {
        Order order = orderRepository.findWithRelationsById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));
        if (order.getStatus() != 1) throw new AppException(HttpStatus.BAD_REQUEST, "Không thể sửa hóa đơn đã chốt hoặc đã hủy");
        ProductDetail productDetail = productDetailRepository.findById(detailDTO.getProductDetailId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));
        Optional<OrderDetail> existingDetailOpt = orderDetailRepository.findByOrderIdAndProductDetailId(orderId, productDetail.getId());
        int oldQuantity = 0;
        OrderDetail orderDetail;
        if (existingDetailOpt.isPresent()) { orderDetail = existingDetailOpt.get(); oldQuantity = orderDetail.getQuantity(); }
        else { orderDetail = new OrderDetail(); orderDetail.setOrder(order); orderDetail.setProductDetail(productDetail); orderDetail.setPrice(productDetail.getPrice()); }
        int newQuantity = detailDTO.getQuantity();
        int delta = newQuantity - oldQuantity;
        if (delta > 0 && productDetail.getQuantity() < delta) throw new AppException(HttpStatus.BAD_REQUEST, "Sản phẩm " + productDetail.getName() + " không đủ số lượng!");
        productDetail.setQuantity(productDetail.getQuantity() - delta);
        productDetailRepository.save(productDetail);
        orderDetail.setQuantity(newQuantity);
        orderDetail.setTotalPrice(orderDetail.getPrice().multiply(new BigDecimal(newQuantity)));
        orderDetailRepository.save(orderDetail);
        recalculateOrder(order);
        return mapToDTO(order);
    }

    @Transactional
    public OrderDTO removeOrderDetail(Integer orderId, Integer orderDetailId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết hóa đơn"));
        ProductDetail productDetail = orderDetail.getProductDetail();
        productDetail.setQuantity(productDetail.getQuantity() + orderDetail.getQuantity());
        productDetailRepository.save(productDetail);
        orderDetailRepository.delete(orderDetail);
        recalculateOrder(order);
        return mapToDTO(order);
    }

    private void recalculateOrder(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        int totalQty = 0;
        BigDecimal totalMoney = BigDecimal.ZERO;
        for (OrderDetail detail : details) { totalQty += detail.getQuantity(); totalMoney = totalMoney.add(detail.getTotalPrice()); }
        order.setTotalQuantity(totalQty);
        order.setTotalMoney(totalMoney);
        BigDecimal shipping = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discount = order.getVoucherDiscountValue() != null ? order.getVoucherDiscountValue() : BigDecimal.ZERO;
        order.setFinalAmount(totalMoney.add(shipping).subtract(discount));
    }

    // ==========================================
    // TẦNG MAPPING DỮ LIỆU (Tối ưu điền đầy đủ trường)
    // ==========================================
    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCode(order.getCode());
        dto.setCreatedAt(order.getCreatedAt()); // Kế thừa từ BaseEntity
        dto.setUpdatedAt(order.getUpdatedAt());

        // Map Snapshot Info
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setConsigneeName(order.getConsigneeName());
        dto.setConsigneePhone(order.getConsigneePhone());
        dto.setConsigneeAddress(order.getConsigneeAddress());
        dto.setNote(order.getNote());

        // Map Money Flow
        dto.setTotalMoney(order.getTotalMoney());
        dto.setTotalQuantity(order.getTotalQuantity());
        dto.setShippingFee(order.getShippingFee());
        dto.setVoucherDiscountValue(order.getVoucherDiscountValue());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderType(order.getOrderType());

        // Check Null cho quan hệ thực thể để tránh NullPointerException (Khách vãng lai)
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        dto.setEmployeeId(order.getEmployee() != null ? order.getEmployee().getId() : null);
        dto.setVoucherId(order.getVoucher() != null ? order.getVoucher().getId() : null);

        return dto;
    }

    private OrderDetailDTO mapDetailToDTO(OrderDetail detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(detail.getId());
        dto.setOrderId(detail.getOrder().getId());
        dto.setProductDetailId(detail.getProductDetail().getId());
        dto.setPrice(detail.getPrice());
        dto.setQuantity(detail.getQuantity());
        dto.setTotalPrice(detail.getTotalPrice());

        // Đóng gói sâu thông tin Biến thể (Tên giày, màu, size) phục vụ hiển thị Data Table con
        if (detail.getProductDetail() != null) {
            ProductDetail pd = detail.getProductDetail();
            ProductDetailDTO pdDTO = new ProductDetailDTO();
            pdDTO.setId(pd.getId());
            pdDTO.setName(pd.getName());
            pdDTO.setPrice(pd.getPrice());
            pdDTO.setImage(pd.getImage());

            // Gắn thông tin màu/size lồng ghép trực tiếp
            if (pd.getColor() != null) {
                ColorDTO cDTO = new ColorDTO(); cDTO.setName(pd.getColor().getName());
                pdDTO.setColor(cDTO);
            }
            if (pd.getSize() != null) {
                SizeDTO sDTO = new SizeDTO(); sDTO.setName(pd.getSize().getName());
                pdDTO.setSize(sDTO);
            }
            dto.setProductDetail(pdDTO);
        }
        return dto;
    }
    @Transactional
    public OrderDTO updateOrderStatus(Integer orderId, Integer newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn mang ID: " + orderId));

        Integer currentStatus = order.getStatus();

        // Ghi nhận ID nhân viên đang thao tác
        Integer currentUserId = com.example.backend.config.SecurityUtils.getCurrentUserId();
        System.out.println("DEBUG - Current User ID từ SecurityUtils: " + currentUserId);
        if (currentUserId != null) {
            // Giả định nhân viên hoặc quản lý là người có quyền update trạng thái
            order.setEmployee(employeeRepository.getReferenceById(currentUserId));
        }
        // 1. CHẶN NẾU KHÔNG CÓ SỰ THAY ĐỔI
        if (currentStatus.equals(newStatus)) {
            return mapToDTO(order);
        }

        // 2. CHẶN NẾU ĐƠN HÀNG ĐÃ Ở TRẠNG THÁI ĐÍCH (ĐÃ HOÀN THÀNH HOẶC ĐÃ HỦY)
        if (currentStatus == 4 || currentStatus == 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng đã hoàn thành hoặc đã hủy, vĩnh viễn không thể đổi trạng thái!");
        }

        // 3. KIỂM TRA QUY TẮC DI CHUYỂN TRẠNG THÁI VÀ XỬ LÝ HOÀN KHO
        if (newStatus == 0) { // Nghiệp vụ HỦY ĐƠN
            // CHỈ CHO PHÉP HỦY KHI Ở TRẠNG THÁI CHỜ XÁC NHẬN (1) HOẶC ĐANG CHUẨN BỊ HÀNG (2)
            if (currentStatus != 1 && currentStatus != 2) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng đã giao cho đơn vị vận chuyển, không thể hủy!");
            }

            // QUY TRÌNH RESTOCK: Duyệt toàn bộ món hàng để cộng trả lại tồn kho vật lý
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail detail : details) {
                ProductDetail pd = detail.getProductDetail();
                if (pd != null) {
                    pd.setQuantity(pd.getQuantity() + detail.getQuantity());
                    productDetailRepository.save(pd);
                }
            }
        } else { // Luồng tịnh tiến thông thường (1 ➔ 2 ➔ 3 ➔ 4)
            if (newStatus == 2 && currentStatus != 1) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Lỗi logic: Đơn phải ở trạng thái Chờ xác nhận mới có thể Chuẩn bị hàng!");
            }
            if (newStatus == 3 && currentStatus != 2) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Lỗi logic: Đơn phải ở trạng thái Đang chuẩn bị mới có thể Giao hàng!");
            }
            if (newStatus == 4 && currentStatus != 3) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Lỗi logic: Đơn phải ở trạng thái Đang giao mới có thể bấm Hoàn thành!");
            }
        }

        // 4. LƯU TRẠNG THÁI MỚI VÀ ĐỒNG BỘ DATA
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return mapToDTO(updatedOrder);
    }
    // 2. CẬP NHẬT HÀM TẠO HÓA ĐƠN
    @Transactional
    public OrderDTO createOrderLogin(OrderDTO dto, List<OrderDetailDTO> detailDTOs) {
        Order order = new Order();
        order.setCode(sequenceGeneratorService.generateCode(CodeType.ORDER));
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerPhone(dto.getCustomerPhone());
        order.setConsigneeName(dto.getConsigneeName());
        order.setConsigneePhone(dto.getConsigneePhone());
        order.setConsigneeAddress(dto.getConsigneeAddress());
        order.setShippingFee(dto.getShippingFee() != null ? dto.getShippingFee() : BigDecimal.ZERO);
        order.setVoucherDiscountValue(dto.getVoucherDiscountValue() != null ? dto.getVoucherDiscountValue() : BigDecimal.ZERO);
        order.setStatus(1);
        order.setOrderType("ONLINE");
        order.setTotalMoney(BigDecimal.ZERO);
        order.setTotalQuantity(0);
        order.setFinalAmount(BigDecimal.ZERO);

        // BỔ SUNG: Lấy ID và Role từ SecurityUtils
        Integer currentUserId = com.example.backend.config.SecurityUtils.getCurrentUserId();
        String role = com.example.backend.config.SecurityUtils.getCurrentUserRole();

        if (currentUserId != null) {
            if (role != null && role.contains("CUSTOMER")) {
                customerRepository.findById(currentUserId).ifPresent(order::setCustomer);
            } else {
                employeeRepository.findById(currentUserId).ifPresent(order::setEmployee);
            }
        }

        Order savedOrder = orderRepository.save(order);
        System.out.println("DEBUG - Order trước khi SAVE: " + (order.getCustomer() != null ? "Customer ID: " + order.getCustomer().getId() : "Customer is NULL"));
        for (OrderDetailDTO detailDTO : detailDTOs) {
            addOrUpdateOrderDetail(savedOrder.getId(), detailDTO);
        }
        return mapToDTO(orderRepository.findById(savedOrder.getId()).orElseThrow());
    }
    @Transactional
    public OrderDTO createDraftOrder() {
        Order order = new Order();
        order.setStatus(0);
        order.setOrderType("POS");
        order.setTotalMoney(BigDecimal.ZERO);
        order.setFinalAmount(BigDecimal.ZERO);
        order.setTotalQuantity(0);
        order.setCode("POS-" + System.currentTimeMillis());

        Integer currentUserId = com.example.backend.config.SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            // Lấy thẳng đối tượng Employee từ DB để có FirstName và LastName
            employeeRepository.findById(currentUserId).ifPresent(emp -> {
                order.setEmployee(emp);
                // QUAN TRỌNG: Lưu snapshot thông tin nhân viên tại thời điểm tạo đơn
                order.setEmployeeCode(emp.getCode());
                order.setEmployeeName(emp.getLastName() + " " + emp.getFirstName());
            });
        }

        Order savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }
    @Transactional
    public OrderDTO posCheckout(Integer orderId, POSCheckoutDTO checkoutData) {
        // 1. Lấy đơn hàng và kiểm tra
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        if (order.getStatus() != 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng này đã thanh toán hoặc không hợp lệ");
        }

        // 2. Lấy danh sách chi tiết đơn hàng
        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        if (details.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng trống, không thể thanh toán");
        }

        // 3. LOGIC TRỪ TỒN KHO (QUAN TRỌNG)
        for (OrderDetail detail : details) {
            ProductDetail productDetail = productDetailRepository.findById(detail.getProductDetail().getId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));

            if (productDetail.getQuantity() < detail.getQuantity()) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        "Sản phẩm " + productDetail.getName() + " không đủ số lượng tồn kho!");
            }

            // Trừ kho
            productDetail.setQuantity(productDetail.getQuantity() - detail.getQuantity());
            productDetailRepository.save(productDetail);
        }

        // 4. Cập nhật thông tin thanh toán (Từ POSCheckoutDTO)
        order.setCustomerName(checkoutData.getCustomerName()); // Có thể là khách vãng lai
        order.setNote(checkoutData.getNote());// Số tiền khách trả
        order.setStatus(2); // Cập nhật trạng thái Hoàn thành

        return mapToDTO(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getPosDraftOrders() {
        // Truy vấn: order_type = "POS" và status = 0
        return orderRepository.findByOrderTypeAndStatusOrderByCreatedAtDesc("POS", 0)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
}