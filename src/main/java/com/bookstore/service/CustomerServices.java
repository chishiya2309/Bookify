package com.bookstore.service;
import com.bookstore.model.Customer;
import com.bookstore.model.Category;
import com.bookstore.data.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import com.bookstore.dao.CustomerHomePageDAO;
import com.bookstore.model.Book;
import java.util.List;

public class CustomerServices {

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
    
    public List<Book> listMostFavoredBooks() {
        return CustomerHomePageDAO.listMostFavoredBooks();
    }
}