-- =====================================================
-- BOOKIFY DATABASE - SAMPLE DATA FOR POSTGRESQL
-- Part 3: Books (150 books)
-- =====================================================

INSERT INTO books (title, description, isbn, price, quantity_in_stock, publish_date, last_updated, category_id, publisher_id) VALUES
-- Văn học Việt Nam (category_id = 1)
('Cho tôi xin một vé đi tuổi thơ', 'Tác phẩm đưa người đọc trở về những ngày tháng tuổi thơ trong sáng với những kỷ niệm đẹp đẽ nhất.', '9786041234567', 85000.00, 150, '2020-01-15', '2024-11-01', 1, 2),
('Mắt biếc', 'Câu chuyện tình yêu đơn phương da diết của chàng trai tên Ngạn dành cho cô gái làng Đo Đo tên Hà Lan.', '9786041234568', 95000.00, 200, '2019-06-20', '2024-11-01', 1, 2),
('Tôi thấy hoa vàng trên cỏ xanh', 'Câu chuyện về tình anh em, tình bạn, tình yêu tuổi mới lớn ở một vùng quê nghèo miền Trung.', '9786041234569', 89000.00, 180, '2018-03-10', '2024-11-01', 1, 2),
('Kính vạn hoa', 'Bộ truyện nổi tiếng về tuổi học trò với những câu chuyện hài hước, dí dỏm.', '9786041234570', 75000.00, 120, '2017-08-25', '2024-11-01', 1, 2),
('Cô gái đến từ hôm qua', 'Tiểu thuyết lãng mạn về mối tình học trò trong sáng.', '9786041234571', 82000.00, 90, '2020-05-15', '2024-11-01', 1, 2),
('Ngồi khóc trên cây', 'Câu chuyện cảm động về tuổi thơ và những ký ức không phai.', '9786041234572', 78000.00, 110, '2019-09-20', '2024-11-01', 1, 2),
('Chí Phèo', 'Kiệt tác văn học hiện thực phê phán của Nam Cao về số phận bi kịch của người nông dân.', '9786041234573', 45000.00, 300, '2015-01-01', '2024-11-01', 1, 3),
('Lão Hạc', 'Truyện ngắn xúc động về người nông dân nghèo khổ và lòng tự trọng cao quý.', '9786041234574', 35000.00, 250, '2015-01-01', '2024-11-01', 1, 3),
('Tắt đèn', 'Tiểu thuyết về cuộc sống khốn khổ của người nông dân trong xã hội phong kiến.', '9786041234575', 65000.00, 180, '2016-06-15', '2024-11-01', 1, 3),
('Số đỏ', 'Tiểu thuyết trào phúng đả kích xã hội thượng lưu thối nát.', '9786041234576', 72000.00, 150, '2017-03-20', '2024-11-01', 1, 3),

-- Văn học nước ngoài (category_id = 2)
('Trăm năm cô đơn', 'Kiệt tác của Gabriel García Márquez về dòng họ Buendía qua 7 thế hệ.', '9786041234577', 145000.00, 100, '2018-09-01', '2024-11-01', 2, 13),
('Rừng Na Uy', 'Tiểu thuyết nổi tiếng của Haruki Murakami về tình yêu, mất mát và trưởng thành.', '9786041234578', 125000.00, 120, '2019-02-14', '2024-11-01', 2, 13),
('Kafka bên bờ biển', 'Cuốn tiểu thuyết ma thuật hiện thực của Murakami.', '9786041234579', 135000.00, 80, '2019-07-20', '2024-11-01', 2, 13),
('1Q84', 'Bộ ba tiểu thuyết đồ sộ nhất của Haruki Murakami.', '9786041234580', 285000.00, 60, '2020-01-10', '2024-11-01', 2, 13),
('Tội ác và hình phạt', 'Kiệt tác của Dostoevsky về cuộc đấu tranh nội tâm của một kẻ giết người.', '9786041234581', 155000.00, 90, '2018-05-15', '2024-11-01', 2, 9),
('Anh em nhà Karamazov', 'Tiểu thuyết vĩ đại cuối cùng của Dostoevsky.', '9786041234582', 185000.00, 70, '2019-08-20', '2024-11-01', 2, 9),
('Chiến tranh và hòa bình', 'Sử thi văn học của Leo Tolstoy về nước Nga thời Napoleon.', '9786041234583', 245000.00, 50, '2017-11-11', '2024-11-01', 2, 9),
('Anna Karenina', 'Bi kịch tình yêu nổi tiếng của Leo Tolstoy.', '9786041234584', 195000.00, 65, '2018-02-28', '2024-11-01', 2, 9),
('Ông già và biển cả', 'Kiệt tác của Hemingway đoạt giải Nobel Văn học.', '9786041234585', 75000.00, 200, '2019-04-15', '2024-11-01', 2, 9),
('Những người khốn khổ', 'Kiệt tác của Victor Hugo về công lý và tình thương.', '9786041234586', 225000.00, 80, '2018-07-14', '2024-11-01', 2, 9),

