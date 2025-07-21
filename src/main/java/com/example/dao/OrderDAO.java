package com.example.dao;

import com.example.entity.Order;
import com.example.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class OrderDAO {
    
    /**
     * Save a new order - returns boolean to indicate success/failure
     */
    public boolean save(Order order) {
        System.out.println("=== OrderDAO.save() called ===");
        System.out.println("Order details:");
        System.out.println("  - Item: " + order.getItemName());
        System.out.println("  - Quantity: " + order.getQuantity());
        System.out.println("  - User: " + order.getUser().getName());
        
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(order);
            tx.commit();
            
            System.out.println("✅ Order saved successfully with ID: " + order.getId());
            if (order.getOrderDate() != null) {
                System.out.println("✅ Order date: " + order.getOrderDate());
            }
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error saving order for item " + order.getItemName() + ": " + e.getMessage());
            e.printStackTrace();
            if (tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            em.close();
        }
    }
    
    /**
     * Find all orders for a specific user
     */
    public List<Order> findByUserId(Long userId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Order> orders = em.createQuery(
                "SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC", 
                Order.class)
                .setParameter("userId", userId)
                .getResultList();
            
            System.out.println("Found " + orders.size() + " orders for user ID: " + userId);
            return orders;
            
        } catch (Exception e) {
            System.err.println("Error finding orders for user ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Return empty list instead of null
        } finally {
            em.close();
        }
    }
    
    /**
     * Find order by ID
     */
    public Order findById(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Order order = em.find(Order.class, id);
            return order;
        } catch (Exception e) {
            System.err.println("Error finding order by ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    /**
     * Get all orders (for admin purposes)
     */
    public List<Order> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT o FROM Order o ORDER BY o.orderDate DESC", 
                Order.class)
                .getResultList();
        } catch (Exception e) {
            System.err.println("Error finding all orders: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }
}
