package com.bookstore.dao;

import com.bookstore.model.ShoppingCart;
import com.bookstore.model.BookImage;
import com.bookstore.model.CartItem;
import jakarta.persistence.EntityManager;
import com.bookstore.data.DBUtil;
import com.bookstore.model.Customer;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ShoppingCartDAO {
    public ShoppingCart findById(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(ShoppingCart.class, id);
        } finally {
            em.close();
        }
    }

    public ShoppingCart findByCustomer(Customer customer) {
        return findByCustomerId(customer.getUserId());
    }

    public ShoppingCart findByCustomerId(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Lấy cart với items và books
            TypedQuery<ShoppingCart> query = em.createQuery(
                    "SELECT DISTINCT c FROM ShoppingCart c " +
                            "LEFT JOIN FETCH c.items i " +
                            "LEFT JOIN FETCH i.book b " +
                            "WHERE c.customer.userId = :customerId",
                    ShoppingCart.class);
            query.setParameter("customerId", customerId);
            ShoppingCart cart = query.getSingleResult();

            // Chỉ fetch primary image hoặc image có sort order nhỏ nhất (tối ưu
            // performance)
            if (cart != null && !cart.getItems().isEmpty()) {
                for (CartItem item : cart.getItems()) {
                    Integer bookId = item.getBook().getBookId();

                    // Query lấy primary image hoặc image có sort order nhỏ nhất
                    TypedQuery<BookImage> imgQuery = em.createQuery(
                            "SELECT img FROM BookImage img " +
                                    "WHERE img.book.bookId = :bookId " +
                                    "ORDER BY img.isPrimary DESC, img.sortOrder ASC",
                            BookImage.class);
                    imgQuery.setParameter("bookId", bookId);
                    imgQuery.setMaxResults(1);

                    List<BookImage> primaryImages = imgQuery.getResultList();
                    if (!primaryImages.isEmpty()) {
                        // Gắn chỉ primary image vào book
                        item.getBook().getImages().clear();
                        item.getBook().getImages().add(primaryImages.get(0));
                    }

                    // Fetch authors để tránh LazyInitializationException trong cart.jsp
                    item.getBook().getAuthors().size();
                }
            }
            return cart;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void save(ShoppingCart cart) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Re-attach customer nếu đang detached
            if (cart.getCustomer() != null && cart.getCustomer().getUserId() != null) {
                Customer managedCustomer = em.find(Customer.class, cart.getCustomer().getUserId());
                if (managedCustomer != null) {
                    cart.setCustomer(managedCustomer);
                } else {
                    throw new IllegalArgumentException(
                            "Customer with ID " + cart.getCustomer().getUserId() + " does not exist.");
                }
            }

            em.persist(cart);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(ShoppingCart cart) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cart);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            ShoppingCart cart = em.find(ShoppingCart.class, id);
            if (cart != null) {
                em.remove(cart);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