-- Tiểu thuyết (category_id = 3)
('Nhà giả kim', 'Tiểu thuyết triết lý của Paulo Coelho về hành trình tìm kiếm giấc mơ.', '9786041234587', 79000.00, 300, '2019-03-21', '2024-11-01', 3, 11),
('Hoàng tử bé', 'Câu chuyện triết lý về tình bạn và tình yêu của Antoine de Saint-Exupéry.', '9786041234588', 65000.00, 400, '2018-06-29', '2024-11-01', 3, 13),
('Đi tìm lẽ sống', 'Tác phẩm của Viktor Frankl về ý nghĩa cuộc sống.', '9786041234589', 89000.00, 150, '2020-02-10', '2024-11-01', 3, 11),
('Nỗi buồn chiến tranh', 'Tiểu thuyết nổi tiếng của Bảo Ninh về chiến tranh Việt Nam.', '9786041234590', 95000.00, 100, '2019-04-30', '2024-11-01', 3, 5),
('Harry Potter và hòn đá phù thủy', 'Tập đầu tiên trong series Harry Potter của J.K. Rowling.', '9786041234591', 145000.00, 200, '2020-07-31', '2024-11-01', 3, 2),
('Harry Potter và phòng chứa bí mật', 'Tập 2 trong series Harry Potter.', '9786041234592', 155000.00, 180, '2020-07-31', '2024-11-01', 3, 2),
('Harry Potter và tên tù nhân ngục Azkaban', 'Tập 3 trong series Harry Potter.', '9786041234593', 165000.00, 170, '2020-07-31', '2024-11-01', 3, 2),
('Harry Potter và chiếc cốc lửa', 'Tập 4 trong series Harry Potter.', '9786041234594', 195000.00, 150, '2020-07-31', '2024-11-01', 3, 2),
('Mật mã Da Vinci', 'Tiểu thuyết trinh thám bestseller của Dan Brown.', '9786041234595', 135000.00, 120, '2019-05-18', '2024-11-01', 3, 20),
('Thiên thần và ác quỷ', 'Tiểu thuyết trinh thám của Dan Brown về cuộc chiến giữa khoa học và tôn giáo.', '9786041234596', 145000.00, 100, '2019-06-15', '2024-11-01', 3, 20),

-- Truyện ngắn (category_id = 4)
('Truyện Kiều', 'Kiệt tác văn học cổ điển Việt Nam của đại thi hào Nguyễn Du.', '9786041234597', 55000.00, 500, '2015-09-16', '2024-11-01', 4, 3),
('Vang bóng một thời', 'Tập truyện ngắn nổi tiếng của Nguyễn Tuân.', '9786041234598', 68000.00, 120, '2018-11-20', '2024-11-01', 4, 3),
('Dế mèn phiêu lưu ký', 'Tác phẩm kinh điển của Tô Hoài dành cho thiếu nhi.', '9786041234599', 45000.00, 350, '2016-06-01', '2024-11-01', 4, 1),
('Tuyển tập truyện ngắn Nguyễn Huy Thiệp', 'Tập hợp những truyện ngắn xuất sắc nhất của Nguyễn Huy Thiệp.', '9786041234600', 95000.00, 80, '2020-08-15', '2024-11-01', 4, 5),
('Tuyển tập Nam Cao', 'Những tác phẩm tiêu biểu nhất của nhà văn Nam Cao.', '9786041234601', 85000.00, 150, '2017-05-05', '2024-11-01', 4, 3),

