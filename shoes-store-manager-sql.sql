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

-- ======================================================================================
-- PHẦN 1: PHÂN QUYỀN & CA LÀM VIỆC
-- ======================================================================================
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

-- ======================================================================================
-- PHẦN 2: KHÁCH HÀNG & GIAO VẬN
-- ======================================================================================
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

-- ======================================================================================
-- PHẦN 3: THUỘC TÍNH SẢN PHẨM
-- ======================================================================================
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

-- ======================================================================================
-- PHẦN 4: HÀNG HÓA VÀ KHO BIẾN THỂ (SKU)
-- ======================================================================================
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
    FOREIGN KEY (id_size) REFERENCES size(id),
    CONSTRAINT UQ_Product_Color_Size UNIQUE (id_product, id_color, id_size)
);

-- ======================================================================================
-- PHẦN 5: MARKETING
-- ======================================================================================
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

-- ======================================================================================
-- PHẦN 6: ĐƠN HÀNG & KIỂM TOÁN TÀI CHÍNH
-- ======================================================================================
CREATE TABLE orders(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_customer INT,
    id_employee INT,
    id_voucher INT,
    code VARCHAR(50) UNIQUE NOT NULL,
    order_type VARCHAR(20) NOT NULL DEFAULT 'ONLINE',
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

--LƯU VẾT LỊCH SỬ CHUYỂN TRẠNG THÁI ĐƠN HÀNG (AUDIT TRAIL)
CREATE TABLE order_status_history(
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_order INT NOT NULL,
    id_employee INT, -- Ai là người thao tác (Null nếu hệ thống tự đổi như VNPAY)
    old_status INT, -- Trạng thái cũ
    new_status INT NOT NULL, -- Trạng thái mới
    note NVARCHAR(MAX), -- Lý do hủy, ghi chú chuẩn bị hàng...
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_order) REFERENCES orders(id),
    FOREIGN KEY (id_employee) REFERENCES employee(id)
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
    amount_tendered DECIMAL(19, 2) NOT NULL DEFAULT 0, 
    change_amount DECIMAL(19, 2) NOT NULL DEFAULT 0,   
    payment_method INT NOT NULL,
    status INT NOT NULL,
    transaction_code VARCHAR(100),
    payment_date DATETIME DEFAULT GETDATE(),
    note NVARCHAR(MAX),
    -- Đã hợp nhất cột created_at và updated_at vào bảng
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_order) REFERENCES orders(id)
);

-- ======================================================================================
-- PHẦN 7: TƯƠNG TÁC & GIỎ HÀNG
-- ======================================================================================
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
);
GO

-- ======================================================================================
-- PHẦN 8: CHÈN DỮ LIỆU MẪU (SEED DATA)
-- ======================================================================================

-- 1. Metadata
INSERT INTO role (name) VALUES (N'ROLE_ADMIN');
INSERT INTO work_shift (name, start_time, end_time) VALUES (N'Ca Hành Chính', '08:00:00', '17:00:00');

-- 2. Con người
INSERT INTO employee (id_workshift, id_role, code, last_name, first_name, email, phone_number, gender, birthday, account, password, salary, status) 
VALUES (1, 1, 'NV0001', N'Nguyễn Mạnh', N'Tú', 'admin@architect.com', '0988888888', 1, '2000-01-01', 'admin', '123456', 50000000, 1);

INSERT INTO customer (code, last_name, first_name, email, phone_number, gender, birthday, account, password, status) 
VALUES ('KH0001', N'Trần', N'Khách Hàng', 'khachhang@gmail.com', '0911112222', 0, '1995-05-15', 'khachhang', '123456', 1);

INSERT INTO address (id_customer, consignee_name, consignee_phone, city, ward, street_detail) 
VALUES (1, N'Trần Khách Hàng', '0911112222', N'Hà Nội', N'Cầu Giấy', N'Số 1 Tôn Thất Thuyết');

-- 3. Cấu hình sản phẩm
INSERT INTO brand (code, name, status) VALUES ('BR001', 'Nike', 1);
INSERT INTO category (code, name, status) VALUES ('CAT01', 'Sneaker', 1);
INSERT INTO origin (code, name, status) VALUES ('ORG01', 'Vietnam', 1);
INSERT INTO size (code, name, status) VALUES ('SZ40', 'Size 40', 1);
INSERT INTO color (code, name, status) VALUES ('CL01', 'Black', 1);

-- 4. Hàng hóa thực thể
INSERT INTO product (id_brand, id_category, id_origin, code, name, status) 
VALUES (1, 1, 1, 'SP0001', 'Nike Air Max 2026', 1);

INSERT INTO product_detail (id_product, id_color, id_size, code, name, price, quantity, status) 
VALUES (1, 1, 1, 'SP0001-CL01-SZ40', 'Nike Air Max 2026 - Black - 40', 2500000, 100, 1);

-- 5. Marketing
INSERT INTO voucher (code, name, min_order_value, max_discount_value, start_date, end_date, value, quantity, type, status) 
VALUES ('SALE100K', N'Giảm 100K Đơn 1 Triệu', 1000000, 100000, '2026-01-01', '2026-12-31', 100000, 50, 0, 1);

INSERT INTO promotion (code, name, value, start_date, end_date, status) 
VALUES ('SUMMER26', N'Khuyến mãi hè 2026', 10, '2026-05-01', '2026-08-31', 1);

INSERT INTO product_promotion (id_product, id_promotion) VALUES (1, 1);

-- 6. Giao dịch
INSERT INTO orders (id_customer, id_employee, id_voucher, code, order_type, total_money, total_quantity, final_amount, status) 
VALUES (1, 1, 1, 'ORD0001', 'ONLINE', 2500000, 1, 2400000, 1);

-- Lưu vết lịch sử khi vừa tạo đơn (Nháp -> Chờ xác nhận)
INSERT INTO order_status_history (id_order, id_employee, old_status, new_status, note) 
VALUES (1, 1, NULL, 1, N'Khởi tạo đơn hàng trực tuyến');

INSERT INTO order_detail (id_order, id_product_detail, price, quantity, total_price) 
VALUES (1, 1, 2500000, 1, 2500000);

INSERT INTO payment (id_order, amount, payment_method, status) 
VALUES (1, 2400000, 1, 0); -- 1 = VNPAY, 0 = Chờ thanh toán

-- 7. Tương tác
INSERT INTO cart (id_customer) VALUES (1);
INSERT INTO cart_detail (id_cart, id_product_detail, quantity) VALUES (1, 1, 1);
INSERT INTO product_review (id_customer, id_product, id_order, rating, comment, status) 
VALUES (1, 1, 1, 5, N'Giày rất êm, giao hàng nhanh', 1);

-- 8. Bộ đếm
INSERT INTO system_sequence (prefix, next_value) VALUES ('ORD', 2);
GO