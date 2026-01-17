USE master;
GO

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


-- 1.1. NHÓM PHÂN QUYỀN & NHÂN VIÊN
-- --------------------------------------------------------------------------------------
CREATE TABLE role(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL
);

CREATE TABLE work_shift(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

CREATE TABLE employee(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_workshift INT NOT NULL,
    id_role INT NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    image VARCHAR(MAX), -- Đã có từ trước
    last_name NVARCHAR(100) NOT NULL,
    first_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    gender BIT NOT NULL,      
    birthday DATE NOT NULL,
    account VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    salary DECIMAL(19, 2) NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL,      
    
    FOREIGN KEY (id_workshift) REFERENCES work_shift(id),
    FOREIGN KEY (id_role) REFERENCES role(id)
);

-- 1.2. NHÓM KHÁCH HÀNG
-- --------------------------------------------------------------------------------------
CREATE TABLE customer(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    image VARCHAR(MAX), -- Đã có từ trước
    last_name NVARCHAR(100) NOT NULL,
    first_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(15),
    gender BIT,                
    birthday DATE,
    account VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL        
);

CREATE TABLE address(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT NOT NULL,
    consignee_name NVARCHAR(255) NOT NULL,
    consignee_phone VARCHAR(15) NOT NULL,
    city NVARCHAR(100) NOT NULL,
    ward NVARCHAR(100) NOT NULL,
    street_detail NVARCHAR(255) NOT NULL,
    note NVARCHAR(MAX),
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_customer) REFERENCES customer(id)
);

-- 1.3. NHÓM SẢN PHẨM (CORE) - ĐÃ BỔ SUNG IMAGE
-- --------------------------------------------------------------------------------------
CREATE TABLE brand(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL          
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
    name NVARCHAR(100) NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL
);

CREATE TABLE color(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL
);

CREATE TABLE product(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_brand INT NOT NULL,
    id_category INT NOT NULL,
    id_origin INT NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255) NOT NULL,
    
    image VARCHAR(MAX), -- [UPDATED] Thêm ảnh đại diện sản phẩm (Thumbnail)
    
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL,              
    
    FOREIGN KEY (id_brand) REFERENCES brand(id),
    FOREIGN KEY (id_category) REFERENCES category(id),
    FOREIGN KEY (id_origin) REFERENCES origin(id),
);

CREATE TABLE product_detail(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_product INT NOT NULL,
    id_color INT NOT NULL,
    id_size INT NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255) NOT NULL,
    
    image VARCHAR(MAX), -- [UPDATED] Thêm ảnh chi tiết (VD: Ảnh đúng góc cạnh màu sắc đó)
    
    price DECIMAL(19, 2) NOT NULL,
    quantity INT NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL,              
    
    FOREIGN KEY (id_product) REFERENCES product(id),
    FOREIGN KEY (id_color) REFERENCES color(id),
    FOREIGN KEY (id_size) REFERENCES size(id),
);

-- 1.4. NHÓM KHUYẾN MÃI
-- --------------------------------------------------------------------------------------
CREATE TABLE voucher(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    min_order_value DECIMAL(19, 2) NOT NULL,
    max_discount_value DECIMAL(19, 2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    value DECIMAL(19, 2) NOT NULL,
    quantity INT NOT NULL,
    type BIT NOT NULL,                
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL                
);

CREATE TABLE promotion(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    value DECIMAL(19, 2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    status BIT NOT NULL
);

CREATE TABLE product_promotion(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_product INT NOT NULL,
    id_promotion INT NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_product) REFERENCES product(id),
    FOREIGN KEY (id_promotion) REFERENCES promotion(id)
);

-- 1.5. NHÓM ĐƠN HÀNG & THANH TOÁN
-- --------------------------------------------------------------------------------------
CREATE TABLE orders(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT,
    id_employee INT,
    id_voucher INT,
    code VARCHAR(50) UNIQUE NOT NULL,
    employee_code VARCHAR(50),
    employee_name NVARCHAR(255),
    customer_name NVARCHAR(255),
    customer_phone VARCHAR(15),
    consignee_name NVARCHAR(255),
    consignee_phone VARCHAR(15),
    consignee_address NVARCHAR(MAX),
    total_money DECIMAL(19, 2) NOT NULL,
    total_quantity INT NOT NULL,
    voucher_discount_value DECIMAL(19, 2),
    shipping_fee DECIMAL(19, 2),
    final_amount DECIMAL(19, 2) NOT NULL,
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    note NVARCHAR(MAX),
    status INT NOT NULL,
    FOREIGN KEY (id_customer) REFERENCES customer(id),
    FOREIGN KEY (id_employee) REFERENCES employee(id),
    FOREIGN KEY (id_voucher) REFERENCES voucher(id)
);

CREATE TABLE order_detail(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_order INT NOT NULL,
    id_product_detail INT NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(19, 2),
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_order) REFERENCES orders(id),
    FOREIGN KEY (id_product_detail) REFERENCES product_detail(id),
);