-- Kinh tế - Kinh doanh (category_id = 5)
('Đắc nhân tâm', 'Cuốn sách kinh điển về nghệ thuật đối nhân xử thế của Dale Carnegie.', '9786041234602', 86000.00, 500, '2019-01-01', '2024-11-01', 5, 11),
('Nghĩ giàu làm giàu', 'Sách kinh điển về tư duy làm giàu của Napoleon Hill.', '9786041234603', 95000.00, 400, '2019-02-15', '2024-11-01', 5, 11),
('Cha giàu cha nghèo', 'Bài học tài chính từ Robert Kiyosaki.', '9786041234604', 115000.00, 350, '2019-04-10', '2024-11-01', 5, 11),
('Binh pháp Tôn Tử', 'Chiến lược cổ đại áp dụng cho kinh doanh hiện đại.', '9786041234605', 75000.00, 200, '2018-08-15', '2024-11-01', 5, 15),
('Từ tốt đến vĩ đại', 'Nghiên cứu về các công ty vượt trội của Jim Collins.', '9786041234606', 145000.00, 150, '2020-03-20', '2024-11-01', 5, 12),
('7 thói quen của người thành đạt', 'Cuốn sách kinh điển của Stephen Covey về phát triển cá nhân.', '9786041234607', 125000.00, 180, '2019-06-01', '2024-11-01', 5, 11),
('Người giàu có nhất thành Babylon', 'Những bài học tài chính cổ xưa vẫn còn giá trị.', '9786041234608', 68000.00, 250, '2018-11-11', '2024-11-01', 5, 15),
('Khởi nghiệp tinh gọn', 'Phương pháp khởi nghiệp của Eric Ries.', '9786041234609', 135000.00, 120, '2020-05-15', '2024-11-01', 5, 12),
('Bí mật tư duy triệu phú', 'Cách thay đổi tư duy để đạt thành công tài chính.', '9786041234610', 98000.00, 200, '2019-09-20', '2024-11-01', 5, 15),
('Zero to One', 'Bí quyết khởi nghiệp của Peter Thiel.', '9786041234611', 115000.00, 150, '2020-07-01', '2024-11-01', 5, 12),

-- Kỹ năng sống (category_id = 6)
('Đời ngắn đừng ngủ dài', 'Sách về quản lý thời gian và sống có mục đích của Robin Sharma.', '9786041234612', 79000.00, 300, '2019-01-15', '2024-11-01', 6, 11),
('Tuổi trẻ đáng giá bao nhiêu', 'Sách dành cho tuổi trẻ Việt Nam của Rosie Nguyễn.', '9786041234613', 69000.00, 400, '2018-08-20', '2024-11-01', 6, 4),
('Không gia đình', 'Câu chuyện về lòng kiên trì và tình người.', '9786041234614', 85000.00, 180, '2017-12-25', '2024-11-01', 6, 1),
('Hành trình về phương Đông', 'Cuốn sách tâm linh nổi tiếng dịch bởi Nguyên Phong.', '9786041234615', 95000.00, 250, '2019-05-05', '2024-11-01', 6, 11),
('Nghệ thuật sống', 'Sách về chánh niệm và sống tỉnh thức của Thích Nhất Hạnh.', '9786041234616', 65000.00, 200, '2018-10-10', '2024-11-01', 6, 6),
('Phép màu buổi sáng', 'Thay đổi cuộc sống từ buổi sáng của Hal Elrod.', '9786041234617', 89000.00, 220, '2020-01-01', '2024-11-01', 6, 11),
('Sức mạnh của thói quen', 'Nghiên cứu về cách thay đổi thói quen của Charles Duhigg.', '9786041234618', 125000.00, 150, '2019-07-15', '2024-11-01', 6, 12),
('Dám nghĩ lớn', 'Sách về tư duy lớn của David J. Schwartz.', '9786041234619', 85000.00, 180, '2018-05-20', '2024-11-01', 6, 11),
('Bạn có thể đàm phán bất cứ điều gì', 'Kỹ năng đàm phán trong mọi tình huống.', '9786041234620', 95000.00, 140, '2019-11-11', '2024-11-01', 6, 12),
('Sống chậm để yêu thương', 'Triết lý sống chậm trong thời đại số.', '9786041234621', 72000.00, 160, '2020-04-15', '2024-11-01', 6, 4),

