package com.bookstore.service;
import org.mindrot.jbcrypt.BCrypt;
import com.bookstore.model.Customer;
import com.bookstore.data.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class CustomerServices extends UserServices {

    private static final int BCrypt_ROUNDS = 12;
    
    @Override
    public boolean login() {
        // code xử lý login cho Customer
        return true;
    }

    @Override
    public void logout() {
        // code xử lý logout cho Customer
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

    /**
     * Lấy Customer từ database theo email
     * @param email Email của customer
     * @return Customer nếu tìm thấy, null nếu không tìm thấy
     */
    private Customer getCustomerByEmail(String email) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.email = :email",
                Customer.class
            );
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Xác thực customer với email và password
     * @param email Email của customer
     * @param password Password (plain text) người dùng nhập
     * @return Customer nếu đăng nhập thành công, null nếu thất bại
     */
    public Customer authenticate(String email, String password) {
        try {
            // Lấy customer từ database theo email
            Customer customer = getCustomerByEmail(email);
            
            if (customer == null) {
                return null; // Không tìm thấy customer
            }
            
            // Lấy password đã hash từ database
            String hashedPasswordInDb = customer.getPassword();
            
            // Verify password với BCrypt
            if (BCrypt.checkpw(password, hashedPasswordInDb)) {
                return customer; // Đăng nhập thành công
            } else {
                return null; // Password không khớp
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method để hash password khi đăng ký
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCrypt_ROUNDS));
    }
}