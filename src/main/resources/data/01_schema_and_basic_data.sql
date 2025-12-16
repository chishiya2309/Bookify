-- =====================================================
-- BOOKIFY DATABASE - SAMPLE DATA FOR POSTGRESQL
-- Part 1: Categories, Publishers, Authors
-- =====================================================

-- Disable foreign key checks for bulk insert
SET session_replication_role = replica;

-- =====================================================
-- CATEGORIES (15 categories)
-- =====================================================
INSERT INTO categories (name) VALUES
('Văn học Việt Nam'),
('Văn học nước ngoài'),
('Tiểu thuyết'),
('Truyện ngắn'),
('Kinh tế - Kinh doanh'),
('Kỹ năng sống'),
('Tâm lý - Tâm linh'),
('Khoa học - Công nghệ'),
('Lịch sử'),
('Thiếu nhi'),
('Manga - Comic'),
('Sách giáo khoa'),
('Ngoại ngữ'),
('Y học - Sức khỏe'),
('Nấu ăn - Ẩm thực');

-- =====================================================
-- PUBLISHERS (25 publishers)
-- =====================================================
INSERT INTO publishers (name, address, contact_email, website) VALUES
('Nhà xuất bản Kim Đồng', 'Số 55 Quang Trung, Phường Hai Bà Trưng, Thành phố Hà Nội ', 'cskh_online@nxbkimdong.com.vn', 'https://nxbkimdong.com.vn'),
('Nhà xuất bản Trẻ', '161B Lý Chính Thắng, Phường Xuân Hoà , TP. Hồ Chí Minh ', 'contact@nxbtre.com.vn', 'hopthubandoc@nxbtre.com.vn'),
('Nhà xuất bản Văn học', '18 Nguyễn Trường Tộ, Ba Đình, Hà Nội', 'info@nxbvanhoc.com.vn', 'https://nxbvanhoc.com.vn'),
('Nhà xuất bản Tổng hợp TP.HCM', '62 Nguyễn Thị Minh Khai, Phường Sài Gòn, TPHCM ', 'tonghop@nxbhcm.com.vn', 'https://nxbhcm.com.vn'),
('Nhà xuất bản Hội Nhà văn', ' Số 65 Nguyễn Du, Phường Hai Bà Trưng, Hà Nội', 'lienhe@nxbhoinhavan.vn', 'https://nxbhoinhavan.vn'),
('Nhà xuất bản Phụ nữ', '39 Hàng Chuối, Hai Bà Trưng, Hà Nội', 'truyenthongvaprnxbpn@gmail.com', 'https://nxbphunu.com.vn'),
('Nhà xuất bản Lao động', 'Tầng 12 - Số 175 Đường Giảng Võ - Phường Ô Chợ Dừa - Thành phố Hà Nội', 'info@nxblaodong.com.vn', 'https://nxblaodong.com.vn/'),
('Nhà xuất bản Thanh niên', '61 Bà Triệu, Hoàn Kiếm, Hà Nội', 'info@nxbthanhnien.vn', 'https://nhaxuatbanthanhnien.vn/'),
('Nhà xuất bản Thế giới', '59 Thợ Nhuộm, Hoàn Kiếm, Hà Nội', 'nxbthegioi@gmail.com', 'https://thegioipublishers.vn'),
('Nhà xuất bản Đại học Quốc gia Hà Nội', '16 Hàng Chuối, Hai Bà Trưng, Hà Nội, Vietnam', 'nxb@vnu.edu.vn', 'https://vnupress.edu.vn/trang-chu'),
('First News - Trí Việt', '11 I-H Nguyễn Thị Minh Khai, phường Sài Gòn, Thành phố Hồ Chí Minh ', 'triviet@firstnews.com.vn', 'https://firstnews.vn/'),
('Alphabooks', 'Tầng 3, Dream Home Center, 11a ngõ 282 Nguyễn Huy Tưởng, Thanh Xuân, Hà Nội', 'mkt.alphabooks@gmail.com', 'https://www.alphabooks.vn/'),
('Nhã Nam', 'Số 59, Đỗ Quang, Trung Hoà, Cầu Giấy, Hà Nội', 'info@nhanam.vn', 'https://nhanam.vn/'),
('Saigon Books', '1 Nguyễn Văn Bình, Quận 1, TPHCM', 'nhasach@saigonbook.com.vn', 'https://saigonbooks.vn/'),
('Thái Hà Books', 'Lô B2, khu đấu giá 3ha, TDP số 1, Phường Phú Diễn, Thành phố Hà Nội', 'Sachthaiha@thaihabooks.com', 'https://thaihabooks.com/'),
('Minh Long', 'LK 02 - 03, Dãy B, KĐT Green Pearl, 378 Minh Khai, Hai Bà Trưng, Hà Nội', 'cskh@minhlongbook.vn', 'https://minhlongbook.vn/'),
('Skybooks', 'Số 83 Lý Nam Đế, Phường Cửa Đông, Quận Hoàn Kiếm, Hà Nội', 'contact.skybooks@gmail.com', 'https://skybooks.vn/'),
('1980 Books', 'Nhà 20H2, Ngõ 6, Trần Kim Xuyến, Phường Yên Hòa, Quận Cầu Giấy, Hà Nội', 'info.1980books@gmail.com', 'https://1980books.com/'),
('Đinh Tị Books', 'Số 78 đường số 1, Phường Hạnh Thông, Thành phố Hồ Chí Minh', 'contacts@dinhtibooks.vn', 'https://dinhtibooks.com.vn/'),
('Penguin Random House Vietnam', '123 Pasteur, Quận 1, TP.HCM', 'vietnam@penguinrandomhouse.com', 'https://penguinrandomhouse.com.vn'),
('Hachette Vietnam', '456 Lê Duẩn, Quận 1, TP.HCM', 'vietnam@hachette.com', 'https://hachette.vn'),
('HarperCollins Vietnam', '789 Nguyễn Huệ, Quận 1, TP.HCM', 'vietnam@harpercollins.com', 'https://harpercollins.vn'),
('Nhà xuất bản Chính trị quốc gia Sự thật', '6/86 Duy Tân, Cầu Giấy, Hà Nội', 'phathanh@nxbctqg.vn', 'https://www.nxbctqg.org.vn/'),
('Nhà xuất bản Giáo dục Việt Nam', 'Số 81 Trần Hưng Đạo, Phường Cửa Nam,TP. Hà Nội', 'veph@nxbgd.vn', 'https://nxbgd.vn/'),
('Nhà xuất bản Dân trí', 'Số 9, ngõ 26, phố Hoàng Cầu, phường Ô Chợ Dừa, quận Đống Đa, Hà Nội', 'nxbdantri@gmail.com', 'https://nxbdantri.com.vn/');

