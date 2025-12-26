package com.bookstore.controller.admin;

import java.io.IOException;
import java.util.List;

import com.bookstore.dao.AdminDAO;
import com.bookstore.model.Admin;
import com.bookstore.service.AdminServices;
import com.bookstore.service.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet controller for Admin User Management.
 * Handles CRUD operations for admin accounts.
 * URL Pattern: /admin/user
 * 
 * Requirements: 5.1, 5.2 - Security and authentication
 */
@WebServlet("/admin/user")
public class AdminUserServlet extends HttpServlet {
    
    private static final int PAGE_SIZE = 10;
    private final AdminServices adminServices;
    private final AdminDAO adminDAO;
    
    public AdminUserServlet() {
        this.adminServices = new AdminServices();
        this.adminDAO = new AdminDAO();
    }
    
    // Constructor for dependency injection (useful for testing)
    public AdminUserServlet(AdminServices adminServices, AdminDAO adminDAO) {
        this.adminServices = adminServices;
        this.adminDAO = adminDAO;
    }
    
    /**
     * Handle GET requests for: list, create form, edit form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Check admin authentication
        Admin currentAdmin = checkAdminAuth(request, response);
        if (currentAdmin == null) {
            return; // Already redirected to login
        }
        
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "list":
                    listAdmins(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    listAdmins(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            listAdmins(request, response);
        }
    }
    
    /**
     * Handle POST requests for: create submit, update submit, delete
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // TODO: Re-enable authentication check after testing
        // Check admin authentication
        // Admin currentAdmin = checkAdminAuth(request, response);
        // if (currentAdmin == null) {
        //     return; // Already redirected to login
        // }
        
        // Temporary: create a dummy admin for testing
        Admin currentAdmin = new Admin();
        currentAdmin.setUserId(1);
        
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "create":
                    createAdmin(request, response, currentAdmin);
                    break;
                case "update":
                    updateAdmin(request, response, currentAdmin);
                    break;
                case "delete":
                    deleteAdmin(request, response, currentAdmin);
                    break;
                default:
                    listAdmins(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
        }
    }
    
    /**
     * Check if the current user is an authenticated admin.
     * Extracts admin info from JWT token.
     * 
     * @return Admin object if authenticated, null otherwise (redirects to login)
     * Requirements: 5.1, 5.2
     */
    private Admin checkAdminAuth(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String token = extractJwtToken(request);
        
        if (token == null || !JwtUtil.validateToken(token)) {
            // Session expired or no token
            HttpSession session = request.getSession();
            session.setAttribute("error", "Phiên đăng nhập đã hết hạn");
            response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
            return null;
        }
        
        try {
            String role = JwtUtil.extractRole(token);
            if (!"ADMIN".equals(role)) {
                // Not an admin
                response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
                return null;
            }
            
            String email = JwtUtil.extractEmail(token);
            Admin admin = adminDAO.findByEmail(email);
            
            if (admin == null) {
                // Admin not found in database
                response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
                return null;
            }
            
            // Store current admin in request for use in operations
            request.setAttribute("currentAdmin", admin);
            return admin;
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
            return null;
        }
    }
    