CREATE TABLE payment(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_order INT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    payment_method INT NOT NULL,
    status INT NOT NULL,
    transaction_code VARCHAR(100),
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
    id_employee INT,
    action VARCHAR(100),
    column_name NVARCHAR(100),
    before_val NVARCHAR(MAX),
    after_val NVARCHAR(MAX),
    create_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_order) REFERENCES orders(id),
    FOREIGN KEY (id_employee) REFERENCES employee(id)
);


-- ======================================================================================
-- INSERT DATA 
-- ======================================================================================

-- 1. Role & Workshift
INSERT INTO role (name) VALUES (N'Admin'), (N'Staff'), (N'Warehouse');
INSERT INTO work_shift (name, start_time, end_time) VALUES (N'Ca Sáng', '08:00:00', '12:00:00'), (N'Ca Chiều', '13:00:00', '17:00:00'), (N'Ca Tối', '18:00:00', '22:00:00');

-- 2. Employee (Đã có image)
INSERT INTO employee (id_workshift, id_role, code, image, last_name, first_name, email, phone_number, gender, birthday, account, password, salary, create_at, status) VALUES 
(1, 1, 'NV001', 'https://example.com/emp1.jpg', N'Nguyễn', N'Văn A', 'a@gmail.com', '0901234567', 1, '1995-01-01', 'admin01', '$2a$12$JD...', 15000000, GETDATE(), 1),
(2, 2, 'NV002', 'https://example.com/emp2.jpg', N'Trần', N'Thị B', 'b@gmail.com', '0901234568', 0, '1998-05-15', 'staff01', '$2a$12$JD...', 8000000, GETDATE(), 1),
(3, 3, 'NV003', 'https://example.com/emp3.jpg', N'Lê', N'Văn C', 'c@gmail.com', '0901234569', 1, '2000-10-20', 'kho01', '$2a$12$JD...', 9000000, GETDATE(), 1);

-- 3. Customer & Address (Đã có image)
INSERT INTO customer (code, image, last_name, first_name, email, phone_number, gender, birthday, account, password, create_at, status) VALUES 
('KH001', 'https://example.com/cus1.jpg', N'Phạm', N'Hương', 'huong@gmail.com', '0987654321', 0, '1999-02-02', 'khach01', 'pass123', GETDATE(), 1),
('KH002', 'https://example.com/cus2.jpg', N'Đỗ', N'Nam', 'nam@gmail.com', '0987654322', 1, '1995-03-03', 'khach02', 'pass123', GETDATE(), 1),
('KH003', NULL, N'Khách', N'Vãng Lai', 'vanglai@gmail.com', NULL, 1, NULL, NULL, NULL, GETDATE(), 1);

INSERT INTO address (id_customer, consignee_name, consignee_phone, city, ward, street_detail, note) VALUES 
(1, N'Phạm Hương', '0987654321', N'Hà Nội', N'Dịch Vọng', N'Số 1 Cầu Giấy', N'Giao giờ hành chính'),
(1, N'Phạm Hương (Cty)', '0987654321', N'Hà Nội', N'Mễ Trì', N'Tòa Keangnam', N'Gọi trước khi giao'),
(2, N'Đỗ Nam', '0987654322', N'TP.HCM', N'Bến Nghé', N'Quận 1', N'Nhà riêng');

-- 4. Product Lookup Data
INSERT INTO brand (code, name, status) VALUES ('B_NIKE', 'Nike', 1), ('B_ADIDAS', 'Adidas', 1), ('B_PUMA', 'Puma', 1);
INSERT INTO category (code, name, status) VALUES ('C_RUN', N'Giày Chạy Bộ', 1), ('C_BASKET', N'Giày Bóng Rổ', 1), ('C_CASUAL', N'Giày Thời Trang', 1);
INSERT INTO origin (code, name, status) VALUES ('O_VN', N'Việt Nam', 1), ('O_USA', N'Mỹ', 1), ('O_CN', N'Trung Quốc', 1);
INSERT INTO size (code, name, status) VALUES ('S_39', '39', 1), ('S_40', '40', 1), ('S_41', '41', 1);
INSERT INTO color (code, name, status) VALUES ('CL_RED', N'Đỏ', 1), ('CL_BLK', N'Đen', 1), ('CL_WHT', N'Trắng', 1);