-- =====================================================
-- AUTHORS (50 authors)
-- =====================================================
INSERT INTO authors (name, biography, photo_url) VALUES
('Nguyễn Nhật Ánh', 'Nguyễn Nhật Ánh (sinh 1955) là một nhà văn Việt Nam chuyên viết cho tuổi mới lớn. Ông được biết đến nhiều nhất với các tác phẩm Cho tôi xin một vé đi tuổi thơ, Mắt biếc, Tôi thấy hoa vàng trên cỏ xanh...', 'https://images.bookify.vn/authors/nguyen-nhat-anh.jpg'),
('Nam Cao', 'Nam Cao (1915-1951) là một trong những nhà văn xuất sắc nhất của nền văn học hiện thực Việt Nam. Các tác phẩm nổi tiếng: Chí Phèo, Lão Hạc, Đời thừa...', 'https://images.bookify.vn/authors/nam-cao.jpg'),
('Ngô Tất Tố', 'Ngô Tất Tố (1893-1954) là nhà văn, nhà báo, nhà dịch thuật Việt Nam. Tác phẩm tiêu biểu: Tắt đèn, Việc làng, Lều chõng...', 'https://images.bookify.vn/authors/ngo-tat-to.jpg'),
('Nguyễn Du', 'Nguyễn Du (1766-1820) là đại thi hào dân tộc Việt Nam, tác giả của Truyện Kiều - kiệt tác văn học cổ điển Việt Nam.', 'https://images.bookify.vn/authors/nguyen-du.jpg'),
('Vũ Trọng Phụng', 'Vũ Trọng Phụng (1912-1939) là nhà văn, nhà báo Việt Nam. Các tác phẩm nổi tiếng: Số đỏ, Giông tố, Vỡ đê...', 'https://images.bookify.vn/authors/vu-trong-phung.jpg'),
('Tô Hoài', 'Tô Hoài (1920-2014) là nhà văn Việt Nam với sự nghiệp sáng tác đồ sộ. Tác phẩm tiêu biểu: Dế mèn phiêu lưu ký, O chuột...', 'https://images.bookify.vn/authors/to-hoai.jpg'),
('Nguyễn Tuân', 'Nguyễn Tuân (1910-1987) là nhà văn Việt Nam với phong cách độc đáo. Tác phẩm nổi bật: Vang bóng một thời, Người lái đò sông Đà...', 'https://images.bookify.vn/authors/nguyen-tuan.jpg'),
('Xuân Diệu', 'Xuân Diệu (1916-1985) được mệnh danh là ông hoàng thơ tình Việt Nam. Các tập thơ: Thơ thơ, Gửi hương cho gió...', 'https://images.bookify.vn/authors/xuan-dieu.jpg'),
('Hàn Mặc Tử', 'Hàn Mặc Tử (1912-1940) là nhà thơ nổi tiếng với những bài thơ về tình yêu và tôn giáo. Tác phẩm: Gái quê, Thơ điên...', 'https://images.bookify.vn/authors/han-mac-tu.jpg'),
('Nguyễn Bính', 'Nguyễn Bính (1918-1966) là nhà thơ nổi tiếng với những bài thơ lục bát dân dã. Tác phẩm: Lỡ bước sang ngang, Tương tư...', 'https://images.bookify.vn/authors/nguyen-binh.jpg'),
('Haruki Murakami', 'Haruki Murakami (sinh 1949) là nhà văn Nhật Bản nổi tiếng thế giới với nhiều tác phẩm được dịch ra hàng chục thứ tiếng.', 'https://images.bookify.vn/authors/haruki-murakami.jpg'),
('Gabriel García Márquez', 'Gabriel García Márquez (1927-2014) là nhà văn Colombia, chủ nhân giải Nobel Văn học 1982, tác giả Trăm năm cô đơn.', 'https://images.bookify.vn/authors/garcia-marquez.jpg'),
('Paulo Coelho', 'Paulo Coelho (sinh 1947) là nhà văn Brazil nổi tiếng với tác phẩm Nhà giả kim, sách bán chạy nhất mọi thời đại.', 'https://images.bookify.vn/authors/paulo-coelho.jpg'),
('J.K. Rowling', 'J.K. Rowling (sinh 1965) là nhà văn Anh, tác giả của series Harry Potter bán hơn 500 triệu bản trên toàn thế giới.', 'https://images.bookify.vn/authors/jk-rowling.jpg'),
('Stephen King', 'Stephen King (sinh 1947) là nhà văn Mỹ được mệnh danh là vua truyện kinh dị với hơn 60 tiểu thuyết.', 'https://images.bookify.vn/authors/stephen-king.jpg'),
('Dale Carnegie', 'Dale Carnegie (1888-1955) là nhà văn và nhà giáo dục Mỹ, tác giả của cuốn sách kinh điển Đắc nhân tâm.', 'https://images.bookify.vn/authors/dale-carnegie.jpg'),
('Napoleon Hill', 'Napoleon Hill (1883-1970) là tác giả người Mỹ, nổi tiếng với sách Nghĩ giàu làm giàu.', 'https://images.bookify.vn/authors/napoleon-hill.jpg'),
('Robert Kiyosaki', 'Robert Kiyosaki (sinh 1947) là doanh nhân, tác giả người Mỹ, nổi tiếng với sách Cha giàu cha nghèo.', 'https://images.bookify.vn/authors/robert-kiyosaki.jpg'),
('Sun Tzu', 'Tôn Tử là nhà chiến lược quân sự Trung Quốc cổ đại, tác giả Binh pháp Tôn Tử.', 'https://images.bookify.vn/authors/sun-tzu.jpg'),
('Yuval Noah Harari', 'Yuval Noah Harari (sinh 1976) là giáo sư sử học người Israel, tác giả Sapiens: Lược sử loài người.', 'https://images.bookify.vn/authors/yuval-harari.jpg'),
('Thích Nhất Hạnh', 'Thích Nhất Hạnh (1926-2022) là thiền sư Phật giáo Việt Nam nổi tiếng thế giới với nhiều tác phẩm về tâm linh.', 'https://images.bookify.vn/authors/thich-nhat-hanh.jpg'),
('Osho', 'Osho (1931-1990) là nhà thuyết giảng tâm linh người Ấn Độ với nhiều tác phẩm về thiền định và tự do.', 'https://images.bookify.vn/authors/osho.jpg'),
('Eiichiro Oda', 'Eiichiro Oda (sinh 1975) là họa sĩ manga Nhật Bản, tác giả One Piece - manga bán chạy nhất lịch sử.', 'https://images.bookify.vn/authors/eiichiro-oda.jpg'),
('Masashi Kishimoto', 'Masashi Kishimoto (sinh 1974) là họa sĩ manga Nhật Bản, tác giả series Naruto.', 'https://images.bookify.vn/authors/masashi-kishimoto.jpg'),
('Akira Toriyama', 'Akira Toriyama (1955-2024) là họa sĩ manga huyền thoại, tác giả Dragon Ball và Dr. Slump.', 'https://images.bookify.vn/authors/akira-toriyama.jpg'),
('Gosho Aoyama', 'Gosho Aoyama (sinh 1963) là họa sĩ manga Nhật Bản, tác giả series Thám tử lừng danh Conan.', 'https://images.bookify.vn/authors/gosho-aoyama.jpg'),
('Trần Đức Thảo', 'Trần Đức Thảo (1917-1993) là triết gia Việt Nam nổi tiếng với các công trình về hiện tượng học.', 'https://images.bookify.vn/authors/tran-duc-thao.jpg'),
('Nguyễn Huy Thiệp', 'Nguyễn Huy Thiệp (1950-2021) là nhà văn Việt Nam tiêu biểu của văn học đổi mới. Tác phẩm: Tướng về hưu, Vàng lửa...', 'https://images.bookify.vn/authors/nguyen-huy-thiep.jpg'),
('Bảo Ninh', 'Bảo Ninh (sinh 1952) là nhà văn Việt Nam, tác giả Nỗi buồn chiến tranh - tiểu thuyết được đánh giá cao quốc tế.', 'https://images.bookify.vn/authors/bao-ninh.jpg'),
('Dương Thu Hương', 'Dương Thu Hương (sinh 1947) là nhà văn Việt Nam với nhiều tác phẩm gây tiếng vang trong và ngoài nước.', 'https://images.bookify.vn/authors/duong-thu-huong.jpg'),
('Fyodor Dostoevsky', 'Fyodor Dostoevsky (1821-1881) là nhà văn Nga vĩ đại, tác giả của Tội ác và hình phạt, Anh em nhà Karamazov.', 'https://images.bookify.vn/authors/dostoevsky.jpg'),
('Leo Tolstoy', 'Leo Tolstoy (1828-1910) là nhà văn Nga, tác giả của Chiến tranh và hòa bình, Anna Karenina.', 'https://images.bookify.vn/authors/tolstoy.jpg'),
('Ernest Hemingway', 'Ernest Hemingway (1899-1961) là nhà văn Mỹ, chủ nhân giải Nobel Văn học 1954. Tác phẩm: Ông già và biển cả...', 'https://images.bookify.vn/authors/hemingway.jpg'),
('Franz Kafka', 'Franz Kafka (1883-1924) là nhà văn người Bohemia viết tiếng Đức. Tác phẩm: Hóa thân, Vụ án...', 'https://images.bookify.vn/authors/kafka.jpg'),
('Albert Camus', 'Albert Camus (1913-1960) là nhà văn và triết gia Pháp gốc Algeria, chủ nhân giải Nobel Văn học 1957.', 'https://images.bookify.vn/authors/camus.jpg'),
('Antoine de Saint-Exupéry', 'Antoine de Saint-Exupéry (1900-1944) là nhà văn và phi công Pháp, tác giả Hoàng tử bé.', 'https://images.bookify.vn/authors/saint-exupery.jpg'),
('Agatha Christie', 'Agatha Christie (1890-1976) là nữ nhà văn trinh thám Anh với hơn 80 tiểu thuyết.', 'https://images.bookify.vn/authors/agatha-christie.jpg'),
('George Orwell', 'George Orwell (1903-1950) là nhà văn Anh, tác giả 1984 và Trại súc vật.', 'https://images.bookify.vn/authors/george-orwell.jpg'),
('Mark Twain', 'Mark Twain (1835-1910) là nhà văn Mỹ, tác giả Những cuộc phiêu lưu của Tom Sawyer và Huckleberry Finn.', 'https://images.bookify.vn/authors/mark-twain.jpg'),
('Jane Austen', 'Jane Austen (1775-1817) là nữ nhà văn Anh với các tác phẩm kinh điển như Kiêu hãnh và định kiến.', 'https://images.bookify.vn/authors/jane-austen.jpg'),
('Victor Hugo', 'Victor Hugo (1802-1885) là nhà văn, nhà thơ Pháp, tác giả Những người khốn khổ, Nhà thờ Đức Bà Paris.', 'https://images.bookify.vn/authors/victor-hugo.jpg'),
('Charles Dickens', 'Charles Dickens (1812-1870) là nhà văn Anh thời đại Victoria. Tác phẩm: Oliver Twist, Hai thành phố...', 'https://images.bookify.vn/authors/charles-dickens.jpg'),
('William Shakespeare', 'William Shakespeare (1564-1616) là nhà soạn kịch và nhà thơ vĩ đại nhất của văn học Anh.', 'https://images.bookify.vn/authors/shakespeare.jpg'),
('Oscar Wilde', 'Oscar Wilde (1854-1900) là nhà văn và nhà thơ Ireland nổi tiếng với lối viết châm biếm.', 'https://images.bookify.vn/authors/oscar-wilde.jpg'),
('Edgar Allan Poe', 'Edgar Allan Poe (1809-1849) là nhà văn Mỹ, được coi là cha đẻ của truyện trinh thám hiện đại.', 'https://images.bookify.vn/authors/edgar-poe.jpg'),
('Hermann Hesse', 'Hermann Hesse (1877-1962) là nhà văn Đức-Thụy Sĩ, chủ nhân giải Nobel Văn học 1946.', 'https://images.bookify.vn/authors/hermann-hesse.jpg'),
('Khalil Gibran', 'Khalil Gibran (1883-1931) là nhà thơ, nhà văn Lebanon-Mỹ, tác giả Nhà tiên tri.', 'https://images.bookify.vn/authors/khalil-gibran.jpg'),
('Dan Brown', 'Dan Brown (sinh 1964) là nhà văn Mỹ nổi tiếng với series Robert Langdon, bao gồm Mật mã Da Vinci.', 'https://images.bookify.vn/authors/dan-brown.jpg'),
('John Grisham', 'John Grisham (sinh 1955) là nhà văn Mỹ chuyên thể loại pháp đình với hơn 40 tiểu thuyết.', 'https://images.bookify.vn/authors/john-grisham.jpg'),
('Nguyên Phong', 'Nguyên Phong là bút danh của tác giả chuyên dịch và viết sách tâm linh như Hành trình về phương Đông.', 'https://images.bookify.vn/authors/nguyen-phong.jpg');

