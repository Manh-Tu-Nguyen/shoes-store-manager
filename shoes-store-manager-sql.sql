USE master;
GO

-- 1. Khởi tạo Database sạch
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
-- PHẦN 1: TẠO BẢNG ĐỒNG BỘ (FULL AUDIT COLUMNS)
-- ======================================================================================

-- 1.1 Phân quyền & Ca làm việc
CREATE TABLE role(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE work_shift(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

-- 1.2 Nhân viên
CREATE TABLE employee(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_workshift INT NOT NULL,
    id_role INT NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    image VARCHAR(MAX),
    last_name NVARCHAR(100) NOT NULL,
    first_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    gender BIT NOT NULL,
    birthday DATE NOT NULL,
    account VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    salary DECIMAL(19, 2) NOT NULL,
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_workshift) REFERENCES work_shift(id),
    FOREIGN KEY (id_role) REFERENCES role(id)
);

-- 1.3 Khách hàng & Địa chỉ
CREATE TABLE customer(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    image VARCHAR(MAX),
    last_name NVARCHAR(100) NOT NULL,
    first_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(15),
    gender BIT,
    birthday DATE,
    account VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
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
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_customer) REFERENCES customer(id)
);

-- 1.4 Thuộc tính Sản phẩm
CREATE TABLE brand(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE category(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE origin(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE size(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE color(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

-- 1.5 Sản phẩm & Chi tiết (SKU)
CREATE TABLE product(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_brand INT NOT NULL,
    id_category INT NOT NULL,
    id_origin INT NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255) NOT NULL,
    image VARCHAR(MAX),
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_brand) REFERENCES brand(id),
    FOREIGN KEY (id_category) REFERENCES category(id),
    FOREIGN KEY (id_origin) REFERENCES origin(id)
);

CREATE TABLE product_detail(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_product INT NOT NULL,
    id_color INT NOT NULL,
    id_size INT NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(255) NOT NULL,
    image VARCHAR(MAX),
    price DECIMAL(19, 2) NOT NULL,
    quantity INT NOT NULL,
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_product) REFERENCES product(id),
    FOREIGN KEY (id_color) REFERENCES color(id),
    FOREIGN KEY (id_size) REFERENCES size(id)
);

-- 1.6 Khuyến mãi & Voucher
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
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE promotion(
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    value DECIMAL(19, 2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE product_promotion(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_product INT NOT NULL,
    id_promotion INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_product) REFERENCES product(id),
    FOREIGN KEY (id_promotion) REFERENCES promotion(id)
);

-- 1.7 Đơn hàng & Thanh toán
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
    status INT NOT NULL,
    note NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
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
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_order) REFERENCES orders(id),
    FOREIGN KEY (id_product_detail) REFERENCES product_detail(id)
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

-- 1.8 Giỏ hàng & Đánh giá
CREATE TABLE cart(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT UNIQUE,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_customer) REFERENCES customer(id)
);

CREATE TABLE cart_detail(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_cart INT NOT NULL,
    id_product_detail INT NOT NULL,
    quantity INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_cart) REFERENCES cart(id),
    FOREIGN KEY (id_product_detail) REFERENCES product_detail(id),
    CONSTRAINT UQ_Cart_Product UNIQUE (id_cart, id_product_detail)
);

CREATE TABLE product_review(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT NOT NULL,
    id_product INT NOT NULL,
    id_order INT,
    rating INT CHECK (rating >= 1 AND rating <= 5) NOT NULL,
    comment NVARCHAR(MAX),
    image VARCHAR(MAX),
    status BIT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_customer) REFERENCES customer(id),
    FOREIGN KEY (id_product) REFERENCES product(id),
    FOREIGN KEY (id_order) REFERENCES orders(id)
);
CREATE TABLE system_sequence (
    prefix VARCHAR(50) PRIMARY KEY,
    next_value BIGINT NOT NULL
    )

GO

-- 1. NHÓM DANH MỤC & PHÂN QUYỀN
INSERT INTO role (name) VALUES (N'ROLE_ADMIN'), (N'ROLE_STAFF'), (N'ROLE_CLIENT');

INSERT INTO work_shift (name, start_time, end_time) VALUES 
(N'Ca Sáng', '08:00:00', '12:00:00'), 
(N'Ca Chiều', '13:00:00', '17:00:00'),
(N'Ca Tối', '18:00:00', '22:00:00');

-- 2. THUỘC TÍNH SẢN PHẨM
INSERT INTO brand (code, name, status) VALUES ('NIKE', 'Nike', 1), ('ADIDAS', 'Adidas', 1), ('PUMA', 'Puma', 1);
INSERT INTO category (code, name, status) VALUES ('SNEAKER', N'Giày Thể Thao', 1), ('RUNNING', N'Giày Chạy Bộ', 1), ('SANDAL', N'Sandal', 1);
INSERT INTO origin (code, name, status) VALUES ('VN', N'Việt Nam', 1), ('USA', N'Mỹ', 1), ('CN', N'Trung Quốc', 1);
INSERT INTO size (code, name, status) VALUES ('S39', '39', 1), ('S40', '40', 1), ('S41', '41', 1);
INSERT INTO color (code, name, status) VALUES ('RED', N'Đỏ', 1), ('BLUE', N'Xanh Dương', 1), ('BLACK', N'Đen', 1);

