USE master;
GO

-- Kiểm tra và xóa DB cũ để làm sạch môi trường
IF EXISTS (SELECT 1 FROM sys.databases WHERE name = 'SHOES_STORE_DB')
BEGIN
    ALTER DATABASE SHOES_STORE_DB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE SHOES_STORE_DB;
END
GO

CREATE DATABASE SHOES_STORE_DB;
GO
USE SHOES_STORE_DB;
GO

-- ======================================================================================
-- PHẦN 1: TẠO BẢNG & GIẢI THÍCH CHI TIẾT (ANNOTATED SCHEMA)
-- ======================================================================================

-- 1.1. NHÓM PHÂN QUYỀN & NHÂN VIÊN
-- --------------------------------------------------------------------------------------
CREATE TABLE role(
    id INT IDENTITY(1,1) PRIMARY KEY, -- Khóa chính tự tăng (1, 2, 3...)
    name NVARCHAR(100) NOT NULL       -- Tên quyền (Admin, Staff, Warehouse). Dùng NVARCHAR để lưu tiếng Việt có dấu.
);

CREATE TABLE work_shift(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL, -- Tên ca (Ca Sáng, Ca Chiều)
    start_time TIME NOT NULL,    -- Giờ bắt đầu (VD: 08:00:00) - Chỉ lưu giờ phút, không lưu ngày
    end_time TIME NOT NULL       -- Giờ kết thúc
);

CREATE TABLE employee(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_workshift INT NOT NULL, -- Khóa ngoại trỏ sang bảng ca làm việc
    id_role INT NOT NULL,      -- Khóa ngoại trỏ sang bảng quyền
    
    code VARCHAR(50) UNIQUE NOT NULL, -- Mã nhân viên nghiệp vụ (NV001). UNIQUE để không bị trùng.
    image VARCHAR(MAX),               -- Link ảnh đại diện (Lưu đường dẫn URL chứ không lưu file ảnh trực tiếp vào DB cho nhẹ)
    
    last_name NVARCHAR(100) NOT NULL,  -- Họ (Nguyễn)
    first_name NVARCHAR(100) NOT NULL, -- Tên (Văn A)
    email VARCHAR(100) UNIQUE NOT NULL, -- Email đăng nhập/liên hệ (Không trùng)
    phone_number VARCHAR(15) NOT NULL,
    
    -- [BIT]: Kiểu dữ liệu chỉ lưu 0 hoặc 1. 
    -- Quy ước: 1 = Nam (Male), 0 = Nữ (Female)
    gender BIT NOT NULL,      
    
    birthday DATE NOT NULL,             -- Ngày sinh (YYYY-MM-DD)
    account VARCHAR(100) UNIQUE NOT NULL, -- Tài khoản đăng nhập hệ thống
    password VARCHAR(255) NOT NULL,       -- Mật khẩu đã mã hóa (Dùng BCrypt ra chuỗi dài loằng ngoằng, không lưu text thường)
    
    -- [DECIMAL(19, 2)]: Chuẩn lưu tiền tệ trong Banking.
    -- 19: Tổng số chữ số. 2: Số chữ số sau dấu phẩy. 
    -- VD: 15000000.00 (Độ chính xác cao hơn FLOAT/DOUBLE)
    salary DECIMAL(19, 2) NOT NULL,
    
    -- [AUDIT LOGS]: Hai cột này bắt buộc có ở mọi bảng để truy vết
    create_at DATETIME DEFAULT GETDATE(), -- Ngày tạo bản ghi (Tự động lấy giờ hiện tại)
    updated_at DATETIME DEFAULT GETDATE(), -- Ngày cập nhật gần nhất
    
    -- [STATUS - BIT]: Trạng thái đơn giản (On/Off)
    -- Quy ước: 1 = Đang làm việc (Active), 0 = Đã nghỉ việc/Khóa (Inactive)
    status BIT NOT NULL,      
    
    FOREIGN KEY (id_workshift) REFERENCES work_shift(id),
    FOREIGN KEY (id_role) REFERENCES role(id)
);

