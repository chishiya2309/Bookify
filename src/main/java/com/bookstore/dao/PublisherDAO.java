package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Publisher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class PublisherDAO {
    public static void createPublisher(Publisher publisher) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(publisher);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
        } finally {
            em.close();
        }
    }

    public static void updatePublisher(Publisher publisher) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(publisher);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
        } finally {
            em.close();
        }
    }

    public static void deletePublisher(Integer publisherId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Publisher publisher = em.find(Publisher.class, publisherId);
            if (publisher != null) {
                em.remove(publisher);
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
        } finally {
            em.close();
        }
    }

    public static List<Publisher> getAllPublishers() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Sắp xếp theo tên A-Z
            String qString = "SELECT p FROM Publisher p ORDER BY p.name ASC";
            TypedQuery<Publisher> q = em.createQuery(qString, Publisher.class);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public static Publisher getPublisherById(Integer publisherId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Publisher.class, publisherId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Publisher findByName(String name) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT p FROM Publisher p WHERE p.name = :name";
            TypedQuery<Publisher> q = em.createQuery(qString, Publisher.class);
            q.setParameter("name", name);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Đếm số sách thuộc nhà xuất bản để kiểm tra trước khi xoá
     */
    public static long countBooksByPublisher(Integer publisherId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT COUNT(b) FROM Book b WHERE b.publisher.publisherId = :publisherId";
            TypedQuery<Long> q = em.createQuery(qString, Long.class);
            q.setParameter("publisherId", publisherId);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }
}