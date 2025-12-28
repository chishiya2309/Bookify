package com.bookstore.service;

import com.bookstore.dao.AdminDAO;
import com.bookstore.dao.AdminHomePageDAO;
import com.bookstore.dao.AdminOrderDAO;
import com.bookstore.model.Address;
import com.bookstore.model.Admin;
import com.bookstore.model.Order;
import com.bookstore.model.Review;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AdminServices {

    /**
     * Custom exception for validation errors.
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Custom exception for business logic errors.
     */
    public static class BusinessException extends Exception {
        public BusinessException(String message) {
            super(message);
        }
    }

    private final AdminDAO adminDAO;

    public AdminServices() {
        this.adminDAO = new AdminDAO();
    }

    // Constructor for dependency injection (useful for testing)
    public AdminServices(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    // ==================== Validation Methods ====================

    /**
     * Validates email format using the same pattern as User entity.
     * 
     * @param email the email to validate
     * @return null if valid, error message if invalid
     */
    public String validateEmail(String email) {
        if (email == null) {
            return "Email không được để trống";
        }
        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty()) {
            return "Email không được để trống";
        }
        if (!ValidationUtil.isValidEmail(trimmedEmail)) {
            return "Email không đúng định dạng";
        }
        if (ValidationUtil.containsSqlInjection(trimmedEmail)) {
            return "Dữ liệu không hợp lệ";
        }
        return null; // Valid
    }

    /**
     * Validates password meets minimum requirements (>= 6 characters).
     * 
     * @param password the password to validate
     * @return null if valid, error message if invalid
     */
    public String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Mật khẩu không được để trống";
        }
        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }
        if (ValidationUtil.containsSqlInjection(password)) {
            return "Dữ liệu không hợp lệ";
        }
        return null; // Valid
    }

    /**
     * Validates full name is not empty.
     * 
     * @param fullName the full name to validate
     * @return null if valid, error message if invalid
     */
    public String validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Họ tên không được để trống";
        }
        if (ValidationUtil.containsSqlInjection(fullName)) {
            return "Dữ liệu không hợp lệ";
        }
        return null; // Valid
    }

    // ==================== CRUD Service Methods ====================

    /**
     * List admins with pagination and optional search.
     * 
     * @param page     the page number (1-based)
     * @param pageSize the number of items per page
     * @param search   optional search keyword (can be null or empty)
     * @return list of admins for the requested page
     */
    public List<Admin> listAdmins(int page, int pageSize, String search) {
        int offset = (page - 1) * pageSize;

        if (search != null && !search.trim().isEmpty()) {
            return adminDAO.search(search.trim(), offset, pageSize);
        }
        return adminDAO.findAll(offset, pageSize);
    }

    /**
     * Count total admins for pagination, with optional search filter.
     * 
     * @param search optional search keyword (can be null or empty)
     * @return total count of admins matching the criteria
     */
    public long countAdmins(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return adminDAO.countBySearch(search.trim());
        }
        return adminDAO.countAll();
    }

    /**
     * Get a single admin by ID.
     * 
     * @param id the admin ID
     * @return the admin or null if not found
     */
    public Admin getAdminById(Integer id) {
        if (id == null) {
            return null;
        }
        return adminDAO.findById(id);
    }

    /**
     * Get a single admin by email.
     * 
     * @param email the admin email
     * @return the admin or null if not found
     */
    public Admin getAdminByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return adminDAO.findByEmail(email.trim());
    }

    /**
     * Create a new admin with BCrypt-hashed password.
     * 
     * @param email    the admin email
     * @param password the plain text password (will be hashed)
     * @param fullName the admin's full name
     * @throws ValidationException if validation fails
     */
    public void createAdmin(String email, String password, String fullName) throws ValidationException {
        // Validate email
        String emailError = validateEmail(email);
        if (emailError != null) {
            throw new ValidationException(emailError);
        }

        // Validate password
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            throw new ValidationException(passwordError);
        }

        // Validate full name
        String fullNameError = validateFullName(fullName);
        if (fullNameError != null) {
            throw new ValidationException(fullNameError);
        }

        // Check for duplicate email
        if (adminDAO.existsByEmail(email.trim())) {
            throw new ValidationException("Email đã được sử dụng");
        }

        // Hash password with BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Create and save admin
        Admin admin = new Admin(email.trim(), hashedPassword, fullName.trim());
        adminDAO.save(admin);
    }

    /**
     * Update an existing admin.
     * If password is empty/null, the existing password is preserved.
     * 
     * @param id       the admin ID to update
     * @param email    the new email
     * @param password the new password (empty/null to keep existing)
     * @param fullName the new full name
     * @throws ValidationException if validation fails or admin not found
     */
    public void updateAdmin(Integer id, String email, String password, String fullName) throws ValidationException {
        // Find existing admin
        Admin existingAdmin = adminDAO.findById(id);
        if (existingAdmin == null) {
            throw new ValidationException("Admin không tồn tại");
        }

        // Validate email
        String emailError = validateEmail(email);
        if (emailError != null) {
            throw new ValidationException(emailError);
        }

        // Validate full name
        String fullNameError = validateFullName(fullName);
        if (fullNameError != null) {
            throw new ValidationException(fullNameError);
        }

        // Check for duplicate email (excluding current admin)
        if (adminDAO.existsByEmailExcluding(email.trim(), id)) {
            throw new ValidationException("Email đã được sử dụng");
        }

        // Update fields
        existingAdmin.setEmail(email.trim());
        existingAdmin.setFullName(fullName.trim());

        // Update password only if provided
        if (password != null && !password.isEmpty()) {
            String passwordError = validatePassword(password);
            if (passwordError != null) {
                throw new ValidationException(passwordError);
            }
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            existingAdmin.setPassword(hashedPassword);
        }

        adminDAO.update(existingAdmin);
    }

    /**
     * Delete an admin with business rule checks.
     * Cannot delete self or the last admin in the system.
     * 
     * @param id             the admin ID to delete
     * @param currentAdminId the ID of the admin performing the deletion
     * @throws BusinessException   if business rules are violated
     * @throws ValidationException if admin not found
     */
    public void deleteAdmin(Integer id, Integer currentAdminId) throws BusinessException, ValidationException {
        // Check if admin exists
        Admin admin = adminDAO.findById(id);
        if (admin == null) {
            throw new ValidationException("Admin không tồn tại");
        }

        // Prevent self-deletion
        if (id.equals(currentAdminId)) {
            throw new BusinessException("Không thể xóa tài khoản của chính mình");
        }

        // Prevent deletion of last admin
        long adminCount = adminDAO.countAll();
        if (adminCount <= 1) {
            throw new BusinessException("Không thể xóa admin cuối cùng trong hệ thống");
        }

        adminDAO.delete(id);
    }

    /**
     * Check if an email already exists.
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return adminDAO.existsByEmail(email.trim());
    }

    /**
     * Check if an email exists excluding a specific admin ID.
     * 
     * @param email     the email to check
     * @param excludeId the admin ID to exclude
     * @return true if email exists for another admin, false otherwise
     */
    public boolean emailExistsExcluding(String email, Integer excludeId) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return adminDAO.existsByEmailExcluding(email.trim(), excludeId);
    }

    // ==================== Admin Home Page Methods ====================

    public long countAllBooks() {
        return AdminHomePageDAO.countAllBooks();
    }

    public long countAllUsers() {
        return AdminHomePageDAO.countAllUsers();
    }

    public long countAllCustomers() {
        return AdminHomePageDAO.countAllCustomers();
    }

    public long countAllReviews() {
        return AdminHomePageDAO.countAllReviews();
    }

    public long countAllOrders() {
        return AdminHomePageDAO.countAllOrders();
    }

    public List<Order> findRecentSales() {
        return AdminHomePageDAO.findRecentSales();
    }

    public List<Review> findRecentReviews() {
        return AdminHomePageDAO.findRecentReviews();
    }

    public List<Order> listAllOrders() {
        List<Order> orders = AdminOrderDAO.findAllOrders();
        return orders;
    }

    public List<Order> listAllOrdersPaginated(int page, int size) {
        return AdminOrderDAO.findAllOrdersPaginated(page, size);
    }

    public long countOrders() {
        return AdminOrderDAO.countAllOrders();
    }

    public Order getOrder(int id) {
        return AdminOrderDAO.findById(id);
    }

    public void deleteOrder(int id) {
        Order o = AdminOrderDAO.findById(id);
        if (o != null) {
            AdminOrderDAO.delete(o);
        }
    }

    public Order getOrderForEdit(int id) {
        return AdminOrderDAO.findByIdWithDetails(id);
    }

    public void updateOrder(
            int orderId,
            String recipientName,
            String paymentMethod,
            Order.OrderStatus status,
            Address address) {
        AdminOrderDAO.updateOrder(orderId, recipientName, paymentMethod, status, address);
    }

    public void updateOrderDetailQty(int detailId, int qty) {
        AdminOrderDAO.updateOrderDetailQty(detailId, qty);
    }

    public void removeOrderDetail(int detailId) {
        AdminOrderDAO.removeOrderDetail(detailId);
    }
}