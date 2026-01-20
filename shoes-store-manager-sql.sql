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

USE SHOES_STORE_DB;
GO

-- ======================================================================================
-- PHẦN 2: INSERT DATA (DATA SEEDING)
-- ======================================================================================

-- 1. NHÓM DANH MỤC (LOOKUP TABLES) - CHẠY TRƯỚC
-- --------------------------------------------------------------------------------------

-- 1.1 Role & Workshift
INSERT INTO role (name) VALUES 
(N'Admin'), 
(N'Staff'), 
(N'Warehouse');

INSERT INTO work_shift (name, start_time, end_time) VALUES 
(N'Ca Sáng', '08:00:00', '12:00:00'), 
(N'Ca Chiều', '13:00:00', '17:00:00'), 
(N'Ca Tối', '18:00:00', '22:00:00');

-- 1.2 Attributes (Brand, Category, Origin, Size, Color)
INSERT INTO brand (code, name, status) VALUES 
('B_NIKE', 'Nike', 1), 
('B_ADIDAS', 'Adidas', 1), 
('B_PUMA', 'Puma', 1);

INSERT INTO category (code, name, status) VALUES 
('C_RUN', N'Giày Chạy Bộ', 1), 
('C_BASKET', N'Giày Bóng Rổ', 1), 
('C_CASUAL', N'Giày Thời Trang', 1);

INSERT INTO origin (code, name, status) VALUES 
('O_VN', N'Việt Nam', 1), 
('O_USA', N'Mỹ', 1), 
('O_CN', N'Trung Quốc', 1);

INSERT INTO size (code, name, status) VALUES 
('S_39', '39', 1), 
('S_40', '40', 1), 
('S_41', '41', 1);

INSERT INTO color (code, name, status) VALUES 
('CL_RED', N'Đỏ', 1), 
('CL_BLK', N'Đen', 1), 
('CL_WHT', N'Trắng', 1);

-- 1.3 Voucher & Promotion
INSERT INTO voucher (code, name, min_order_value, max_discount_value, start_date, end_date, value, type, quantity, status) VALUES 
('SALE10', N'Giảm 10%', 500000, 50000, '2026-01-01', '2026-12-31', 10, 1, 100, 1),
('TET2026', N'Lì xì 50k', 1000000, 50000, '2026-01-01', '2026-02-15', 50000, 0, 50, 1),
('FREESHIP', N'Mã vận chuyển', 200000, 30000, '2026-01-01', '2026-06-01', 30000, 0, 200, 1);

INSERT INTO promotion (code, name, value, start_date, end_date, status) VALUES 
('BLACKFRI', N'Black Friday', 20, '2026-11-20', '2026-11-30', 0),
('SUMMER', N'Xả hè', 15, '2026-06-01', '2026-06-30', 0),
('FLASH', N'Flash Sale', 5, '2026-01-15', '2026-01-20', 1);


-- 2. NHÓM NGƯỜI DÙNG (USER & CUSTOMER)
-- --------------------------------------------------------------------------------------

-- 2.1 Employee
-- Password là hash của '123456' (Ví dụ mẫu BCrypt)
INSERT INTO employee (id_workshift, id_role, code, image, last_name, first_name, email, phone_number, gender, birthday, account, password, salary, create_at, status) VALUES 
(1, 1, 'NV001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix', N'Nguyễn', N'Văn A', 'admin@gmail.com', '0901111111', 1, '1995-01-01', 'admin', '$2a$12$JD2...', 15000000, GETDATE(), 1),
(2, 2, 'NV002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka', N'Trần', N'Thị B', 'staff@gmail.com', '0902222222', 0, '1998-05-15', 'staff', '$2a$12$JD2...', 8000000, GETDATE(), 1),
(3, 3, 'NV003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Bob', N'Lê', N'Văn C', 'kho@gmail.com', '0903333333', 1, '2000-10-20', 'kho', '$2a$12$JD2...', 9000000, GETDATE(), 1);

-- 2.2 Customer
INSERT INTO customer (code, image, last_name, first_name, email, phone_number, gender, birthday, account, password, create_at, status) VALUES 
('KH001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Cus1', N'Phạm', N'Hương', 'khach1@gmail.com', '0987654321', 0, '1999-02-02', 'khach1', '$2a$12$JD2...', GETDATE(), 1),
('KH002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Cus2', N'Đỗ', N'Nam', 'khach2@gmail.com', '0987654322', 1, '1995-03-03', 'khach2', '$2a$12$JD2...', GETDATE(), 1),
('KH003', NULL, N'Lê', N'Lợi', 'khach3@gmail.com', '0987654323', 1, '2002-04-04', 'khach3', '$2a$12$JD2...', GETDATE(), 1);

-- 2.3 Address
INSERT INTO address (id_customer, consignee_name, consignee_phone, city, ward, street_detail, note) VALUES 
(1, N'Phạm Hương', '0987654321', N'Hà Nội', N'Dịch Vọng', N'Số 1 Cầu Giấy', N'Giao giờ hành chính'),
(2, N'Đỗ Nam', '0987654322', N'TP.HCM', N'Bến Nghé', N'Quận 1', N'Nhà riêng'),
(3, N'Lê Lợi', '0987654323', N'Đà Nẵng', N'Hải Châu', N'Số 10 Bạch Đằng', N'Gọi trước khi giao');


-- 3. NHÓM SẢN PHẨM (CORE DATA)
-- --------------------------------------------------------------------------------------