-- 1.2. NHÓM KHÁCH HÀNG
-- --------------------------------------------------------------------------------------
CREATE TABLE customer(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- Mã khách hàng (KH001)
    image VARCHAR(MAX),
    last_name NVARCHAR(100) NOT NULL,
    first_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE, -- Có thể Null (nếu khách mua tại quầy không cung cấp)
    phone_number VARCHAR(15),
    
    -- Nullable (Cho phép rỗng) vì khách vãng lai mua nhanh không cần khai báo
    gender BIT,                
    birthday DATE,
    
    -- Tài khoản đăng nhập App/Web. Null nếu khách mua tại quầy không đăng ký.
    account VARCHAR(100) UNIQUE, 
    password VARCHAR(255),
    
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    -- Quy ước: 1 = Hoạt động, 0 = Bị chặn (Block - do bom hàng hoặc spam)
    status BIT NOT NULL        
);

CREATE TABLE address(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT NOT NULL,            -- Địa chỉ này của ông khách nào?
    consignee_name NVARCHAR(255) NOT NULL, -- Tên người nhận hàng (Có thể khác tên chủ tài khoản)
    consignee_phone VARCHAR(15) NOT NULL,  -- SĐT người nhận
    city NVARCHAR(100) NOT NULL,         -- Tỉnh/Thành phố
    ward NVARCHAR(100) NOT NULL,         -- Phường/Xã
    street_detail NVARCHAR(255) NOT NULL, -- Số nhà, tên đường
    note NVARCHAR(MAX),                  -- Ghi chú (VD: Giao giờ hành chính)
    
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    FOREIGN KEY (id_customer) REFERENCES customer(id)
);

-- 1.3. NHÓM SẢN PHẨM (CORE)
-- --------------------------------------------------------------------------------------
-- Các bảng thuộc tính (Brand, Category, Origin, Size, Color) cấu trúc giống nhau
CREATE TABLE brand(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- Mã code để dev gọi (VD: B_NIKE)
    name NVARCHAR(100) NOT NULL,      -- Tên hiển thị (Nike)
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL -- 1: Hiển thị, 0: Ẩn tạm thời (Hết mùa vụ)
);

CREATE TABLE category(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL
);

CREATE TABLE origin(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL
);

CREATE TABLE size(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL, -- Tên size (39, 40, XL, L)
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL
);

CREATE TABLE color(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL, -- Tên màu (Xanh, Đỏ, Tím)
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL
);

-- Bảng SẢN PHẨM CHA (Product Parent)
-- Đại diện cho dòng sản phẩm chung (VD: Nike Air Zoom)
CREATE TABLE product(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_brand INT NOT NULL,
    id_category INT NOT NULL,
    id_origin INT NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL, -- Mã sản phẩm cha (P001)
    name NVARCHAR(255) NOT NULL,      -- Tên chung
    image VARCHAR(MAX),
    
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    -- Quy ước: 1 = Đang kinh doanh, 0 = Ngừng kinh doanh (Stop Selling)
    status BIT NOT NULL,              
    
    FOREIGN KEY (id_brand) REFERENCES brand(id),
    FOREIGN KEY (id_category) REFERENCES category(id),
    FOREIGN KEY (id_origin) REFERENCES origin(id),
);

-- Bảng CHI TIẾT SẢN PHẨM (Product Detail / SKU)
-- Đại diện cho từng biến thể cụ thể (VD: Nike Air Zoom - Đỏ - Size 39)
CREATE TABLE product_detail(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_product INT NOT NULL, -- Thuộc dòng sản phẩm nào?
    id_color INT NOT NULL,   -- Màu gì?
    id_size INT NOT NULL,    -- Size gì?
    
    code VARCHAR(50) UNIQUE NOT NULL, -- SKU Code (VD: P001-RED-39). Đây là mã quét vạch.
    name NVARCHAR(255) NOT NULL,      -- Tên chi tiết (Nike Air Zoom Đỏ 39)
    image VARCHAR(MAX),               -- Ảnh riêng của biến thể màu này
    price DECIMAL(19, 2) NOT NULL,    -- Giá bán hiện tại
    quantity INT NOT NULL,            -- Số lượng tồn kho thực tế
    
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    -- Quy ước: 1 = Còn hàng/Đang bán, 0 = Tạm ẩn/Hết hàng
    status BIT NOT NULL,              
    
    FOREIGN KEY (id_product) REFERENCES product(id),
    FOREIGN KEY (id_color) REFERENCES color(id),
    FOREIGN KEY (id_size) REFERENCES size(id),
);