-- =====================================================
-- USERS - ADMINS (5 admins)
-- =====================================================
INSERT INTO users (email, password, full_name, user_type) VALUES
('admin@bookify.vn', '$2a$12$RcFtv5.ihBYr2Wjl3ZRL7uPD/Hb0VaEBKJcIbq8jtclsw3hRX7lFS', 'Admin Hệ Thống', 'ADMIN'),
('manager@bookify.vn', '$2a$12$h2iZITh5h3l/ZuinJvNuRuEUGtJl6HM3bL6o2Sydu74VIHf0dsWoy', 'Quản Lý Cửa Hàng', 'ADMIN'),
('warehouse@bookify.vn', '$2a$12$qP6CP42zi2XPmcHNAKeRtOnGbalD56U.m06uMX4KGINjUn3U.cNkq', 'Quản Lý Kho', 'ADMIN'),
('support@bookify.vn', '$2a$12$F5A1nZ2iTIoDmMo0NQqcaeFEGtBSqcPl.dVab6.JqLdYFjO6OQpui', 'Hỗ Trợ Khách Hàng', 'ADMIN'),
('marketing@bookify.vn', '$2a$12$1odIGjqJFsCSRLrU9kkreOGFjfufGlVK3wsf9SvP/U5vciaO3/hPq', 'Marketing Manager', 'ADMIN');

-- Insert admin records
INSERT INTO admins (user_id) VALUES (1), (2), (3), (4), (5);


