package com.bookstore.controller.admin;

import com.bookstore.dao.AdminDAO;
import com.bookstore.model.Admin;
import com.bookstore.model.Author;
import com.bookstore.service.AuthorServices;
import com.bookstore.service.JwtUtil;
import com.bookstore.util.CloudinaryUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/authors")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 5, // 5MB
        maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class AdminAuthorServlet extends HttpServlet {

    private final AuthorServices authorServices = new AuthorServices();
    private final AdminDAO adminDAO = new AdminDAO();
    private static final int PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Kiểm tra JWT authentication thay vì chỉ session
        Admin currentAdmin = checkAdminAuth(request, response);
        if (currentAdmin == null) {
            return; // Đã redirect đến login
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        String searchName = request.getParameter("searchName");
        String pageStr = request.getParameter("searchPage");

        try {
            switch (action) {
                case "list":
                    String listSearchName = request.getParameter("name");
                    String listPageStr = request.getParameter("page");
                    listAuthors(request, listSearchName, listPageStr);
                    request.getRequestDispatcher("/admin/author/list_author.jsp").forward(request, response);
                    break;

                case "create":
                case "edit":
                    showEditForm(request, action, searchName, pageStr);
                    request.getRequestDispatcher("/admin/author/edit_author.jsp").forward(request, response);
                    break;

                case "save":
                    saveAuthor(request, response, searchName, pageStr);
                    break;

                case "delete":
                    deleteAuthor(request, response);
                    redirectToList(request, response, searchName, pageStr);
                    break;

                default:
                    listAuthors(request, null, "1");
                    request.getRequestDispatcher("/admin/author/list_author.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            redirectToList(request, response, searchName, pageStr);
        }
    }

    private void listAuthors(HttpServletRequest request, String searchName, String pageStr) {
        int page = parsePage(pageStr);

        List<Author> authorList = authorServices.getAuthorsForAdmin(searchName, page - 1, PAGE_SIZE);
        long totalAuthors = authorServices.countAuthorsForAdmin(searchName);
        int totalPages = (int) Math.ceil((double) totalAuthors / PAGE_SIZE);

        request.setAttribute("authorList", authorList);
        request.setAttribute("totalAuthors", totalAuthors);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("name", searchName);
    }

    private void showEditForm(HttpServletRequest request, String action, String searchName, String pageStr) {
        request.setAttribute("searchName", searchName);
        request.setAttribute("searchPage", pageStr);

        if ("edit".equals(action)) {
            String authorIdStr = request.getParameter("authorId");
            if (authorIdStr != null && !authorIdStr.isEmpty()) {
                try {
                    int authorId = Integer.parseInt(authorIdStr);
                    Author author = authorServices.getAuthorById(authorId);
                    if (author != null) {
                        request.setAttribute("author", author);
                    } else {
                        request.getSession().setAttribute("errorMessage", "Không tìm thấy tác giả");
                    }
                } catch (NumberFormatException e) {
                    request.getSession().setAttribute("errorMessage", "ID tác giả không hợp lệ");
                }
            }
        }
    }

    private void saveAuthor(HttpServletRequest request, HttpServletResponse response,
            String searchName, String pageStr)
            throws IOException, ServletException {

        Author author = null;
        boolean isUpdate = false;

        String authorIdStr = request.getParameter("authorId");
        if (authorIdStr != null && !authorIdStr.isEmpty()) {
            try {
                int authorId = Integer.parseInt(authorIdStr);
                author = authorServices.getAuthorById(authorId);
                if (author != null) {
                    isUpdate = true;
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errorMessage", "ID tác giả không hợp lệ!");
                redirectToList(request, response, searchName, pageStr);
                return;
            }
        }

        if (author == null) {
            author = new Author();
        }

        // VALIDATE VÀ SET TÊN
        String name = request.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Tên tác giả không được để trống!");
            setFormAttributes(request, author, searchName, pageStr);
            request.getRequestDispatcher("/admin/author/edit_author.jsp").forward(request, response);
            return;
        }

        name = name.trim();
        if (name.length() > 100) {
            request.getSession().setAttribute("errorMessage", "Tên tác giả không được vượt quá 100 ký tự!");
            setFormAttributes(request, author, searchName, pageStr);
            request.getRequestDispatcher("/admin/author/edit_author.jsp").forward(request, response);
            return;
        }

        author.setName(name);

        // XỬ LÝ TIỂU SỬ
        String biography = request.getParameter("biography");
        if (biography != null && !biography.trim().isEmpty()) {
            biography = biography.trim();
            if (biography.length() > 10000) {
                request.getSession().setAttribute("errorMessage", "Tiểu sử không được vượt quá 10.000 ký tự!");
                setFormAttributes(request, author, searchName, pageStr);
                request.getRequestDispatcher("/admin/author/edit_author.jsp").forward(request, response);
                return;
            }
            author.setBiography(biography);
        } else {
            author.setBiography(null);
        }

        // XỬ LÝ ẢNH
        String oldPhotoUrl = author.getPhotoUrl();
        String uploadMethod = request.getParameter("uploadMethod");

        try {
            if ("file".equals(uploadMethod)) {
                Part filePart = request.getPart("photoFile");
                if (filePart != null && filePart.getSize() > 0) {
                    String uploadedUrl = uploadImageToCloudinary(filePart, author.getAuthorId());
                    author.setPhotoUrl(uploadedUrl);

                    if (isUpdate && oldPhotoUrl != null && !oldPhotoUrl.isEmpty()
                            && oldPhotoUrl.contains("cloudinary")) {
                        deleteImageFromCloudinary(oldPhotoUrl);
                    }
                }
            } else if ("url".equals(uploadMethod)) {
                String photoUrl = request.getParameter("photoUrl");
                if (photoUrl != null && !photoUrl.trim().isEmpty()) {
                    photoUrl = photoUrl.trim();

                    if (photoUrl.length() > 500) {
                        request.getSession().setAttribute("errorMessage", "URL ảnh không được vượt quá 500 ký tự!");
                        setFormAttributes(request, author, searchName, pageStr);
                        request.getRequestDispatcher("/admin/author/edit_author.jsp").forward(request, response);
                        return;
                    }

                    if (!photoUrl.matches("^(?i)https?://.+")) {
                        request.getSession().setAttribute("errorMessage",
                                "URL ảnh không hợp lệ (phải bắt đầu bằng http:// hoặc https://)");
                        setFormAttributes(request, author, searchName, pageStr);
                        request.getRequestDispatcher("/admin/author/edit_author.jsp").forward(request, response);
                        return;
                    }

                    author.setPhotoUrl(photoUrl);

                    if (isUpdate && oldPhotoUrl != null && !oldPhotoUrl.isEmpty()
                            && !oldPhotoUrl.equals(photoUrl) && oldPhotoUrl.contains("cloudinary")) {
                        deleteImageFromCloudinary(oldPhotoUrl);
                    }
                } else {
                    author.setPhotoUrl(null);
                }
            } else if ("keep".equals(uploadMethod)) {
                // Giữ nguyên
            } else if ("remove".equals(uploadMethod)) {
                if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()
                        && oldPhotoUrl.contains("cloudinary")) {
                    deleteImageFromCloudinary(oldPhotoUrl);
                }
                author.setPhotoUrl(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi xử lý ảnh: " + e.getMessage());
            setFormAttributes(request, author, searchName, pageStr);
            request.getRequestDispatcher("/admin/author/edit_author.jsp").forward(request, response);
            return;
        }

        // LƯU VÀO DATABASE
        try {
            if (isUpdate) {
                authorServices.updateAuthor(author);
                request.getSession().setAttribute("successMessage", "Cập nhật tác giả thành công!");
            } else {
                authorServices.createAuthor(author);
                request.getSession().setAttribute("successMessage", "Thêm tác giả mới thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Lưu tác giả thất bại: " + e.getMessage();
            request.getSession().setAttribute("errorMessage", errorMsg);
            setFormAttributes(request, author, searchName, pageStr);
            request.getRequestDispatcher("/admin/author/edit_author.jsp").forward(request, response);
            return;
        }

        redirectToList(request, response, searchName, pageStr);
    }

    private void setFormAttributes(HttpServletRequest request, Author author,
            String searchName, String pageStr) {
        request.setAttribute("author", author);
        request.setAttribute("searchName", searchName);
        request.setAttribute("searchPage", pageStr);
    }

    private String uploadImageToCloudinary(Part filePart, Integer authorId) throws Exception {
        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new Exception("Chỉ chấp nhận file ảnh (JPEG, PNG, WEBP)");
        }

        if (filePart.getSize() > 5 * 1024 * 1024) {
            throw new Exception("Kích thước file không được vượt quá 5MB");
        }

        Cloudinary cloudinary = CloudinaryUtil.cloudinary;
        byte[] imageBytes = filePart.getInputStream().readAllBytes();

        String uniqueId = "author_" + System.currentTimeMillis() +
                (authorId != null ? "_" + authorId : "");

        Map uploadResult = cloudinary.uploader().upload(
                imageBytes,
                ObjectUtils.asMap(
                        "folder", "bookstore/authors",
                        "public_id", uniqueId,
                        "overwrite", true,
                        "resource_type", "image"));

        return uploadResult.get("secure_url").toString();
    }

    private void deleteImageFromCloudinary(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                Cloudinary cloudinary = CloudinaryUtil.cloudinary;
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null)
            return null;

        String[] parts = imageUrl.split("/upload/");
        if (parts.length < 2)
            return null;

        String publicPath = parts[1];
        publicPath = publicPath.replaceFirst("^v\\d+/", "");

        int lastDot = publicPath.lastIndexOf('.');
        if (lastDot > 0) {
            return publicPath.substring(0, lastDot);
        }
        return publicPath;
    }

    private void deleteAuthor(HttpServletRequest request, HttpServletResponse response) {
        String authorIdStr = request.getParameter("authorId");
        if (authorIdStr != null && !authorIdStr.isEmpty()) {
            try {
                int authorId = Integer.parseInt(authorIdStr);
                Author author = authorServices.getAuthorById(authorId);

                if (author != null && author.getPhotoUrl() != null
                        && author.getPhotoUrl().contains("cloudinary")) {
                    deleteImageFromCloudinary(author.getPhotoUrl());
                }

                authorServices.deleteAuthor(authorId);
                request.getSession().setAttribute("successMessage", "Xóa tác giả thành công!");
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Không thể xóa tác giả: " + e.getMessage());
            }
        }
    }

    private int parsePage(String pageStr) {
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                int page = Integer.parseInt(pageStr);
                return Math.max(page, 1);
            } catch (NumberFormatException ignored) {
            }
        }
        return 1;
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response,
            String searchName, String currentPageStr) throws IOException {

        StringBuilder url = new StringBuilder(request.getContextPath() + "/admin/authors?action=list");

        if (searchName != null && !searchName.trim().isEmpty()) {
            url.append("&name=").append(URLEncoder.encode(searchName.trim(), StandardCharsets.UTF_8));
        }

        if (currentPageStr != null && !currentPageStr.isEmpty()) {
            try {
                int page = Integer.parseInt(currentPageStr);
                if (page > 1) {
                    url.append("&page=").append(page);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        response.sendRedirect(url.toString());
    }

    // ==================== JWT Authentication ====================

    /**
     * Kiểm tra xác thực admin từ JWT token.
     * 
     * @return Admin object nếu xác thực thành công, null nếu không (đã redirect đến
     *         login)
     */
    private Admin checkAdminAuth(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String token = extractJwtToken(request);

        if (token == null || !JwtUtil.validateToken(token)) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Phiên đăng nhập đã hết hạn");
            response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
            return null;
        }

        try {
            String role = JwtUtil.extractRole(token);
            if (!"ADMIN".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
                return null;
            }

            String email = JwtUtil.extractEmail(token);
            Admin admin = adminDAO.findByEmail(email);

            if (admin == null) {
                response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
                return null;
            }

            // Lưu admin vào request để sử dụng trong các thao tác
            request.setAttribute("currentAdmin", admin);
            return admin;

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
            return null;
        }
    }

    /**
     * Trích xuất JWT token từ cookies hoặc Authorization header.
     */
    private String extractJwtToken(HttpServletRequest request) {
        // Kiểm tra Authorization header trước
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Kiểm tra cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}