package com.bookstore.controller.admin;

import com.bookstore.model.Publisher;
import com.bookstore.service.PublisherService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/publishers")
public class PublisherServlet extends HttpServlet {

    // Khởi tạo Service (Service sẽ gọi DAO)
    private final PublisherService publisherService = new PublisherService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thiết lập encoding tiếng Việt
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        // Đường dẫn mặc định
        String url = "/admin/publisher/show.jsp";

        try {
            switch (action) {
                case "list":
                    listPublishers(request, response);
                    break;

                case "showCreate":
                    // Chuyển sang trang form tạo mới
                    url = "/admin/publisher/create.jsp";
                    break;

                case "create":
                    createPublisher(request, response);
                    // Sau khi tạo xong, load lại danh sách và ở lại trang show
                    url = "/admin/publisher/show.jsp";
                    break;

                case "showUpdate":
                    showUpdateForm(request, response);
                    url = "/admin/publisher/update.jsp";
                    break;

                case "update":
                    updatePublisher(request, response);
                    url = "/admin/publisher/show.jsp";
                    break;

                case "delete":
                    deletePublisher(request, response);
                    url = "/admin/publisher/show.jsp";
                    break;

                default:
                    listPublishers(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        // Chuyển hướng đến file JSP tương ứng
        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    // --- CÁC HÀM XỬ LÝ LOGIC ---

    private void listPublishers(HttpServletRequest request, HttpServletResponse response) {
        // Gọi Service lấy danh sách
        List<Publisher> listPublishers = publisherService.getAllPublishers(); // Hoặc findAll() tùy tên hàm trong
                                                                              // Service
        request.setAttribute("listPublishers", listPublishers);
    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Publisher publisher = publisherService.findById(id);
            session.setAttribute("publisher", publisher);
        }
    }

    private void createPublisher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Publisher newPublisher = new Publisher();

        // Gọi hàm chung để đọc dữ liệu từ form
        readPublisherFields(newPublisher, request);

        // Validate dữ liệu
        String validationError = validatePublisher(newPublisher);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            getServletContext().getRequestDispatcher("/admin/publisher/create.jsp").forward(request, response);
            return;
        }

        // Lưu vào DB
        publisherService.createPublisher(newPublisher); // Hoặc createPublisher(newPublisher)

        // Load lại danh sách để hiển thị cập nhật mới
        request.setAttribute("message", "Created successfully!");
        listPublishers(request, response);
    }

    private void updatePublisher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id"); // Lấy ID từ hidden field hoặc URL

        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);

            // 1. Tìm đối tượng cũ
            Publisher publisher = publisherService.findById(id);

            if (publisher != null) {
                // 2. Cập nhật thông tin mới vào đối tượng cũ
                readPublisherFields(publisher, request);

                // 3. Validate dữ liệu
                String validationError = validatePublisher(publisher);
                if (validationError != null) {
                    request.setAttribute("errorMessage", validationError);
                    request.setAttribute("publisher", publisher);
                    getServletContext().getRequestDispatcher("/admin/publisher/update.jsp").forward(request, response);
                    return;
                }

                // 4. Lưu xuống DB
                publisherService.updatePublisher(publisher); // Hoặc updatePublisher
                request.setAttribute("message", "Updated successfully!");
            }
        }

        // Load lại danh sách
        listPublishers(request, response);
    }

    private void deletePublisher(HttpServletRequest request, HttpServletResponse response) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            
            // Kiểm tra xem NXB có sách liên kết không
            long bookCount = com.bookstore.dao.PublisherDAO.countBooksByPublisher(id);
            if (bookCount > 0) {
                request.setAttribute("errorMessage", 
                    "Không thể xoá nhà xuất bản này vì có " + bookCount + " sách liên kết. Vui lòng chuyển hoặc xoá các sách trước.");
            } else {
                publisherService.deletePublisher(id);
                request.setAttribute("message", "Xoá nhà xuất bản thành công!");
            }
        }
        listPublishers(request, response);
    }

    // --- HÀM HỖ TRỢ (HELPER) ---
    // Hàm này giúp code gọn hơn, dùng chung cho cả Create và Update
    private void readPublisherFields(Publisher publisher, HttpServletRequest request) {
        String name = request.getParameter("name");
        String email = request.getParameter("contactEmail"); // Khớp với name="" bên JSP
        String address = request.getParameter("address");
        String website = request.getParameter("website");

        publisher.setName(name);
        publisher.setContactEmail(email);
        publisher.setAddress(address);
        publisher.setWebsite(website);
    }

    // --- VALIDATION METHOD ---
    private String validatePublisher(Publisher publisher) {
        // 1. Validate Name
        if (publisher.getName() == null || publisher.getName().trim().isEmpty()) {
            return "Tên nhà xuất bản không được để trống.";
        }
        if (publisher.getName().length() > 255) {
            return "Tên nhà xuất bản không được vượt quá 255 ký tự.";
        }

        // 2. Validate Contact Email (required and valid format)
        if (publisher.getContactEmail() == null || publisher.getContactEmail().trim().isEmpty()) {
            return "Email liên hệ không được để trống.";
        }
        // Simple email regex validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!publisher.getContactEmail().matches(emailRegex)) {
            return "Email liên hệ không hợp lệ.";
        }
        if (publisher.getContactEmail().length() > 100) {
            return "Email không được vượt quá 100 ký tự.";
        }

        // 3. Validate Address (optional but if provided, check length)
        if (publisher.getAddress() != null && !publisher.getAddress().trim().isEmpty()) {
            if (publisher.getAddress().length() > 500) {
                return "Địa chỉ không được vượt quá 500 ký tự.";
            }
        }

        // 4. Validate Website (optional but if provided, check format)
        if (publisher.getWebsite() != null && !publisher.getWebsite().trim().isEmpty()) {
            if (publisher.getWebsite().length() > 255) {
                return "Website không được vượt quá 255 ký tự.";
            }
            // Simple URL validation
            String urlRegex = "^(https?://)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*$";
            if (!publisher.getWebsite().matches(urlRegex)) {
                return "Website không hợp lệ. Ví dụ: https://example.com";
            }
        }

        return null; // No validation errors
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}