-- Tâm lý - Tâm linh (category_id = 7)
('Phép màu của sự thấu hiểu', 'Sách về tâm lý quan hệ của Thích Nhất Hạnh.', '9786041234622', 68000.00, 180, '2018-02-14', '2024-11-01', 7, 6),
('Bình an tức thì', 'Sách về thiền định và an lạc nội tâm.', '9786041234623', 75000.00, 200, '2019-03-20', '2024-11-01', 7, 6),
('Tâm lý học về sự thuyết phục', 'Khoa học về việc ảnh hưởng người khác.', '9786041234624', 115000.00, 120, '2020-06-10', '2024-11-01', 7, 12),
('Tư duy nhanh và chậm', 'Tác phẩm nổi tiếng của Daniel Kahneman về tâm lý học.', '9786041234625', 165000.00, 100, '2019-10-05', '2024-11-01', 7, 12),
('EQ - Trí tuệ cảm xúc', 'Sách về trí tuệ cảm xúc của Daniel Goleman.', '9786041234626', 145000.00, 130, '2018-12-12', '2024-11-01', 7, 11),
('Thiền - Nghệ thuật của sự tĩnh lặng', 'Hướng dẫn thiền định cho người mới bắt đầu.', '9786041234627', 55000.00, 250, '2019-08-08', '2024-11-01', 7, 15),
('Siddhartha', 'Tiểu thuyết triết học của Hermann Hesse.', '9786041234628', 65000.00, 180, '2017-09-15', '2024-11-01', 7, 13),
('Nhà tiên tri', 'Tác phẩm triết học của Khalil Gibran.', '9786041234629', 55000.00, 220, '2018-01-01', '2024-11-01', 7, 13),
('Yêu thương và tự do', 'Sách về giáo dục và tâm lý trẻ em của Osho.', '9786041234630', 78000.00, 140, '2019-05-15', '2024-11-01', 7, 15),
('Cái tôi và sự tự do', 'Sách triết học của Osho.', '9786041234631', 85000.00, 120, '2020-02-20', '2024-11-01', 7, 15),

-- Khoa học - Công nghệ (category_id = 8)
('Sapiens: Lược sử loài người', 'Tác phẩm bestseller của Yuval Noah Harari.', '9786041234632', 195000.00, 150, '2019-09-01', '2024-11-01', 8, 12),
('Homo Deus: Lược sử tương lai', 'Phần tiếp theo của Sapiens về tương lai nhân loại.', '9786041234633', 195000.00, 120, '2020-03-15', '2024-11-01', 8, 12),
('21 bài học cho thế kỷ 21', 'Sách về các vấn đề của thế giới hiện đại.', '9786041234634', 175000.00, 100, '2020-08-21', '2024-11-01', 8, 12),
('Lịch sử vắn tắt về thời gian', 'Cuốn sách khoa học phổ thông của Stephen Hawking.', '9786041234635', 125000.00, 180, '2018-03-14', '2024-11-01', 8, 12),
('Vũ trụ trong vỏ hạt dẻ', 'Sách về vật lý lượng tử của Stephen Hawking.', '9786041234636', 145000.00, 100, '2019-01-08', '2024-11-01', 8, 12),
('Gen ích kỷ', 'Cuốn sách nổi tiếng của Richard Dawkins về tiến hóa.', '9786041234637', 155000.00, 90, '2018-06-01', '2024-11-01', 8, 9),
('Nguồn gốc các loài', 'Tác phẩm kinh điển của Charles Darwin.', '9786041234638', 135000.00, 120, '2017-11-24', '2024-11-01', 8, 9),
('Thế giới phẳng', 'Sách về toàn cầu hóa của Thomas Friedman.', '9786041234639', 165000.00, 80, '2019-04-22', '2024-11-01', 8, 11),
('Outliers - Những kẻ xuất chúng', 'Nghiên cứu về thành công của Malcolm Gladwell.', '9786041234640', 115000.00, 150, '2018-11-18', '2024-11-01', 8, 12),
('Clean Code', 'Sách về lập trình sạch của Robert C. Martin.', '9786041234641', 285000.00, 80, '2020-08-01', '2024-11-01', 8, 10),

