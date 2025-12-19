package com.bookstore.controller.admin;

import com.bookstore.model.*;
import com.bookstore.service.BookServices;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // QUAN TRỌNG
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        HttpSession session = request.getSession();
        String bookIdStr = request.getParameter("bookId");
        if (bookIdStr != null && !bookIdStr.isEmpty()) {
            int bookId = Integer.parseInt(bookIdStr);
            Book book = bookService.getBookById(bookId);
            request.setAttribute("book", book);
            request.setAttribute("listCategory", bookService.getAllCategories());
            request.setAttribute("listAuthors", bookService.getAllAuthors());
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
            bookService.deleteBook(Integer.parseInt(bookIdStr));
            listBooks(request, response);
        }
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

            // Validate file type
            String contentType = imagePart.getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") &&
                !contentType.equals("image/jpg") && !contentType.equals("image/webp")) {
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

        // 5. Xử lý ẢNH (File Upload)
        Part part = request.getPart("bookImage");
        if (part != null && part.getSize() > 0) {
            String fileName = part.getSubmittedFileName();

            // Tạo thư mục lưu ảnh nếu chưa có
            String path = request.getServletContext().getRealPath("/") + "images";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();

            // Ghi file
            part.write(path + File.separator + fileName);

            // TODO: Lưu tên ảnh vào Book
            // Nếu Book.java của bạn có trường `imagePath` hoặc list `images`
            // Ví dụ: book.setImage(fileName);
            // Hoặc tạo BookImage object và add vào list như code mẫu trước đó
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}