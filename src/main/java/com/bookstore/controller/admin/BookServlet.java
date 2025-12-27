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
        // Gọi hàm đọc dữ liệu từ form (tránh viết lặp lại code)
        readBookFields(book, request);

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

            // 3. Lưu xuống DB
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