-- Lịch sử (category_id = 9)
('Việt Nam sử lược', 'Lịch sử Việt Nam của Trần Trọng Kim.', '9786041234642', 145000.00, 120, '2017-09-02', '2024-11-01', 9, 23),
('Đại Việt sử ký toàn thư', 'Bộ quốc sử đồ sộ của Việt Nam.', '9786041234643', 350000.00, 50, '2018-01-01', '2024-11-01', 9, 23),
('Lịch sử thế giới cận đại', 'Tổng quan lịch sử thế giới từ 1500 đến nay.', '9786041234644', 185000.00, 90, '2019-05-09', '2024-11-01', 9, 10),
('Guns, Germs, and Steel', 'Sách về lịch sử văn minh nhân loại của Jared Diamond.', '9786041234645', 195000.00, 70, '2018-07-04', '2024-11-01', 9, 12),
('Nghìn năm văn hiến', 'Sách về văn hóa và lịch sử Việt Nam.', '9786041234646', 125000.00, 100, '2019-02-03', '2024-11-01', 9, 3),
('Sử ký Tư Mã Thiên', 'Bộ sử nổi tiếng của Trung Quốc.', '9786041234647', 225000.00, 60, '2017-06-06', '2024-11-01', 9, 9),
('Chiến tranh Việt Nam', 'Góc nhìn toàn diện về cuộc chiến.', '9786041234648', 165000.00, 80, '2020-04-30', '2024-11-01', 9, 23),
('Hồ Chí Minh - Tiểu sử', 'Tiểu sử chính thức của Chủ tịch Hồ Chí Minh.', '9786041234649', 145000.00, 150, '2019-05-19', '2024-11-01', 9, 23),
('Nhật ký trong tù', 'Tập thơ nổi tiếng của Chủ tịch Hồ Chí Minh.', '9786041234650', 55000.00, 300, '2018-09-02', '2024-11-01', 9, 23),
('Lịch sử Đảng Cộng sản Việt Nam', 'Giáo trình lịch sử Đảng.', '9786041234651', 95000.00, 200, '2020-02-03', '2024-11-01', 9, 23),

-- Thiếu nhi (category_id = 10)
('Doraemon tập 1', 'Manga nổi tiếng về chú mèo máy đến từ tương lai.', '9786041234652', 25000.00, 500, '2019-01-01', '2024-11-01', 10, 1),
('Doraemon tập 2', 'Tiếp tục cuộc phiêu lưu của Nobita và Doraemon.', '9786041234653', 25000.00, 480, '2019-01-15', '2024-11-01', 10, 1),
('Doraemon tập 3', 'Những bảo bối kỳ diệu của Doraemon.', '9786041234654', 25000.00, 460, '2019-02-01', '2024-11-01', 10, 1),
('Thần đồng đất Việt tập 1', 'Truyện tranh lịch sử Việt Nam cho thiếu nhi.', '9786041234655', 30000.00, 350, '2018-06-01', '2024-11-01', 10, 1),
('Cổ tích Việt Nam', 'Tuyển tập truyện cổ tích Việt Nam.', '9786041234656', 55000.00, 300, '2017-09-01', '2024-11-01', 10, 1),
('Andersen truyện cổ tích', 'Tuyển tập truyện cổ tích của Andersen.', '9786041234657', 65000.00, 250, '2018-04-02', '2024-11-01', 10, 1),
('Truyện cổ Grimm', 'Tuyển tập truyện cổ tích của anh em nhà Grimm.', '9786041234658', 65000.00, 230, '2018-05-15', '2024-11-01', 10, 1),
('Totto-chan bên cửa sổ', 'Cuốn sách về giáo dục trẻ em của Nhật Bản.', '9786041234659', 75000.00, 200, '2019-06-01', '2024-11-01', 10, 1),
('Những cuộc phiêu lưu của Tom Sawyer', 'Tác phẩm kinh điển của Mark Twain.', '9786041234660', 65000.00, 180, '2018-07-04', '2024-11-01', 10, 1),
('Robinson Crusoe', 'Tiểu thuyết phiêu lưu kinh điển.', '9786041234661', 75000.00, 150, '2019-04-25', '2024-11-01', 10, 1),

