package com.bookstore.dao;

import com.bookstore.model.OrderDetail;
import jakarta.persistence.EntityManager;

public class OrderDetailDAO {

    private EntityManager em;

    public OrderDetailDAO(EntityManager em) {
        this.em = em;
    }

    public void update(OrderDetail detail) {
        em.merge(detail);
    }

    public void delete(Integer id) {
        OrderDetail od = em.find(OrderDetail.class, id);
        if (od != null) em.remove(od);
    }
}