-- 3.1 Product (Parent)
INSERT INTO product (id_brand, id_category, id_origin, code, name, image, create_at, status) VALUES 
(1, 1, 1, 'P001', N'Nike Air Zoom Pegasus', 'https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/pegasus.jpg', GETDATE(), 1),
(2, 3, 2, 'P002', N'Adidas Superstar', 'https://assets.adidas.com/images/w_600,f_auto,q_auto/superstar.jpg', GETDATE(), 1),
(3, 2, 3, 'P003', N'Puma MB.01', 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa,w_2000,h_2000/global/mb01.jpg', GETDATE(), 1);

-- 3.2 Product Detail (SKU)
INSERT INTO product_detail (id_product, id_color, id_size, code, name, image, price, quantity, create_at, status) VALUES 
(1, 1, 1, 'SKU_P1_RED_39', N'Nike Air Zoom Đỏ 39', 'https://img.nike/red.jpg', 2500000, 50, GETDATE(), 1),
(1, 2, 2, 'SKU_P1_BLK_40', N'Nike Air Zoom Đen 40', 'https://img.nike/black.jpg', 2500000, 30, GETDATE(), 1),
(2, 3, 3, 'SKU_P2_WHT_41', N'Adidas Superstar Trắng 41', 'https://img.adidas/white.jpg', 1800000, 100, GETDATE(), 1);

-- 3.3 Product Promotion (Link SP với Khuyến mãi)
INSERT INTO product_promotion (id_product, id_promotion) VALUES 
(1, 3), -- Nike tham gia Flash Sale
(2, 3), -- Adidas tham gia Flash Sale
(3, 1); -- Puma tham gia Black Friday


-- 4. NHÓM GIAO DỊCH (TRANSACTIONS)
-- --------------------------------------------------------------------------------------

-- 4.1 Cart & Cart Detail
INSERT INTO cart (id_customer) VALUES (1), (2), (3);

INSERT INTO cart_detail (id_cart, id_product_detail, quantity) VALUES 
(1, 1, 1), -- Khách 1 giỏ có Nike Đỏ
(1, 2, 2), -- Khách 1 giỏ có thêm 2 đôi Nike Đen
(2, 3, 1); -- Khách 2 giỏ có Adidas Trắng

-- 4.2 Orders
INSERT INTO orders (id_customer, id_employee, id_voucher, code, employee_code, employee_name, customer_name, customer_phone, consignee_name, consignee_phone, consignee_address, total_money, total_quantity, voucher_discount_value, shipping_fee, final_amount, create_at, note, status) VALUES 
(1, 1, 1, 'ORD_001', 'NV001', N'Nguyễn Văn A', N'Phạm Hương', '0987654321', N'Phạm Hương', '0987654321', N'Số 1 Cầu Giấy HN', 2500000, 1, 50000, 30000, 2480000, GETDATE(), N'Giao nhanh', 3), -- Hoàn thành
(2, NULL, NULL, 'ORD_002', NULL, NULL, N'Đỗ Nam', '0987654322', N'Đỗ Nam', '0987654322', N'Quận 1 HCM', 5000000, 2, 0, 0, 5000000, DATEADD(hour, -2, GETDATE()), N'', 0), -- Chờ xác nhận
(NULL, 2, NULL, 'ORD_003', 'NV002', N'Trần Thị B', N'Khách lẻ', N'', N'Khách lẻ', N'', N'Tại quầy', 1800000, 1, 0, 0, 1800000, DATEADD(day, -1, GETDATE()), N'Khách mua tại quầy', 3); -- Hoàn thành

-- 4.3 Order Detail
INSERT INTO order_detail (id_order, id_product_detail, price, quantity, total_price) VALUES 
(1, 1, 2500000, 1, 2500000), -- Đơn 1 mua 1 Nike Đỏ
(2, 2, 2500000, 2, 5000000), -- Đơn 2 mua 2 Nike Đen
(3, 3, 1800000, 1, 1800000); -- Đơn 3 mua 1 Adidas Trắng

-- 4.4 Payment
INSERT INTO payment (id_order, amount, payment_method, status, transaction_code, payment_date, note) VALUES 
(1, 2480000, 0, 1, NULL, GETDATE(), N'Tiền mặt khi nhận hàng'),
(2, 5000000, 1, 1, 'VNP12345678', GETDATE(), N'VNPAY - Đã thanh toán'),
(3, 1800000, 0, 1, NULL, GETDATE(), N'Thanh toán tại quầy');

-- 4.5 Order History (Log)
INSERT INTO order_history (id_order, id_employee, action, column_name, before_val, after_val, create_at) VALUES 
(1, 1, N'Xác nhận đơn hàng', N'status', N'0', N'1', GETDATE()),
(2, NULL, N'Đặt hàng mới', N'status', NULL, N'0', DATEADD(hour, -2, GETDATE())),
(3, 2, N'Hoàn thành', N'status', N'1', N'3', DATEADD(day, -1, GETDATE()));

-- 4.6 Product Review
INSERT INTO product_review (id_customer, id_product, id_order, rating, comment, image, create_at, status) VALUES 
(1, 1, 1, 5, N'Giày đi rất êm, giao hàng nhanh!', NULL, GETDATE(), 1),
(1, 1, 1, 4, N'Màu đỏ bên ngoài hơi tối hơn ảnh chút.', NULL, GETDATE(), 1),
(2, 2, 2, 5, N'Hàng chính hãng, check code ok.', NULL, GETDATE(), 1);