-- 1.4. NHÓM KHUYẾN MÃI
-- --------------------------------------------------------------------------------------
CREATE TABLE voucher(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- Mã nhập (SALE10)
    name NVARCHAR(100) NOT NULL,
    min_order_value DECIMAL(19, 2) NOT NULL, -- Đơn tối thiểu 500k mới được dùng
    max_discount_value DECIMAL(19, 2) NOT NULL, -- Giảm tối đa 50k (để tránh lỗ khi giảm %)
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    
    value DECIMAL(19, 2) NOT NULL,    -- Giá trị giảm (VD: 10 hoặc 50000)
    quantity INT NOT NULL,            -- Số lượng voucher phát hành
    
    -- [TYPE - BIT]: Loại giảm giá
    -- Quy ước: 0 = Giảm tiền mặt (VND), 1 = Giảm phần trăm (%)
    type BIT NOT NULL,                
    
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    -- Quy ước: 1 = Đang diễn ra, 0 = Đã kết thúc/Hủy
    status BIT NOT NULL                
);

CREATE TABLE promotion(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,      -- Tên đợt KM (Black Friday)
    value DECIMAL(19, 2) NOT NULL,    -- % Giảm giá cho đợt này
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL
);

-- Bảng trung gian: Một sản phẩm có thể thuộc đợt khuyến mãi này
CREATE TABLE product_promotion(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_product INT NOT NULL,
    id_promotion INT NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_product) REFERENCES product(id),
    FOREIGN KEY (id_promotion) REFERENCES promotion(id)
);

-- 1.5. NHÓM ĐƠN HÀNG & THANH TOÁN (QUAN TRỌNG NHẤT)
-- --------------------------------------------------------------------------------------
CREATE TABLE orders(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT,   -- Null nếu khách vãng lai (Guest) mua tại quầy không cần tạo acc
    id_employee INT,   -- Null nếu khách tự đặt Online (không có nhân viên tạo giúp)
    id_voucher INT,    -- Null nếu không áp mã giảm giá
    code VARCHAR(50) UNIQUE NOT NULL, -- Mã đơn hàng (ORD-260120-001)
    
    -- [SNAPSHOT INFO]: Lưu chết thông tin tại thời điểm mua.
    -- Tại sao? Vì nếu sau này nhân viên đổi tên, khách đổi sđt, thì hóa đơn cũ KHÔNG ĐƯỢC thay đổi.
    employee_code VARCHAR(50),
    employee_name NVARCHAR(255),
    customer_name NVARCHAR(255),
    customer_phone VARCHAR(15),
    consignee_name NVARCHAR(255),    -- Người nhận hàng thực tế
    consignee_phone VARCHAR(15),
    consignee_address NVARCHAR(MAX),
    
    -- [MONEY FLOW]: Luồng tiền
    total_money DECIMAL(19, 2) NOT NULL, -- Tổng tiền hàng (Chưa trừ gì cả)
    total_quantity INT NOT NULL,         -- Tổng số lượng SP
    voucher_discount_value DECIMAL(19, 2), -- Tiền được giảm nhờ Voucher
    shipping_fee DECIMAL(19, 2),           -- Phí ship
    final_amount DECIMAL(19, 2) NOT NULL,  -- KHÁCH PHẢI TRẢ (= Total - Voucher + Ship)
    
    create_at DATETIME DEFAULT GETDATE(),  -- Thời điểm đặt hàng
    updated_at DATETIME DEFAULT GETDATE(), -- Thời điểm cập nhật trạng thái
    note NVARCHAR(MAX),                    -- Ghi chú của khách (Giao giờ HC)
    
    -- [STATUS - INT]: Trạng thái đơn hàng (Quy trình nghiệp vụ)
    -- Dùng INT vì có nhiều trạng thái phức tạp, không chỉ On/Off
    -- 0: Chờ xác nhận (Pending) - Mới đặt
    -- 1: Đã xác nhận (Confirmed) - Đã gọi điện/duyệt, trừ kho
    -- 2: Đang giao hàng (Shipping) - Đã đưa cho shipper
    -- 3: Hoàn thành (Completed) - Khách đã nhận và trả tiền
    -- 4: Đã hủy (Cancelled) - Khách hủy hoặc Shop hủy
    -- 5: Trả hàng/Hoàn tiền (Returned) - Khách đổi trả
    status INT NOT NULL, 
    
    FOREIGN KEY (id_customer) REFERENCES customer(id),
    FOREIGN KEY (id_employee) REFERENCES employee(id),
    FOREIGN KEY (id_voucher) REFERENCES voucher(id)
);

