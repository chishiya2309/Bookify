package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public static void createCategory(Category category) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(category);
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

    public static void updateCategory(Category category) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(category);
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

    public static void deleteCategory(Integer categoryId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Category category = em.find(Category.class, categoryId);
            if (category != null) {
                em.remove(category);
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

    public static List<Category> getAllCategories() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Sắp xếp theo tên A-Z
            String qString = "SELECT c FROM Category c ORDER BY c.name ASC";
            TypedQuery<Category> q = em.createQuery(qString, Category.class);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public static Category getCategoryById(Integer categoryId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Category.class, categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}