package com.bookstore.controller.admin;

import com.bookstore.model.*;
import com.bookstore.service.BookServices;

import com.bookstore.util.CloudinaryUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // QUAN TRỌNG
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/books")
@MultipartConfig( // BẮT BUỘC ĐỂ UPLOAD ẢNH
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class BookServlet extends HttpServlet {
    private final BookServices bookService = new BookServices();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }


        String url = "/admin/book/show.jsp";

        try {
            switch (action) {
                case "list":
                    listBooks(request, response);
                    break;
                case "showCreate":
                    showCreateForm(request, response);
                    url = "/admin/book/create.jsp";
                    break;
                case "showUpdate":
                    showUpdateForm(request, response);
                    url = "/admin/book/update.jsp";
                    break;
                case "create":
                    createBook(request, response);
                    return;
                case "update":
                    updateBook(request, response);
                    return;
                case "delete":
                    deleteBook(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response) {
        List<Book> books = bookService.getAllBooks();
        request.setAttribute("books", books);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("listCategory", bookService.getAllCategories());
        request.setAttribute("listAuthors", bookService.getAllAuthors());
    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response) {
        try {
            String bookIdStr = request.getParameter("bookId");
            if (bookIdStr != null && !bookIdStr.isEmpty()) {
                int bookId = Integer.parseInt(bookIdStr);
                Book book = bookService.getBookById(bookId);

                if (book == null) {
                    throw new ServletException("Book not found with ID: " + bookId);
                }

                request.setAttribute("book", book);
                request.setAttribute("listCategory", bookService.getAllCategories());
                request.setAttribute("listAuthors", bookService.getAllAuthors());
            } else {
                throw new ServletException("Book ID is required");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to load book: " + e.getMessage());
        }
    }

    private void createBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Book book = new Book();

        // Đọc dữ liệu từ form
        readBookFields(book, request);

        // Validate dữ liệu
        String validationError = validateBook(book, request, true);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            request.setAttribute("listCategory", bookService.getAllCategories());
            request.setAttribute("listAuthors", bookService.getAllAuthors());
            getServletContext().getRequestDispatcher("/admin/book/create.jsp").forward(request, response);
            return;
        }

        bookService.createBook(book);

        listBooks(request, response);
        getServletContext().getRequestDispatcher("/admin/book/show.jsp").forward(request, response);
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookIdStr = request.getParameter("bookId");
        if (bookIdStr != null) {
            int bookId = Integer.parseInt(bookIdStr);

            // 1. Lấy sách cũ từ DB lên
            Book book = bookService.getBookById(bookId);

            // 2. Cập nhật thông tin mới vào sách cũ
            readBookFields(book, request);

            // 3. Validate dữ liệu
            String validationError = validateBook(book, request, false);
            if (validationError != null) {
                request.setAttribute("errorMessage", validationError);
                request.setAttribute("book", book);
                request.setAttribute("listCategory", bookService.getAllCategories());
                request.setAttribute("listAuthors", bookService.getAllAuthors());
                getServletContext().getRequestDispatcher("/admin/book/update.jsp").forward(request, response);
                return;
            }

            // 4. Lưu xuống DB
            bookService.updateBook(book);
        }
        listBooks(request, response);
        getServletContext().getRequestDispatcher("/admin/book/show.jsp").forward(request, response);
    }


    private void deleteBook(HttpServletRequest request, HttpServletResponse response) {
        String bookIdStr = request.getParameter("bookId");

        if (bookIdStr != null) {
            int bookId = Integer.parseInt(bookIdStr);

            Book book = bookService.getBookById(bookId);
            Cloudinary cloudinary = CloudinaryUtil.cloudinary;

            for (BookImage img : book.getImages()) {
                try {
                    String publicId = extractPublicId(img.getUrl());
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (IOException e) {
                    // Log lại là đủ, KHÔNG nên stop delete book
                    e.printStackTrace();
                }
            }

            bookService.deleteBook(bookId);
            listBooks(request, response);
        }
    }


    private String extractPublicId(String imageUrl) {
        // Ví dụ URL:
        // https://res.cloudinary.com/demo/image/upload/v123456/bookstore/books/book_9781234567890.jpg

        String[] parts = imageUrl.split("/upload/");
        if (parts.length < 2) return null;

        String publicPath = parts[1];

        // bỏ version v123456/
        publicPath = publicPath.replaceFirst("^v\\d+/", "");

        // bỏ đuôi .jpg / .png / ...
        return publicPath.substring(0, publicPath.lastIndexOf('.'));
    }
    // --- VALIDATION METHOD ---
    private String validateBook(Book book, HttpServletRequest request, boolean isCreate) throws ServletException, IOException {
        // 1. Validate Title
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return "Title is required.";
        }
        if (book.getTitle().length() > 255) {
            return "Title must not exceed 255 characters.";
        }
        // 2. Validate ISBN
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            return "ISBN is required.";
        }
        if (book.getIsbn().length() < 10 || book.getIsbn().length() > 20) {
            return "ISBN must be between 10 and 20 characters.";
        }
        if (!book.getIsbn().matches("[0-9\\-]{10,20}")) {
            return "ISBN must contain only digits and hyphens.";
        }

        // 3. Validate Price
        if (book.getPrice() == null) {
            return "Price is required.";
        }
        if (book.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "Price must be greater than 0.";
        }
        if (book.getPrice().compareTo(new BigDecimal("99999999.99")) > 0) {
            return "Price must not exceed 99,999,999.99.";
        }

        // 4. Validate Publish Date
        if (book.getPublishDate() == null) {
            return "Publish date is required.";
        }
        if (book.getPublishDate().isAfter(LocalDate.now())) {
            return "Publish date cannot be in the future.";
        }

        // 5. Validate Category
        if (book.getCategory() == null) {
            return "Category is required.";
        }

        // 6. Validate Authors
        if (book.getAuthors() == null || book.getAuthors().isEmpty()) {
            return "At least one author is required.";
        }

        // 7. Validate Description (optional but if provided, check length)
        if (book.getDescription() != null && !book.getDescription().trim().isEmpty()) {
            if (book.getDescription().trim().length() < 10) {
                return "Description must be at least 10 characters if provided.";
            }
            if (book.getDescription().length() > 5000) {
                return "Description must not exceed 5000 characters.";
            }
        }

        // 8. Validate Book Image (required for create, optional for update)
        if (isCreate) {
            Part imagePart = request.getPart("bookImage");
            if (imagePart == null || imagePart.getSize() == 0) {
                return "Book image is required.";
            }

            // Validate file type - đồng bộ với client-side validation
            String contentType = imagePart.getContentType();
            List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp");
            if (!allowedTypes.contains(contentType.toLowerCase())) {
                return "Invalid image format. Only JPEG, PNG, JPG, WEBP are allowed.";
            }


            // Validate file size (max 5MB)
            long maxFileSize = 5 * 1024 * 1024; // 5MB
            if (imagePart.getSize() > maxFileSize) {
                return "Image file size must not exceed 5MB.";
            }
        }

        return null; // No validation errors
    }

    // --- HÀM HỖ TRỢ ĐỌC DỮ LIỆU TỪ FORM (Dùng chung cho Create và Update) ---
    private void readBookFields(Book book, HttpServletRequest request) throws IOException, ServletException {
        book.setTitle(request.getParameter("title"));
        book.setIsbn(request.getParameter("isbn"));
        book.setDescription(request.getParameter("description"));

        String priceStr = request.getParameter("price");
        if(priceStr != null) book.setPrice(new BigDecimal(priceStr));

        String qtyStr = request.getParameter("quantity");
        if(qtyStr != null) book.setQuantityInStock(Integer.parseInt(qtyStr)); // Cẩn thận null

        // 2. Xử lý NGÀY THÁNG (Publish Date)
        String dateStr = request.getParameter("publishDate");
        if (dateStr != null && !dateStr.isEmpty()) {
            book.setPublishDate(LocalDate.parse(dateStr));
        }
        book.setLastUpdated(LocalDate.now());

        // 3. Xử lý CATEGORY (Dropdown)
        String catIdStr = request.getParameter("categoryId");
        if (catIdStr != null) {
            // Bạn cần viết thêm hàm findById trong CategoryService
            Category cat = bookService.findCategoryById(Integer.parseInt(catIdStr));
            book.setCategory(cat);
        }

        // 4. Xử lý AUTHORS (Select2 - Mảng ID)
        String[] authorIds = request.getParameterValues("authorIds");
        List<Author> authors = new ArrayList<>();
        if (authorIds != null) {
            for (String authId : authorIds) {
                Author author = bookService.findAuthorById(Integer.parseInt(authId));
                if (author != null) authors.add(author);
            }
        }
        // Chỉ set authors nếu người dùng có chọn (để tránh xóa mất author cũ nếu form lỗi)
        if (authorIds != null) {
            book.setAuthors(authors);
        }
// 5. XỬ LÝ ẢNH (Cloudinary + BookImage)
        Part part = request.getPart("bookImage");
        if (part != null && part.getSize() > 0) {
            try {
                Cloudinary cloudinary = CloudinaryUtil.cloudinary;

                // Đọc InputStream thành byte[] array
                byte[] imageBytes = part.getInputStream().readAllBytes();

                // Upload lên Cloudinary
                Map uploadResult = cloudinary.uploader().upload(
                        imageBytes,
                        ObjectUtils.asMap(
                                "folder", "bookstore/books",
                                "public_id", "book_" + book.getIsbn(),
                                "overwrite", true,
                                "resource_type", "image"
                        )
                );

                String imageUrl = uploadResult.get("secure_url").toString();

                // TẠO BookImage
                BookImage image = new BookImage();
                image.setUrl(imageUrl);
                image.setIsPrimary(true);
                image.setSortOrder(0);
                image.setBook(book);

                // Đảm bảo images list được khởi tạo
                if (book.getImages() == null) {
                    book.setImages(new ArrayList<>());
                }

                // Nếu là UPDATE: xóa ảnh primary cũ
                book.getImages().removeIf(img -> Boolean.TRUE.equals(img.getIsPrimary()));

                // Add ảnh mới
                book.getImages().add(image);

            } catch (Exception e) {
                throw new ServletException("Failed to upload image: " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}