-- Manga - Comic (category_id = 11)
('One Piece tập 1', 'Manga bán chạy nhất lịch sử của Eiichiro Oda.', '9786041234662', 25000.00, 400, '2020-07-22', '2024-11-01', 11, 1),
('One Piece tập 2', 'Luffy tiếp tục hành trình trở thành Vua hải tặc.', '9786041234663', 25000.00, 380, '2020-08-05', '2024-11-01', 11, 1),
('One Piece tập 3', 'Băng hải tặc Mũ Rơm mở rộng.', '9786041234664', 25000.00, 360, '2020-08-19', '2024-11-01', 11, 1),
('Naruto tập 1', 'Câu chuyện về cậu bé ninja Naruto Uzumaki.', '9786041234665', 25000.00, 350, '2019-10-10', '2024-11-01', 11, 1),
('Naruto tập 2', 'Naruto tiếp tục con đường trở thành Hokage.', '9786041234666', 25000.00, 340, '2019-10-24', '2024-11-01', 11, 1),
('Dragon Ball tập 1', 'Manga kinh điển của Akira Toriyama.', '9786041234667', 25000.00, 300, '2018-11-20', '2024-11-01', 11, 1),
('Dragon Ball tập 2', 'Goku tiếp tục tìm kiếm ngọc rồng.', '9786041234668', 25000.00, 290, '2018-12-04', '2024-11-01', 11, 1),
('Thám tử lừng danh Conan tập 1', 'Manga trinh thám nổi tiếng của Gosho Aoyama.', '9786041234669', 25000.00, 400, '2019-05-04', '2024-11-01', 11, 1),
('Thám tử lừng danh Conan tập 2', 'Conan tiếp tục phá các vụ án.', '9786041234670', 25000.00, 380, '2019-05-18', '2024-11-01', 11, 1),
('Attack on Titan tập 1', 'Manga hành động kinh dị nổi tiếng.', '9786041234671', 30000.00, 250, '2020-04-07', '2024-11-01', 11, 1),

-- Sách giáo khoa (category_id = 12)
('Toán 12', 'Sách giáo khoa Toán lớp 12.', '9786041234672', 35000.00, 1000, '2024-06-01', '2024-11-01', 12, 24),
('Ngữ văn 12', 'Sách giáo khoa Ngữ văn lớp 12.', '9786041234673', 32000.00, 1000, '2024-06-01', '2024-11-01', 12, 24),
('Tiếng Anh 12', 'Sách giáo khoa Tiếng Anh lớp 12.', '9786041234674', 38000.00, 1000, '2024-06-01', '2024-11-01', 12, 24),
('Vật lý 12', 'Sách giáo khoa Vật lý lớp 12.', '9786041234675', 28000.00, 800, '2024-06-01', '2024-11-01', 12, 24),
('Hóa học 12', 'Sách giáo khoa Hóa học lớp 12.', '9786041234676', 28000.00, 800, '2024-06-01', '2024-11-01', 12, 24),
('Sinh học 12', 'Sách giáo khoa Sinh học lớp 12.', '9786041234677', 26000.00, 700, '2024-06-01', '2024-11-01', 12, 24),
('Lịch sử 12', 'Sách giáo khoa Lịch sử lớp 12.', '9786041234678', 24000.00, 600, '2024-06-01', '2024-11-01', 12, 24),
('Địa lý 12', 'Sách giáo khoa Địa lý lớp 12.', '9786041234679', 25000.00, 600, '2024-06-01', '2024-11-01', 12, 24),
('Giáo dục công dân 12', 'Sách giáo khoa GDCD lớp 12.', '9786041234680', 22000.00, 500, '2024-06-01', '2024-11-01', 12, 24),
('Tin học 12', 'Sách giáo khoa Tin học lớp 12.', '9786041234681', 30000.00, 500, '2024-06-01', '2024-11-01', 12, 24),

