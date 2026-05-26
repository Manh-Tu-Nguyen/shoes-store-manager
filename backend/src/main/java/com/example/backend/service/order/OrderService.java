package com.example.backend.service.order;

import com.example.backend.dto.order.*;
import com.example.backend.dto.product.*;
import com.example.backend.entity.code.CodeType;
import com.example.backend.entity.order.*;
import com.example.backend.entity.product.ProductDetail;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.EmployeeRepository;
import com.example.backend.repository.auth.CustomerRepository;
import com.example.backend.repository.order.*;
import com.example.backend.repository.product.ProductDetailRepository;
import com.example.backend.service.code.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    // BỔ SUNG REPO GHI VẾT LỊCH SỬ
    private final OrderStatusHistoryRepository historyRepository;

    // =========================================================================
    // HÀM PRIVATE: ĐỘNG CƠ GHI VẾT LỊCH SỬ (AUDIT TRAIL ENGINE)
    // =========================================================================
    private void logOrderStatusChange(Order order, Integer oldStatus, Integer newStatus, String note) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setNote(note);

        // Tự động định danh người thao tác (nếu có Token)
        Integer currentUserId = com.example.backend.config.SecurityUtils.getCurrentUserId();
        String currentRole = com.example.backend.config.SecurityUtils.getCurrentUserRole();

        // Chỉ gán ID Employee nếu người thao tác là Admin/Staff. Nếu là Customer hoặc hệ thống thì để Null.
        if (currentUserId != null && currentRole != null && !currentRole.contains("CUSTOMER")) {
            history.setEmployee(employeeRepository.getReferenceById(currentUserId));
        }

        historyRepository.save(history);
    }

    // =========================================================================
    // TRUY VẤN LỊCH SỬ CHO FRONTEND
    // =========================================================================
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryDTO> getOrderTimeline(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn ID: " + orderId);
        }
        return historyRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream().map(h -> {
            OrderStatusHistoryDTO dto = new OrderStatusHistoryDTO();
            dto.setId(h.getId());
            dto.setOrderId(h.getOrder().getId());
            dto.setOldStatus(h.getOldStatus());
            dto.setNewStatus(h.getNewStatus());
            dto.setNote(h.getNote());
            dto.setCreatedAt(h.getCreatedAt());
            if (h.getEmployee() != null) {
                dto.setEmployeeId(h.getEmployee().getId());
                dto.setEmployeeName(h.getEmployee().getLastName() + " " + h.getEmployee().getFirstName());
            }
            return dto;
        }).toList();
    }

    // [GIỮ NGUYÊN CÁC HÀM GET DỮ LIỆU VÀ RECALCULATE NHƯ CŨ...]
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(this::mapToDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDetailDTO> getOrderDetails(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn");
        }
        return orderDetailRepository.findByOrderId(orderId)
                .stream().map(this::mapDetailToDTO).toList();
    }

    private void recalculateOrder(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        int totalQty = 0;
        BigDecimal totalMoney = BigDecimal.ZERO;
        for (OrderDetail detail : details) {
            totalQty += detail.getQuantity();
            totalMoney = totalMoney.add(detail.getTotalPrice());
        }
        order.setTotalQuantity(totalQty);
        order.setTotalMoney(totalMoney);
        BigDecimal shipping = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discount = order.getVoucherDiscountValue() != null ? order.getVoucherDiscountValue() : BigDecimal.ZERO;
        order.setFinalAmount(totalMoney.add(shipping).subtract(discount));
    }


    // =========================================================================
    // PHẦN 2: PHÂN HỆ POS
    // =========================================================================

    @Transactional(readOnly = true)
    public List<OrderDTO> getPosDraftOrders() {
        return orderRepository.findByOrderTypeAndStatusOrderByCreatedAtDesc("POS", 1)
                .stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public OrderDTO createDraftOrder() {
        Order order = new Order();
        order.setStatus(1);
        order.setOrderType("POS");
        order.setTotalMoney(BigDecimal.ZERO);
        order.setFinalAmount(BigDecimal.ZERO);
        order.setTotalQuantity(0);
        order.setCode("POS-" + System.currentTimeMillis());

        Integer currentUserId = com.example.backend.config.SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            employeeRepository.findById(currentUserId).ifPresent(emp -> {
                order.setEmployee(emp);
                order.setEmployeeCode(emp.getCode());
                order.setEmployeeName(emp.getLastName() + " " + emp.getFirstName());
            });
        }
        Order savedOrder = orderRepository.save(order);

        // GHI VẾT
        logOrderStatusChange(savedOrder, null, 1, "Khởi tạo hóa đơn nháp tại quầy");
        return mapToDTO(savedOrder);
    }

    @Transactional
    public void deleteDraftOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        if (!"POS".equals(order.getOrderType()) || order.getStatus() != 1) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Hệ thống chỉ cho phép xóa bỏ hóa đơn nháp tại quầy!");
        }

        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        for (OrderDetail detail : details) {
            ProductDetail pd = detail.getProductDetail();
            if (pd != null) {
                pd.setQuantity(pd.getQuantity() + detail.getQuantity());
                productDetailRepository.save(pd);
            }
        }
        orderDetailRepository.deleteAll(details);
        orderRepository.delete(order);
    }

    @Transactional
    public OrderDTO addOrderDetailUpdateOrderDraft(Integer orderId, OrderDetailDTO detailDTO) {
        // [GIỮ NGUYÊN LOGIC NHƯ BẠN VIẾT]
        Order order = orderRepository.findWithRelationsById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn POS"));

        if (order.getStatus() != 1 || !"POS".equals(order.getOrderType())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Chỉ phục vụ chỉnh sửa hóa đơn nháp tại quầy.");
        }

        ProductDetail productDetail = productDetailRepository.findById(detailDTO.getProductDetailId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại!"));

        Optional<OrderDetail> existingDetailOpt = orderDetailRepository.findByOrderIdAndProductDetailId(orderId, productDetail.getId());

        int oldQuantity = 0;
        OrderDetail orderDetail;

        if (existingDetailOpt.isPresent()) {
            orderDetail = existingDetailOpt.get();
            oldQuantity = orderDetail.getQuantity();
        } else {
            orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setPrice(productDetail.getPrice());
        }

        int newQuantity = detailDTO.getQuantity();
        int delta = newQuantity - oldQuantity;

        if (delta > 0 && productDetail.getQuantity() < delta) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Sản phẩm " + productDetail.getName() + " không đủ số lượng!");
        }
        productDetail.setQuantity(productDetail.getQuantity() - delta);
        productDetailRepository.save(productDetail);

        orderDetail.setQuantity(newQuantity);
        orderDetail.setTotalPrice(orderDetail.getPrice().multiply(new BigDecimal(newQuantity)));
        orderDetailRepository.save(orderDetail);

        recalculateOrder(order);
        return mapToDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO removePosOrderDetail(Integer orderId, Integer orderDetailId) {
        // [GIỮ NGUYÊN NHƯ BẠN VIẾT]
        Order order = orderRepository.findWithRelationsById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        if (order.getStatus() != 1 || !"POS".equals(order.getOrderType())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Chỉ được xóa sản phẩm khỏi hóa đơn nháp.");
        }

        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết hóa đơn"));

        ProductDetail productDetail = orderDetail.getProductDetail();
        if (productDetail != null) {
            productDetail.setQuantity(productDetail.getQuantity() + orderDetail.getQuantity());
            productDetailRepository.save(productDetail);
        }

        orderDetailRepository.delete(orderDetail);
        recalculateOrder(order);
        return mapToDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO posCheckout(Integer orderId, POSCheckoutDTO checkoutData) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        if (order.getStatus() != 1) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng này đã chốt hoặc không hợp lệ");
        }

        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        if (details.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng trống, không thể thanh toán");
        }

        order.setCustomerName(checkoutData.getCustomerName());
        order.setNote(checkoutData.getNote());

        if ("SHIPPING".equals(checkoutData.getDeliveryType())) {
            order.setConsigneeAddress(checkoutData.getShippingAddress());
            order.setShippingFee(checkoutData.getShippingFee() != null ? checkoutData.getShippingFee() : new BigDecimal("30000"));
        } else {
            order.setConsigneeAddress("Nhận tại quầy");
            order.setShippingFee(BigDecimal.ZERO);
        }

        recalculateOrder(order);

        boolean isCash = "CASH".equals(checkoutData.getPaymentMethod());
        int targetStatus = isCash ? 4 : 1;

        order.setStatus(targetStatus);
        Order savedOrder = orderRepository.save(order);

        // GHI VẾT
        logOrderStatusChange(savedOrder, 1, targetStatus, isCash ? "Thanh toán thành công tại quầy bằng Tiền mặt" : "Chờ khách hàng quét mã VNPAY");

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(savedOrder.getFinalAmount());
        payment.setPaymentMethod(isCash ? 0 : 1);
        payment.setStatus(isCash ? 1 : 0);
        payment.setAmountTendered(checkoutData.getAmountTendered() != null ? checkoutData.getAmountTendered() : savedOrder.getFinalAmount());
        payment.setChangeAmount(checkoutData.getChangeAmount() != null ? checkoutData.getChangeAmount() : BigDecimal.ZERO);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setNote(checkoutData.getNote());
        paymentRepository.save(payment);

        return mapToDTO(savedOrder);
    }

    // =========================================================================
    // PHẦN 3: PHÂN HỆ ONLINE
    // =========================================================================

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
        order.setStatus(1); // 1 = Chờ xác nhận
        order.setOrderType("ONLINE");
        order.setTotalMoney(BigDecimal.ZERO);
        order.setTotalQuantity(0);
        order.setFinalAmount(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);
        for (OrderDetailDTO detailDTO : detailDTOs) {
            addOrUpdateOrderDetail(savedOrder.getId(), detailDTO);
        }

        // GHI VẾT
        logOrderStatusChange(savedOrder, null, 1, "Khách Vãng Lai đặt hàng thành công");
        return mapToDTO(orderRepository.findById(savedOrder.getId()).get());
    }

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
        for (OrderDetailDTO detailDTO : detailDTOs) {
            addOrUpdateOrderDetail(savedOrder.getId(), detailDTO);
        }

        // GHI VẾT
        logOrderStatusChange(savedOrder, null, 1, "Tài khoản thành viên đặt hàng thành công");
        return mapToDTO(orderRepository.findById(savedOrder.getId()).orElseThrow());
    }

    @Transactional
    public OrderDTO addOrUpdateOrderDetail(Integer orderId, OrderDetailDTO detailDTO) {
        // [GIỮ NGUYÊN CODE CỦA BẠN]
        Order order = orderRepository.findWithRelationsById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));

        if (order.getStatus() != 1) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Không thể sửa hóa đơn đã chốt hoặc đã hủy");
        }

        ProductDetail productDetail = productDetailRepository.findById(detailDTO.getProductDetailId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        Optional<OrderDetail> existingDetailOpt = orderDetailRepository.findByOrderIdAndProductDetailId(orderId, productDetail.getId());

        int oldQuantity = 0;
        OrderDetail orderDetail;
        if (existingDetailOpt.isPresent()) {
            orderDetail = existingDetailOpt.get();
            oldQuantity = orderDetail.getQuantity();
        } else {
            orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setPrice(productDetail.getPrice());
        }

        int newQuantity = detailDTO.getQuantity();
        int delta = newQuantity - oldQuantity;

        if (delta > 0 && productDetail.getQuantity() < delta) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Sản phẩm " + productDetail.getName() + " không đủ số lượng!");
        }

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
        // [GIỮ NGUYÊN CODE CỦA BẠN]
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"));
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết hóa đơn"));

        ProductDetail productDetail = orderDetail.getProductDetail();
        productDetail.setQuantity(productDetail.getQuantity() + orderDetail.getQuantity());
        productDetailRepository.save(productDetail);

        orderDetailRepository.delete(orderDetail);
        recalculateOrder(order);
        return mapToDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Integer orderId, Integer newStatus, String customNote) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn mang ID: " + orderId));

        Integer currentStatus = order.getStatus();
        Integer currentUserId = com.example.backend.config.SecurityUtils.getCurrentUserId();

        if (currentUserId != null) {
            order.setEmployee(employeeRepository.getReferenceById(currentUserId));
        }

        if (currentStatus.equals(newStatus)) return mapToDTO(order);

        if (currentStatus == 4 || currentStatus == 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng đã hoàn thành hoặc đã hủy, vĩnh viễn không thể đổi trạng thái!");
        }

        if (newStatus == 0) {
            if (currentStatus != 1 && currentStatus != 2) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Đơn hàng đã giao cho đơn vị vận chuyển, không thể hủy!");
            }
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail detail : details) {
                ProductDetail pd = detail.getProductDetail();
                if (pd != null) {
                    pd.setQuantity(pd.getQuantity() + detail.getQuantity());
                    productDetailRepository.save(pd);
                }
            }
        } else {
            if (newStatus == 2 && currentStatus != 1) throw new AppException(HttpStatus.BAD_REQUEST, "Lỗi logic: Đơn phải ở trạng thái Chờ xác nhận mới có thể Chuẩn bị hàng!");
            if (newStatus == 3 && currentStatus != 2) throw new AppException(HttpStatus.BAD_REQUEST, "Lỗi logic: Đơn phải ở trạng thái Đang chuẩn bị mới có thể Giao hàng!");
            if (newStatus == 4 && currentStatus != 3) throw new AppException(HttpStatus.BAD_REQUEST, "Lỗi logic: Đơn phải ở trạng thái Đang giao mới có thể bấm Hoàn thành!");
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        // GHI VẾT
        String defaultNote = newStatus == 0 ? "Hủy đơn hàng" : "Cập nhật tiến trình đơn hàng";
        logOrderStatusChange(savedOrder, currentStatus, newStatus, customNote != null ? customNote : defaultNote);

        return mapToDTO(savedOrder);
    }

    // =========================================================================
    // MAPPER & IPN
    // =========================================================================

    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCode(order.getCode());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setConsigneeName(order.getConsigneeName());
        dto.setConsigneePhone(order.getConsigneePhone());
        dto.setConsigneeAddress(order.getConsigneeAddress());
        dto.setNote(order.getNote());
        dto.setTotalMoney(order.getTotalMoney());
        dto.setTotalQuantity(order.getTotalQuantity());
        dto.setShippingFee(order.getShippingFee());
        dto.setVoucherDiscountValue(order.getVoucherDiscountValue());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderType(order.getOrderType());
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

        if (detail.getProductDetail() != null) {
            ProductDetail pd = detail.getProductDetail();
            ProductDetailDTO pdDTO = new ProductDetailDTO();
            pdDTO.setId(pd.getId());
            pdDTO.setName(pd.getName());
            pdDTO.setPrice(pd.getPrice());
            pdDTO.setImage(pd.getImage());
            pdDTO.setCode(pd.getCode());

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
    public void processVNPAYIPN(String orderCode, String responseCode, BigDecimal vnpAmount, String transactionNo) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Order not found"));

        BigDecimal expectedAmount = order.getFinalAmount().multiply(new BigDecimal("100"));
        if (expectedAmount.compareTo(vnpAmount) != 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid Amount");
        }

        if (order.getStatus() == 4) return;

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Payment ledger not found"));

        Integer currentStatus = order.getStatus();

        if ("00".equals(responseCode)) {
            order.setStatus(4);
            payment.setStatus(1);
            payment.setTransactionCode(transactionNo);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setNote("VNPAY IPN: Thanh toán trực tuyến thành công");

            // GHI VẾT HỆ THỐNG
            logOrderStatusChange(order, currentStatus, 4, "Hệ thống VNPAY IPN: Xác nhận thanh toán thành công");
        } else {
            order.setStatus(0);
            payment.setStatus(0);
            payment.setNote("VNPAY IPN: Giao dịch thất bại hoặc bị hủy, Mã lỗi: " + responseCode);

            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
            for (OrderDetail detail : details) {
                ProductDetail pd = detail.getProductDetail();
                if (pd != null) {
                    pd.setQuantity(pd.getQuantity() + detail.getQuantity());
                    productDetailRepository.save(pd);
                }
            }
            // GHI VẾT HỆ THỐNG
            logOrderStatusChange(order, currentStatus, 0, "Hệ thống VNPAY IPN: Khách hàng hủy giao dịch hoặc lỗi thanh toán");
        }

        orderRepository.save(order);
        paymentRepository.save(payment);
    }
}