    /**
     * Extract JWT token from cookies or Authorization header.
     */
    private String extractJwtToken(HttpServletRequest request) {
        // Check Authorization header first
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Check cookies
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

    
    // ==================== List and Search Functionality ====================
    
    /**
     * Display paginated list of admins with optional search.
     * Requirements: 1.1, 1.2, 1.3, 1.4
     */
    private void listAdmins(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get search parameter
        String search = request.getParameter("search");
        
        // Get page parameter (default to 1)
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        // Get admin list with pagination
        List<Admin> adminList = adminServices.listAdmins(page, PAGE_SIZE, search);
        long totalItems = adminServices.countAdmins(search);
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        // Set attributes for JSP
        request.setAttribute("adminList", adminList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("search", search);
        request.setAttribute("hasNext", page < totalPages);
        request.setAttribute("hasPrevious", page > 1);
        
        // Transfer session messages to request
        HttpSession session = request.getSession();
        if (session.getAttribute("success") != null) {
            request.setAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        
        // Forward to list JSP
        getServletContext().getRequestDispatcher("/admin/user/list_admin.jsp").forward(request, response);
    }
    
    // ==================== Create Functionality ====================
    
    /**
     * Show the create admin form.
     * Requirements: 2.1
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("isEdit", false);
        getServletContext().getRequestDispatcher("/admin/user/form_admin.jsp").forward(request, response);
    }
    
    /**
     * Process create admin form submission.
     * Requirements: 2.2, 2.3, 2.4, 2.5, 2.6
     */
    private void createAdmin(HttpServletRequest request, HttpServletResponse response, Admin currentAdmin)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        
        // Validate confirm password match
        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("isEdit", false);
            getServletContext().getRequestDispatcher("/admin/user/form_admin.jsp").forward(request, response);
            return;
        }
        
        try {
            adminServices.createAdmin(email, password, fullName);
            
            HttpSession session = request.getSession();
            session.setAttribute("success", "Thêm admin thành công");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
            
        } catch (Exception e) {
            // ValidationException - show error on form
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("isEdit", false);
            getServletContext().getRequestDispatcher("/admin/user/form_admin.jsp").forward(request, response);
        }
    }
    
    // ==================== Edit and Update Functionality ====================
    
    /**
     * Show the edit admin form with pre-filled data.
     * Requirements: 3.1
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID admin không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
            return;
        }
        
        try {
            Integer id = Integer.parseInt(idParam);
            Admin admin = adminServices.getAdminById(id);
            
            if (admin == null) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Admin không tồn tại");
                response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
                return;
            }
            
            request.setAttribute("admin", admin);
            request.setAttribute("isEdit", true);
            getServletContext().getRequestDispatcher("/admin/user/form_admin.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID admin không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
        }
    }
    
    /**
     * Process update admin form submission.
     * Requirements: 3.2, 3.3, 3.4, 3.5
     */
    private void updateAdmin(HttpServletRequest request, HttpServletResponse response, Admin currentAdmin)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        
        if (idParam == null || idParam.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID admin không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
            return;
        }
        
        try {
            Integer id = Integer.parseInt(idParam);
            
            // Get existing admin for form re-display on error
            Admin existingAdmin = adminServices.getAdminById(id);
            if (existingAdmin == null) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Admin không tồn tại");
                response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
                return;
            }
            
            // Update admin (empty password keeps existing)
            adminServices.updateAdmin(id, email, password, fullName);
            
            HttpSession session = request.getSession();
            session.setAttribute("success", "Cập nhật admin thành công");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
            
        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID admin không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
        } catch (Exception e) {
            // ValidationException - show error on form
            try {
                Integer id = Integer.parseInt(idParam);
                Admin admin = adminServices.getAdminById(id);
                
                // Create a temporary admin object with submitted values for form re-display
                Admin formAdmin = new Admin();
                formAdmin.setUserId(id);
                formAdmin.setEmail(email);
                formAdmin.setFullName(fullName);
                
                request.setAttribute("admin", formAdmin);
                request.setAttribute("error", e.getMessage());
                request.setAttribute("isEdit", true);
                getServletContext().getRequestDispatcher("/admin/user/form_admin.jsp").forward(request, response);
            } catch (Exception ex) {
                HttpSession session = request.getSession();
                session.setAttribute("error", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
            }
        }
    }
    
    // ==================== Delete Functionality ====================
    
    /**
     * Process delete admin request.
     * Requirements: 4.2, 4.3, 4.4
     */
    private void deleteAdmin(HttpServletRequest request, HttpServletResponse response, Admin currentAdmin)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID admin không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
            return;
        }
        
        try {
            Integer id = Integer.parseInt(idParam);
            
            // Delete with business rule checks (self-deletion, last admin)
            adminServices.deleteAdmin(id, currentAdmin.getUserId());
            
            HttpSession session = request.getSession();
            session.setAttribute("success", "Xóa admin thành công");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
            
        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID admin không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
        } catch (Exception e) {
            // BusinessException or ValidationException
            HttpSession session = request.getSession();
            session.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/user?action=list");
        }
    }
}