-- Ngoại ngữ (category_id = 13)
('English Grammar in Use', 'Sách ngữ pháp tiếng Anh kinh điển của Raymond Murphy.', '9786041234682', 195000.00, 200, '2019-09-01', '2024-11-01', 13, 20),
('Essential Grammar in Use', 'Sách ngữ pháp tiếng Anh cơ bản.', '9786041234683', 175000.00, 180, '2019-10-15', '2024-11-01', 13, 20),
('IELTS 15 Academic', 'Đề thi IELTS chính thức từ Cambridge.', '9786041234684', 285000.00, 150, '2020-07-02', '2024-11-01', 13, 20),
('TOEIC Test Preparation', 'Giáo trình luyện thi TOEIC.', '9786041234685', 165000.00, 200, '2020-01-15', '2024-11-01', 13, 20),
('Minna no Nihongo Sơ cấp 1', 'Giáo trình tiếng Nhật phổ biến nhất.', '9786041234686', 185000.00, 150, '2019-04-01', '2024-11-01', 13, 9),
('Minna no Nihongo Sơ cấp 2', 'Phần tiếp theo của giáo trình Minna.', '9786041234687', 185000.00, 140, '2019-07-01', '2024-11-01', 13, 9),
('Giáo trình Hán ngữ tập 1', 'Giáo trình tiếng Trung phổ biến.', '9786041234688', 125000.00, 180, '2018-09-01', '2024-11-01', 13, 9),
('Giáo trình Hán ngữ tập 2', 'Phần tiếp theo của giáo trình Hán ngữ.', '9786041234689', 135000.00, 160, '2019-01-01', '2024-11-01', 13, 9),
('Tiếng Hàn tổng hợp Sơ cấp 1', 'Giáo trình tiếng Hàn cơ bản.', '9786041234690', 155000.00, 200, '2019-06-01', '2024-11-01', 13, 9),
('Le nouveau Taxi 1', 'Giáo trình tiếng Pháp phổ biến.', '9786041234691', 195000.00, 80, '2018-10-01', '2024-11-01', 13, 21),

-- Y học - Sức khỏe (category_id = 14)
('Cẩm nang sức khỏe gia đình', 'Sách y học phổ thông cho mọi nhà.', '9786041234692', 195000.00, 150, '2019-12-01', '2024-11-01', 14, 25),
('Ăn để khỏe', 'Sách về dinh dưỡng và sức khỏe.', '9786041234693', 125000.00, 200, '2020-01-15', '2024-11-01', 14, 25),
('Yoga cho người mới bắt đầu', 'Hướng dẫn yoga cơ bản.', '9786041234694', 95000.00, 180, '2019-08-15', '2024-11-01', 14, 15),
('Thiền và sức khỏe', 'Lợi ích của thiền định cho sức khỏe.', '9786041234695', 85000.00, 150, '2019-05-10', '2024-11-01', 14, 15),
('Đông y cơ bản', 'Giới thiệu về y học cổ truyền.', '9786041234696', 145000.00, 100, '2018-11-11', '2024-11-01', 14, 25),
('Bài thuốc dân gian', 'Tuyển tập bài thuốc dân gian Việt Nam.', '9786041234697', 75000.00, 200, '2019-03-15', '2024-11-01', 14, 25),
('Chăm sóc trẻ em', 'Hướng dẫn chăm sóc sức khỏe trẻ em.', '9786041234698', 135000.00, 180, '2020-06-01', '2024-11-01', 14, 6),
('Phụ nữ khỏe đẹp', 'Sách sức khỏe dành cho phụ nữ.', '9786041234699', 115000.00, 160, '2019-10-20', '2024-11-01', 14, 6),
('Sống khỏe mỗi ngày', 'Lối sống lành mạnh cho người hiện đại.', '9786041234700', 98000.00, 200, '2020-02-15', '2024-11-01', 14, 15),
('Chống lão hóa', 'Bí quyết trẻ khỏe lâu dài.', '9786041234701', 125000.00, 120, '2019-07-07', '2024-11-01', 14, 15),

