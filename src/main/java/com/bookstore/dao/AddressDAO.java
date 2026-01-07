package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Address;
import com.bookstore.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * AddressDAO - Data Access Object for Address entity
 * Handles all database operations for customer addresses
 */
public class AddressDAO {

    /**
     * Save new address
     */
    public void save(Address address) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(address);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Find address by ID
     */
    public Address findById(Integer addressId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Address.class, addressId);
        } finally {
            em.close();
        }
    }

    /**
     * Update existing address
     */
    public void update(Address address) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(address);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Delete address by ID
     */
    public void delete(Integer addressId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Address address = em.find(Address.class, addressId);
            if (address != null) {
                em.remove(address);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Find all addresses for a customer
     */
    public List<Address> findByCustomerId(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Address> query = em.createQuery(
                    "SELECT a FROM Address a WHERE a.customer.userId = :customerId ORDER BY a.isDefault DESC, a.addressId DESC",
                    Address.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Find default address for a customer
     */
    public Address findDefaultAddress(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Address> query = em.createQuery(
                    "SELECT a FROM Address a WHERE a.customer.userId = :customerId AND a.isDefault = true",
                    Address.class);
            query.setParameter("customerId", customerId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Set an address as default (and unset others)
     */
    public void setDefaultAddress(Integer addressId, Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Unset all default addresses for this customer
            em.createQuery("UPDATE Address a SET a.isDefault = false WHERE a.customer.userId = :customerId")
                    .setParameter("customerId", customerId)
                    .executeUpdate();

            // Set the new default
            Address address = em.find(Address.class, addressId);
            if (address != null && address.getCustomer().getUserId().equals(customerId)) {
                address.setIsDefault(true);
                em.merge(address);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Count addresses for a customer
     */
    public long countByCustomerId(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM Address a WHERE a.customer.userId = :customerId",
                    Long.class);
            query.setParameter("customerId", customerId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Check if address is used in any order
     * Order has a ManyToOne relationship with Address via shippingAddress field
     * 
     * @param addressId The address ID to check
     * @return true if address is used in any order
     */
    public boolean isAddressUsedInOrder(Integer addressId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.shippingAddress.addressId = :addressId",
                    Long.class);
            query.setParameter("addressId", addressId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, giả sử địa chỉ đang được sử dụng để tránh xóa nhầm
            return true;
        } finally {
            em.close();
        }
    }

    /**
     * Fallback method: Check if address is used in any order by matching address
     * fields
     */
    private boolean isAddressUsedInOrderByFields(Integer addressId, EntityManager em) {
        try {
            Address address = em.find(Address.class, addressId);
            if (address == null) {
                return false;
            }

            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.customer.userId = :customerId " +
                            "AND o.shippingStreet = :street " +
                            "AND o.shippingWard = :ward " +
                            "AND o.shippingDistrict = :district " +
                            "AND o.shippingProvince = :province",
                    Long.class);
            query.setParameter("customerId", address.getCustomer().getUserId());
            query.setParameter("street", address.getStreet());
            query.setParameter("ward", address.getWard());
            query.setParameter("district", address.getDistrict());
            query.setParameter("province", address.getProvince());
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            // If any error, assume address can be deleted
            return false;
        }
    }
}
