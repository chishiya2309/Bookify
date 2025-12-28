package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Address;
import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class AdminOrderDAO {

    // ===== LIST ALL =====
    public static List<Order> findAllOrders() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT DISTINCT o FROM Order o
                        LEFT JOIN FETCH o.customer
                        LEFT JOIN FETCH o.orderDetails
                        ORDER BY o.orderDate DESC
                    """;
            return em.createQuery(jpql, Order.class).getResultList();
        } finally {
            em.close();
        }
    }

    // ===== LIST ALL WITH PAGINATION =====
    public static List<Order> findAllOrdersPaginated(int page, int size) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // First, get paginated order IDs
            String idQuery = """
                        SELECT o.orderId FROM Order o
                        ORDER BY o.orderDate DESC
                    """;
            List<Integer> orderIds = em.createQuery(idQuery, Integer.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();

            if (orderIds.isEmpty()) {
                return List.of();
            }

            // Then fetch full orders with joins for those IDs
            String jpql = """
                        SELECT DISTINCT o FROM Order o
                        LEFT JOIN FETCH o.customer
                        LEFT JOIN FETCH o.orderDetails
                        WHERE o.orderId IN :ids
                        ORDER BY o.orderDate DESC
                    """;
            return em.createQuery(jpql, Order.class)
                    .setParameter("ids", orderIds)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ===== COUNT ALL ORDERS =====
    public static long countAllOrders() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(o) FROM Order o";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    // ===== FIND SIMPLE =====
    public static Order findById(int id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Order.class, id);
        } finally {
            em.close();
        }
    }

    // ===== FIND WITH DETAILS =====
    public static Order findByIdWithDetails(int id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT o FROM Order o
                        LEFT JOIN FETCH o.customer
                        LEFT JOIN FETCH o.shippingAddress
                        LEFT JOIN FETCH o.orderDetails od
                        LEFT JOIN FETCH od.book
                        WHERE o.orderId = :id
                    """;
            return em.createQuery(jpql, Order.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    // ===== UPDATE ORDER INFO =====
    public static void updateOrder(
            int orderId,
            String recipientName,
            String paymentMethod,
            Order.OrderStatus status,
            Address address) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order != null) {
                order.setRecipientName(recipientName);
                order.setPaymentMethod(paymentMethod);
                order.setOrderStatus(status);

                Address a = order.getShippingAddress();
                a.setStreet(address.getStreet());
                a.setWard(address.getWard());
                a.setDistrict(address.getDistrict());
                a.setProvince(address.getProvince());
                a.setZipCode(address.getZipCode());
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //
    private static void recalculateOrderTotal(EntityManager em, Order order) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderDetail d : order.getOrderDetails()) {
            total = total.add(d.getSubTotal());
        }

        order.setTotalAmount(total);
    }

    // ===== UPDATE QTY =====
    public static void updateOrderDetailQty(int detailId, int qty) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            OrderDetail od = em.find(OrderDetail.class, detailId);
            if (od != null) {
                od.setQuantity(qty);

                Order order = od.getOrder();
                recalculateOrderTotal(em, order);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // ===== REMOVE BOOK =====
    public static void removeOrderDetail(int detailId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            OrderDetail od = em.find(OrderDetail.class, detailId);
            if (od != null) {
                Order order = od.getOrder();
                em.remove(od);

                em.flush(); // đảm bảo xóa xong
                recalculateOrderTotal(em, order);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // ===== DELETE ORDER =====
    public static void delete(Order order) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Order managed = em.merge(order);
            managed.getOrderDetails().clear();
            em.remove(managed);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