-- Nấu ăn - Ẩm thực (category_id = 15)
('555 món ăn Việt Nam', 'Tuyển tập công thức nấu ăn Việt Nam.', '9786041234702', 185000.00, 150, '2019-01-20', '2024-11-01', 15, 6),
('Bánh Việt Nam', 'Sách về các loại bánh truyền thống Việt Nam.', '9786041234703', 145000.00, 120, '2019-09-14', '2024-11-01', 15, 6),
('Ẩm thực Huế', 'Tinh hoa ẩm thực cố đô.', '9786041234704', 165000.00, 100, '2020-01-01', '2024-11-01', 15, 4),
('Món ngon từ rau củ', 'Công thức chay thanh đạm.', '9786041234705', 125000.00, 180, '2019-06-15', '2024-11-01', 15, 6),
('Món ăn cho bé', 'Thực đơn dinh dưỡng cho trẻ em.', '9786041234706', 135000.00, 200, '2019-11-20', '2024-11-01', 15, 6),
('Ẩm thực Nhật Bản', 'Khám phá ẩm thực xứ sở hoa anh đào.', '9786041234707', 175000.00, 80, '2020-03-21', '2024-11-01', 15, 13),
('Ẩm thực Hàn Quốc', 'Các món ăn Hàn Quốc phổ biến.', '9786041234708', 165000.00, 90, '2019-08-15', '2024-11-01', 15, 13),
('Ẩm thực Ý', 'Pizza, pasta và hơn thế nữa.', '9786041234709', 155000.00, 100, '2020-05-05', '2024-11-01', 15, 13),
('Bánh ngọt Pháp', 'Nghệ thuật làm bánh ngọt kiểu Pháp.', '9786041234710', 195000.00, 70, '2019-07-14', '2024-11-01', 15, 13),
('Cà phê và trà', 'Văn hóa cà phê và trà thế giới.', '9786041234711', 145000.00, 150, '2020-10-01', '2024-11-01', 15, 14);

-- =====================================================
-- BOOK_AUTHORS (Many-to-Many relationship)
-- =====================================================
INSERT INTO book_authors (book_id, author_id) VALUES
-- Nguyễn Nhật Ánh books (author_id = 1)
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1),
-- Nam Cao (author_id = 2)
(7, 2), (8, 2), (35, 2),
-- Ngô Tất Tố (author_id = 3)
(9, 3),
-- Vũ Trọng Phụng (author_id = 5)
(10, 5),
-- Nguyễn Du (author_id = 4)
(31, 4),
-- Nguyễn Tuân (author_id = 7)
(32, 7),
-- Tô Hoài (author_id = 6)
(33, 6),
-- Nguyễn Huy Thiệp (author_id = 28)
(34, 28),
-- Garcia Marquez (author_id = 12)
(11, 12),
-- Haruki Murakami (author_id = 11)
(12, 11), (13, 11), (14, 11),
-- Dostoevsky (author_id = 31)
(15, 31), (16, 31),
-- Tolstoy (author_id = 32)
(17, 32), (18, 32),
-- Hemingway (author_id = 33)
(19, 33),
-- Victor Hugo (author_id = 41)
(20, 41),
-- Paulo Coelho (author_id = 13)
(21, 13),
-- Antoine de Saint-Exupéry (author_id = 36)
(22, 36),
-- Viktor Frankl
(23, 16),
-- Bảo Ninh (author_id = 29)
(24, 29),
-- J.K. Rowling (author_id = 14)
(25, 14), (26, 14), (27, 14), (28, 14),
-- Dan Brown (author_id = 48)
(29, 48), (30, 48),
-- Dale Carnegie (author_id = 16)
(36, 16),
-- Napoleon Hill (author_id = 17)
(37, 17),
-- Robert Kiyosaki (author_id = 18)
(38, 18),
-- Sun Tzu (author_id = 19)
(39, 19),
-- Thích Nhất Hạnh (author_id = 21)
(50, 21), (61, 21), (62, 21),
-- Osho (author_id = 22)
(69, 22), (70, 22),
-- Yuval Noah Harari (author_id = 20)
(71, 20), (72, 20), (73, 20),
-- Hermann Hesse (author_id = 46)
(67, 46),
-- Khalil Gibran (author_id = 47)
(68, 47),
-- Eiichiro Oda (author_id = 23)
(101, 23), (102, 23), (103, 23),
-- Masashi Kishimoto (author_id = 24)
(104, 24), (105, 24),
-- Akira Toriyama (author_id = 25)
(106, 25), (107, 25),
-- Gosho Aoyama (author_id = 26)
(108, 26), (109, 26),
-- Mark Twain (author_id = 39)
(99, 39),
-- Agatha Christie (author_id = 37)
-- George Orwell (author_id = 38)
-- Jane Austen (author_id = 40)
-- Charles Dickens (author_id = 42)
-- Shakespeare (author_id = 43)
-- Oscar Wilde (author_id = 44)
-- Edgar Allan Poe (author_id = 45)
-- Nguyên Phong (author_id = 50)
(49, 50);


