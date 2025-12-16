# Bookify Database Sample Data

## 📊 Tổng quan dữ liệu

Bộ dữ liệu mẫu này bao gồm:

| Bảng | Số lượng bản ghi |
|------|-----------------|
| Categories | 15 |
| Publishers | 25 |
| Authors | 50 |
| Users (Admin) | 5 |
| Users (Customer) | 100 |
| Addresses | 200 |
| Books | 150 |
| Book-Author Relations | ~80 |
| Book Images | ~250 |
| Reviews | ~180 |
| Shopping Carts | 100 |
| Cart Items | ~15 |
| Orders | ~180 |
| Order Details | ~300 |
| Payments | ~100 |

## 📁 Cấu trúc file

```
data/
├── 00_run_all_data.sql      # File master chạy tất cả
├── 01_schema_and_basic_data.sql  # Categories, Publishers, Authors, Admins
├── 02_customers_data.sql    # Customers, Addresses
├── 03_books_data.sql        # Books, Book-Author relations
├── 04_book_images_reviews.sql    # Book Images, Reviews
├── 05_orders_payments.sql   # Shopping Carts, Orders, Payments
└── README.md
```

## 🚀 Hướng dẫn sử dụng

### Cách 1: Sử dụng psql command line

```bash
# Chạy tất cả từ file master
cd src/main/resources/data
psql -U postgres -d bookify_db -f 00_run_all_data.sql

# Hoặc chạy từng file riêng lẻ
psql -U postgres -d bookify_db -f 01_schema_and_basic_data.sql
psql -U postgres -d bookify_db -f 02_customers_data.sql
psql -U postgres -d bookify_db -f 03_books_data.sql
psql -U postgres -d bookify_db -f 04_book_images_reviews.sql
psql -U postgres -d bookify_db -f 05_orders_payments.sql
```

### Cách 2: Sử dụng pgAdmin

1. Mở pgAdmin và kết nối đến database
2. Click chuột phải vào database `bookify_db`
3. Chọn **Query Tool**
4. Mở và chạy từng file SQL theo thứ tự

### Cách 3: Sử dụng DBeaver

1. Kết nối đến PostgreSQL database
2. Mở SQL Editor
3. File → Open → chọn file SQL
4. Execute (F5 hoặc Ctrl+Enter)

## ⚙️ Lưu ý quan trọng

1. **Thứ tự chạy**: Phải chạy các file theo đúng thứ tự (01 → 02 → 03 → 04 → 05) do ràng buộc khóa ngoại.

2. **Schema**: Đảm bảo đã tạo schema/tables trước khi import data. Nếu sử dụng JPA/Hibernate với `spring.jpa.hibernate.ddl-auto=create`, schema sẽ được tạo tự động.

3. **Reset data**: Nếu muốn chạy lại từ đầu:
```sql
-- Xóa tất cả dữ liệu (giữ schema)
TRUNCATE TABLE payments, order_details, orders, cart_items, shopping_carts, 
         reviews, book_images, book_authors, books, addresses, customers, 
         admins, users, authors, publishers, categories CASCADE;
```

4. **Foreign Key Checks**: Các file SQL đã có lệnh tắt/bật kiểm tra khóa ngoại để import nhanh hơn.

## 📚 Chi tiết dữ liệu

### Categories (15 danh mục)
- Văn học Việt Nam
- Văn học nước ngoài  
- Tiểu thuyết
- Truyện ngắn
- Kinh tế - Kinh doanh
- Kỹ năng sống
- Tâm lý - Tâm linh
- Khoa học - Công nghệ
- Lịch sử
- Thiếu nhi
- Manga - Comic
- Sách giáo khoa
- Ngoại ngữ
- Y học - Sức khỏe
- Nấu ăn - Ẩm thực

### Books (150 sách)
Bao gồm các tác phẩm nổi tiếng:
- **Văn học Việt Nam**: Nguyễn Nhật Ánh, Nam Cao, Ngô Tất Tố...
- **Văn học nước ngoài**: Murakami, Dostoevsky, Tolstoy...
- **Self-help**: Đắc nhân tâm, Nghĩ giàu làm giàu...
- **Khoa học**: Sapiens, Homo Deus...
- **Manga**: One Piece, Naruto, Dragon Ball, Conan...

### Orders
- Phân bố từ 01/2023 đến 11/2024
- Các trạng thái: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
- Phương thức thanh toán: COD, MOMO, VNPAY, CREDIT_CARD

## 🔐 Tài khoản mặc định

### Admin
| Email | Password (hashed) |
|-------|-------------------|
| admin@bookify.vn | (bcrypt hash) |
| manager@bookify.vn | (bcrypt hash) |

### Customer
| Email | Password (hashed) |
|-------|-------------------|
| nguyenvana@gmail.com | (bcrypt hash) |
| tranthib@gmail.com | (bcrypt hash) |
| ... (100 tài khoản) | |

> **Note**: Password đã được hash bằng BCrypt. Để test, bạn có thể cập nhật password sau khi import.

## 📈 Dữ liệu cho Analytics

Dữ liệu orders được thiết kế để hỗ trợ:
- Thống kê doanh thu theo tháng/năm
- Phân tích sản phẩm bán chạy
- Phân tích hành vi khách hàng
- Thống kê đánh giá sản phẩm