-- 5. Product Core (UPDATED: Đã thêm cột image)
INSERT INTO product (id_brand, id_category, id_origin, code, name, image, create_at, status) VALUES 
(1, 1, 1, 'P001', N'Nike Air Zoom Pegasus', 'https://img.nike.com/pegasus_main.jpg', GETDATE(), 1),
(2, 3, 2, 'P002', N'Adidas Superstar', 'https://img.adidas.com/superstar_main.jpg', GETDATE(), 1),
(3, 2, 3, 'P003', N'Puma MB.01', 'https://img.puma.com/mb01_main.jpg', GETDATE(), 1);

-- UPDATED: Đã thêm cột image vào product_detail
INSERT INTO product_detail (id_product, id_color, id_size, code, name, image, price, quantity, create_at, status) VALUES 
(1, 1, 2, 'SKU_P1_RED_40', N'Nike Air Zoom Đỏ 40', 'https://img.nike.com/pegasus_red.jpg', 2500000, 50, GETDATE(), 1),
(1, 2, 3, 'SKU_P1_BLK_41', N'Nike Air Zoom Đen 41', 'https://img.nike.com/pegasus_black.jpg', 2500000, 30, GETDATE(), 1),
(2, 3, 1, 'SKU_P2_WHT_39', N'Adidas Superstar Trắng 39', 'https://img.adidas.com/superstar_white.jpg', 1800000, 100, GETDATE(), 1);

-- 6. Voucher & Promotion
INSERT INTO voucher (code, name, min_order_value, max_discount_value, start_date, end_date, value, type, quantity, status) VALUES 
('SALE10', N'Giảm 10%', 500000, 50000, '2026-01-01', '2026-02-01', 10, 1, 100, 1),
('TET2026', N'Lì xì 50k', 1000000, 50000, '2026-01-01', '2026-02-15', 50000, 0, 50, 1),
('FREESHIP', N'Mã vận chuyển', 200000, 30000, '2026-01-01', '2026-03-01', 30000, 0, 200, 1);

INSERT INTO promotion (code, name, value, start_date, end_date, status) VALUES 
('BLACKFRI', N'Black Friday', 20, '2026-11-20', '2026-11-30', 0),
('SUMMER', N'Xả hè', 15, '2026-06-01', '2026-06-30', 0),
('FLASH', N'Flash Sale', 5, '2026-01-15', '2026-01-16', 1);

INSERT INTO product_promotion (id_product, id_promotion) VALUES (1, 3), (2, 3), (3, 3);

-- 7. Orders & Order Detail
INSERT INTO orders (id_customer, id_employee, id_voucher, code, employee_code, employee_name, customer_name, customer_phone, consignee_name, consignee_phone, consignee_address, total_money, total_quantity, voucher_discount_value, shipping_fee, final_amount, create_at, note, status) VALUES 
(1, 1, 1, 'ORD_001', 'NV001', N'Nguyễn Văn A', N'Phạm Hương', '0987654321', N'Phạm Hương', '0987654321', N'Số 1 Cầu Giấy HN', 2500000, 1, 50000, 30000, 2480000, GETDATE(), N'Giao nhanh', 1),
(2, NULL, NULL, 'ORD_002', NULL, NULL, N'Đỗ Nam', '0987654322', N'Đỗ Nam', '0987654322', N'Quận 1 HCM', 5000000, 2, 0, 0, 5000000, DATEADD(hour, -2, GETDATE()), N'', 0),
(NULL, 2, NULL, 'ORD_003', 'NV002', N'Trần Thị B', N'Khách lẻ', N'', N'Khách lẻ', N'', N'Tại quầy', 1800000, 1, 0, 0, 1800000, DATEADD(day, -1, GETDATE()), N'Khách mua tại quầy', 3);

INSERT INTO order_detail (id_order, id_product_detail, price, quantity, total_price) VALUES 
(1, 1, 2500000, 1, 2500000),
(2, 1, 2500000, 2, 5000000),
(3, 3, 1800000, 1, 1800000);

-- 8. Payment
INSERT INTO payment (id_order, amount, payment_method, status, transaction_code, payment_date, note) VALUES 
(1, 2480000, 0, 0, NULL, NULL, N'Thanh toán COD'),
(2, 5000000, 1, 1, 'VNP12345678', GETDATE(), N'Đã chuyển khoản'),
(3, 1800000, 0, 1, NULL, GETDATE(), N'Tiền mặt tại quầy');

-- 9. Order History
INSERT INTO order_history (id_order, id_employee, action, column_name, before_val, after_val, create_at) VALUES 
(1, 1, N'Xác nhận đơn hàng', N'status', N'Chờ xác nhận', N'Đã xác nhận', GETDATE()),
(2, NULL, N'Khách tạo đơn mới', N'status', NULL, N'Chờ xác nhận', DATEADD(hour, -2, GETDATE())),
(3, 2, N'Hoàn thành đơn tại quầy', N'status', N'Mới', N'Hoàn thành', DATEADD(day, -1, GETDATE()));