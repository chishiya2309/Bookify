package com.bookstore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.dao.CustomerHomePageDAO;
import com.bookstore.data.DBUtil;
import com.bookstore.model.Book;
import com.bookstore.model.Category;
import com.bookstore.model.Customer;
import com.bookstore.service.AdminServices.ValidationException;

import jakarta.persistence.EntityManager;

public class CustomerServices {

    private final CustomerDAO customerDAO;
    
    // Phone number pattern: starts with 0 or +84, followed by 9 digits
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+84|0)[0-9]{9}$");
    
    public CustomerServices() {
        this.customerDAO = new CustomerDAO();
    }
    
    // Constructor for dependency injection (useful for testing)
    public CustomerServices(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    // ==================== Validation Methods ====================
    
    /**
     * Validates email format using the same pattern as User entity.
     * 
     * @param email the email to validate
     * @return null if valid, error message if invalid
     */
    public String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email không được để trống";
        }
        if (!ValidationUtil.isValidEmail(email.trim())) {
            return "Email không đúng định dạng";
        }
        if (ValidationUtil.containsSqlInjection(email)) {
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
    
    /**
     * Validates phone number format (starts with 0 or +84, followed by 9 digits).
     * 
     * @param phoneNumber the phone number to validate
     * @return null if valid, error message if invalid
     */
    public String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Số điện thoại không được để trống";
        }
        if (!PHONE_PATTERN.matcher(phoneNumber.trim()).matches()) {
            return "Số điện thoại không hợp lệ";
        }
        if (ValidationUtil.containsSqlInjection(phoneNumber)) {
            return "Dữ liệu không hợp lệ";
        }
        return null; // Valid
    }
    
    // ==================== CRUD Service Methods ====================
    
    /**
     * List customers with pagination and optional search.
     * 
     * @param page the page number (1-based)
     * @param pageSize the number of items per page
     * @param search optional search keyword (can be null or empty)
     * @return list of customers for the requested page
     */
    public List<Customer> listCustomers(int page, int pageSize, String search) {
        int offset = (page - 1) * pageSize;
        
        if (search != null && !search.trim().isEmpty()) {
            return customerDAO.search(search.trim(), offset, pageSize);
        }
        return customerDAO.findAll(offset, pageSize);
    }
    
    /**
     * Count total customers for pagination, with optional search filter.
     * 
     * @param search optional search keyword (can be null or empty)
     * @return total count of customers matching the criteria
     */
    public long countCustomers(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return customerDAO.countBySearch(search.trim());
        }
        return customerDAO.countAll();
    }
    
    /**
     * Get a single customer by ID.
     * 
     * @param id the customer ID
     * @return the customer or null if not found
     */
    public Customer getCustomerById(Integer id) {
        if (id == null) {
            return null;
        }
        return customerDAO.findById(id);
    }
    
    /**
     * Get a single customer by ID with all details (addresses, orders, reviews) loaded.
     * Use this method when displaying customer detail page.
     * 
     * @param id the customer ID
     * @return the customer with all collections initialized, or null if not found
     */
    public Customer getCustomerByIdWithDetails(Integer id) {
        if (id == null) {
            return null;
        }
        return customerDAO.findByIdWithDetails(id);
    }
    
    /**
     * Get a single customer by email.
     * 
     * @param email the customer email
     * @return the customer or null if not found
     */
    public Customer getCustomerByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return customerDAO.findByEmail(email.trim());
    }
    
    /**
     * Private helper method that validates inputs, creates, and saves a new customer.
     * 
     * @param email the customer email
     * @param password the plain text password (will be hashed)
     * @param fullName the customer's full name
     * @param phoneNumber the customer's phone number
     * @return the created and saved customer
     * @throws ValidationException if validation fails
     */
    private Customer validateAndCreateCustomer(String email, String password, String fullName, String phoneNumber) throws ValidationException {
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
        
        // Validate phone number
        String phoneError = validatePhoneNumber(phoneNumber);
        if (phoneError != null) {
            throw new ValidationException(phoneError);
        }
        
        // Check for duplicate email
        if (customerDAO.existsByEmail(email.trim())) {
            throw new ValidationException("Email đã được sử dụng");
        }
        
        // Hash password with BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        // Create and save customer
        Customer customer = new Customer(email.trim(), hashedPassword, fullName.trim(), phoneNumber.trim());
        customer.setRegisterDate(LocalDateTime.now());
        customerDAO.save(customer);
        
        return customer;
    }
    
    /**
     * Create a new customer with BCrypt-hashed password.
     * 
     * @param email the customer email
     * @param password the plain text password (will be hashed)
     * @param fullName the customer's full name
     * @param phoneNumber the customer's phone number
     * @throws ValidationException if validation fails
     */
    public void createCustomer(String email, String password, String fullName, String phoneNumber) throws ValidationException {
        validateAndCreateCustomer(email, password, fullName, phoneNumber);
    }
    
    /**
     * Create a new customer with BCrypt-hashed password and addresses.
     */
    public Customer createCustomerWithAddresses(String email, String password, String fullName, String phoneNumber) throws ValidationException {
        return validateAndCreateCustomer(email, password, fullName, phoneNumber);
    }
    
    /**
     * Update an existing customer.
     * If password is empty/null, the existing password is preserved.
     * Email cannot be changed (read-only).
     * 
     * @param id the customer ID to update
     * @param fullName the new full name
     * @param password the new password (empty/null to keep existing)
     * @param phoneNumber the new phone number
     * @throws ValidationException if validation fails or customer not found
     */
    public void updateCustomer(Integer id, String fullName, String password, String phoneNumber) throws ValidationException {
        // Find existing customer
        Customer existingCustomer = customerDAO.findById(id);
        if (existingCustomer == null) {
            throw new ValidationException("Khách hàng không tồn tại");
        }
        
        // Validate full name
        String fullNameError = validateFullName(fullName);
        if (fullNameError != null) {
            throw new ValidationException(fullNameError);
        }
        
        // Validate phone number
        String phoneError = validatePhoneNumber(phoneNumber);
        if (phoneError != null) {
            throw new ValidationException(phoneError);
        }
        
        // Update fields
        existingCustomer.setFullName(fullName.trim());
        existingCustomer.setPhoneNumber(phoneNumber.trim());
        
        // Update password only if provided
        if (password != null && !password.isEmpty()) {
            String passwordError = validatePassword(password);
            if (passwordError != null) {
                throw new ValidationException(passwordError);
            }
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            existingCustomer.setPassword(hashedPassword);
        }
        
        customerDAO.update(existingCustomer);
    }
    

    public void deleteCustomer(Integer id) throws ValidationException {
        // Check if customer exists
        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            throw new ValidationException("Khách hàng không tồn tại");
        }
        
        customerDAO.delete(id);
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
        return customerDAO.existsByEmail(email.trim());
    }
    
    // ==================== Customer Home Page Methods ====================

    public List<Category> listAllCategories() {
        return CustomerHomePageDAO.listAllCategories();
    }

    public List<Book> listNewBooks() {
        return CustomerHomePageDAO.listNewBooks();
    }

    public List<Book> listBestSellingBooks() {
        return CustomerHomePageDAO.listBestSellingBooks();
    }

    /**
     * Lấy Customer từ database theo userId (DEV/TEST only)
     * @param userId User ID của customer
     * @return Customer nếu tìm thấy, null nếu không tìm thấy
     */
    public Customer findById(Integer userId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Customer.class, userId);
        } finally {
            em.close();
        }
    }
    
    public List<Book> listMostFavoredBooks() {
        return CustomerHomePageDAO.listMostFavoredBooks();
    }
}