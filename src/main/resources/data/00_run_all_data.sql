-- =====================================================
-- BOOKIFY DATABASE - MASTER SEED FILE FOR POSTGRESQL
-- =====================================================
-- 
-- Hướng dẫn sử dụng:
-- 1. Đảm bảo database đã được tạo và schema đã được migrate
-- 2. Chạy các file SQL theo thứ tự sau:
--    - 01_schema_and_basic_data.sql
--    - 02_customers_data.sql  
--    - 03_books_data.sql
--    - 04_book_images_reviews.sql
--    - 05_orders_payments.sql
--
-- Hoặc chạy file này để import tất cả:
-- psql -U username -d bookify_db -f 00_run_all_data.sql
--
-- =====================================================

-- Disable foreign key checks for bulk insert
SET session_replication_role = replica;

-- =====================================================
-- Chạy từng file theo thứ tự
-- =====================================================

\echo 'Importing schema and basic data...'
\i '01_schema_and_basic_data.sql'

\echo 'Importing customers data...'
\i '02_customers_data.sql'

\echo 'Importing books data...'
\i '03_books_data.sql'

\echo 'Importing book images and reviews...'
\i '04_book_images_reviews.sql'

\echo 'Importing orders and payments...'
\i '05_orders_payments.sql'

-- Re-enable foreign key checks
SET session_replication_role = DEFAULT;

\echo 'Data import completed successfully!'

-- =====================================================
-- THỐNG KÊ DỮ LIỆU SAU KHI IMPORT
-- =====================================================

\echo ''
\echo '========== DATA STATISTICS =========='

SELECT 'Categories' as table_name, COUNT(*) as row_count FROM categories
UNION ALL
SELECT 'Publishers', COUNT(*) FROM publishers
UNION ALL
SELECT 'Authors', COUNT(*) FROM authors
UNION ALL
SELECT 'Users (Total)', COUNT(*) FROM users
UNION ALL
SELECT 'Admins', COUNT(*) FROM admins
UNION ALL
SELECT 'Customers', COUNT(*) FROM customers
UNION ALL
SELECT 'Addresses', COUNT(*) FROM addresses
UNION ALL
SELECT 'Books', COUNT(*) FROM books
UNION ALL
SELECT 'Book-Author Relations', COUNT(*) FROM book_authors
UNION ALL
SELECT 'Book Images', COUNT(*) FROM book_images
UNION ALL
SELECT 'Reviews', COUNT(*) FROM reviews
UNION ALL
SELECT 'Shopping Carts', COUNT(*) FROM shopping_carts
UNION ALL
SELECT 'Cart Items', COUNT(*) FROM cart_items
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'Order Details', COUNT(*) FROM order_details
UNION ALL
SELECT 'Payments', COUNT(*) FROM payments
ORDER BY table_name;

\echo '======================================'