-- 3. NHÂN VIÊN & KHÁCH HÀNG
INSERT INTO employee (id_workshift, id_role, code, last_name, first_name, email, phone_number, gender, birthday, account, password, salary, status) VALUES 
(1, 1, 'NV001', N'Nguyễn', N'Văn A', 'admin@gmail.com', '0911111111', 1, '1990-01-01', 'admin', '123456', 20000000, 1),
(2, 2, 'NV002', N'Trần', N'Thị B', 'staff@gmail.com', '0922222222', 0, '1995-05-05', 'staff', '123456', 10000000, 1),
(3, 3, 'NV003', N'Lê', N'Văn C', 'warehouse@gmail.com', '0933333333', 1, '1992-10-10', 'warehouse', '123456', 12000000, 1);

INSERT INTO customer (code, last_name, first_name, email, phone_number, gender, birthday, account, password, status) VALUES 
('KH001', N'Phạm', N'Hùng', 'hung@gmail.com', '0988888888', 1, '1998-12-12', 'hungpham', '123456', 1),
('KH002', N'Đỗ', N'Lan', 'lan@gmail.com', '0977777777', 0, '2000-01-01', 'landtt', '123456', 1),
('KH003', N'Vũ', N'Nam', 'nam@gmail.com', '0966666666', 1, '1997-06-06', 'namvu', '123456', 1);

INSERT INTO address (id_customer, consignee_name, consignee_phone, city, ward, street_detail) VALUES 
(1, N'Phạm Hùng', '0988888888', N'Hà Nội', N'Cầu Giấy', N'Số 1 Xuân Thủy'),
(2, N'Đỗ Lan', '0977777777', N'Hồ Chí Minh', N'Quận 1', N'100 Lê Lợi'),
(3, N'Vũ Nam', '0966666666', N'Đà Nẵng', N'Hải Châu', N'50 Phan Chu Trinh');

-- 4. SẢN PHẨM & BIẾN THỂ
INSERT INTO product (id_brand, id_category, id_origin, code, name, status) VALUES 
(1, 1, 1, 'PROD001', N'Nike Air Max 2024', 1),
(2, 1, 2, 'PROD002', N'Adidas Ultraboost', 1),
(3, 2, 3, 'PROD003', N'Puma Nitro Chạy', 1);

INSERT INTO product_detail (id_product, id_color, id_size, code, name, price, quantity, status) VALUES 
(1, 1, 1, 'SKU001', N'Nike Air Max Đỏ 39', 3500000, 100, 1),
(2, 2, 2, 'SKU002', N'Adidas Ultraboost Xanh 40', 4200000, 50, 1),
(3, 3, 3, 'SKU003', N'Puma Nitro Đen 41', 2800000, 80, 1);

-- 5. KHUYẾN MÃI
INSERT INTO voucher (code, name, min_order_value, max_discount_value, start_date, end_date, value, type, quantity, status) VALUES 
('Giam20k', N'Giảm 20k', 200000, 20000, '2024-01-01', '2026-12-31', 20000, 0, 1000, 1),
('Giam10%', N'Giảm 10%', 500000, 100000, '2024-01-01', '2026-12-31', 10, 1, 500, 1),
('VipOnly', N'Voucher VIP', 1000000, 200000, '2024-01-01', '2026-12-31', 15, 1, 100, 1);

INSERT INTO promotion (code, name, value, start_date, end_date, status) VALUES 
('KM_HE', N'Khuyến mãi Hè', 10, '2024-06-01', '2026-08-31', 1),
('KM_TET', N'Khuyến mãi Tết', 20, '2024-12-01', '2026-02-01', 1),
('KM_BLACKFRI', N'Black Friday', 30, '2024-11-20', '2026-11-30', 1);

-- 6. ĐƠN HÀNG & GIAO DỊCH
INSERT INTO orders (id_customer, id_employee, id_voucher, code, total_money, total_quantity, final_amount, status) VALUES 
(1, 2, 1, 'ORD001', 3500000, 1, 3480000, 3), -- Hoàn thành
(2, 2, NULL, 'ORD002', 4200000, 1, 4200000, 0), -- Chờ xác nhận
(3, NULL, 2, 'ORD003', 2800000, 1, 2520000, 1); -- Đã xác nhận

INSERT INTO order_detail (id_order, id_product_detail, price, quantity, total_price) VALUES 
(1, 1, 3500000, 1, 3500000),
(2, 2, 4200000, 1, 4200000),
(3, 3, 2800000, 1, 2800000);

INSERT INTO payment (id_order, amount, payment_method, status, transaction_code) VALUES 
(1, 3480000, 0, 1, 'CASH_001'),
(2, 0, 1, 0, NULL),
(3, 2520000, 1, 1, 'BANK_VNP_001');

-- 7. GIỎ HÀNG & REVIEW
INSERT INTO cart (id_customer) VALUES (1), (2), (3);
INSERT INTO cart_detail (id_cart, id_product_detail, quantity) VALUES (1, 2, 1), (2, 3, 2), (3, 1, 1);

INSERT INTO product_review (id_customer, id_product, id_order, rating, comment, status) VALUES 
(1, 1, 1, 5, N'Giày rất đẹp, giao nhanh!', 1),
(2, 2, NULL, 4, N'Đi khá êm chân', 1),
(3, 3, 3, 5, N'Hàng chính hãng, tuyệt vời', 1);

ALTER TABLE orders 
ADD order_type VARCHAR(20) DEFAULT 'ONLINE';

ALTER TABLE payment 
ADD amount_tendered DECIMAL(19, 2) DEFAULT 0,
    change_amount DECIMAL(19, 2) DEFAULT 0;
GO