CREATE TABLE order_detail(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_order INT NOT NULL,
    id_product_detail INT NOT NULL, -- Mua con SKU nào?
    price DECIMAL(19, 2) NOT NULL,  -- Giá bán TẠI THỜI ĐIỂM MUA (Quan trọng để không bị đổi giá sau này)
    quantity INT NOT NULL,          -- Số lượng mua
    total_price DECIMAL(19, 2),     -- = price * quantity
    
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    FOREIGN KEY (id_order) REFERENCES orders(id),
    FOREIGN KEY (id_product_detail) REFERENCES product_detail(id),
);

CREATE TABLE payment(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_order INT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL, -- Số tiền giao dịch
    
    -- [PAYMENT METHOD - INT]
    -- 0: Tiền mặt (Cash / COD)
    -- 1: Chuyển khoản (Banking / VNPAY)
    payment_method INT NOT NULL, 
    
    -- [PAYMENT STATUS - INT]
    -- 0: Chưa thanh toán (Unpaid)
    -- 1: Đã thanh toán (Paid)
    -- 2: Hoàn tiền (Refunded)
    status INT NOT NULL,         
    
    transaction_code VARCHAR(100), -- Mã giao dịch ngân hàng (nếu có)
    payment_date DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    note NVARCHAR(MAX),
    FOREIGN KEY (id_order) REFERENCES orders(id)
);

-- 1.6. NHÓM LỊCH SỬ (AUDIT LOG)
-- --------------------------------------------------------------------------------------
CREATE TABLE order_history(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_order INT NOT NULL,
    id_employee INT,          -- Ai là người thao tác? (Null nếu hệ thống tự chạy)
    action VARCHAR(100),      -- Hành động: 'Change Status', 'Update Address'
    column_name NVARCHAR(100),-- Cột bị sửa
    before_val NVARCHAR(MAX), -- Giá trị cũ
    after_val NVARCHAR(MAX),  -- Giá trị mới
    create_at DATETIME DEFAULT GETDATE(), -- Thời điểm sửa
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_order) REFERENCES orders(id),
    FOREIGN KEY (id_employee) REFERENCES employee(id)
);

-- 1.7. NHÓM GIỎ HÀNG (SHOPPING CART)
-- --------------------------------------------------------------------------------------
CREATE TABLE cart(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT UNIQUE, -- Ràng buộc 1-1: Mỗi khách hàng chỉ có duy nhất 1 giỏ hàng
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_customer) REFERENCES customer(id)
);

CREATE TABLE cart_detail(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_cart INT NOT NULL,
    id_product_detail INT NOT NULL,
    quantity INT NOT NULL, -- Số lượng muốn mua
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    FOREIGN KEY (id_cart) REFERENCES cart(id),
    FOREIGN KEY (id_product_detail) REFERENCES product_detail(id),
    
    -- [CONSTRAINT]: Ràng buộc duy nhất
    -- Trong 1 giỏ hàng, 1 sản phẩm (SKU) chỉ được xuất hiện 1 dòng. 
    -- Nếu thêm tiếp thì cộng dồn số lượng (Update Quantity) chứ không thêm dòng mới.
    CONSTRAINT UQ_Cart_Product UNIQUE (id_cart, id_product_detail)
);

-- 1.8. NHÓM ĐÁNH GIÁ (PRODUCT REVIEW)
-- --------------------------------------------------------------------------------------
CREATE TABLE product_review(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT NOT NULL,
    id_product INT NOT NULL, -- Đánh giá theo Dòng sản phẩm (Parent), không theo SKU
    id_order INT,            -- Để hiển thị nhãn "Đã mua hàng" (Verified Purchase)
    
    -- Ràng buộc check: Số sao chỉ được từ 1 đến 5
    rating INT CHECK (rating >= 1 AND rating <= 5) NOT NULL,
    comment NVARCHAR(MAX),   -- Nội dung
    image VARCHAR(MAX),      -- Ảnh review
    
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    -- Quy ước: 1 = Hiển thị, 0 = Ẩn (Do vi phạm tiêu chuẩn cộng đồng)
    status BIT NOT NULL,     
    
    FOREIGN KEY (id_customer) REFERENCES customer(id),
    FOREIGN KEY (id_product) REFERENCES product(id),
    FOREIGN KEY (id_order) REFERENCES orders(id)
);