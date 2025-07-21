package com.example.dao;

import com.example.entity.Category;
import com.example.entity.Item;
import com.example.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class CategoryDAO {
    
    /**
     * Find category by ID with items - FIXED to prevent duplicates
     */
    public Category findByIdWithItems(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            // First get the category
            Category category = em.find(Category.class, id);
            if (category == null) {
                return null;
            }
            
            // Then get items separately to avoid duplicates from JOIN FETCH
            List<Item> items = em.createQuery(
                "SELECT DISTINCT i FROM Item i " +
                "LEFT JOIN FETCH i.allowedQuantities " +
                "LEFT JOIN FETCH i.category " +
                "WHERE i.category.id = :categoryId " +
                "ORDER BY i.name", 
                Item.class)
                .setParameter("categoryId", id)
                .getResultList();
            
            // Remove any duplicates based on ID (keep the list as List<Item>)
            Set<Long> seenIds = new HashSet<Long>();
            List<Item> uniqueItems = new ArrayList<Item>();
            
            for (Item item : items) {
                if (seenIds.add(item.getId())) {
                    uniqueItems.add(item);
                }
            }
            
            // Set the unique items to category (as List<Item>)
            category.setItems(uniqueItems);
            
            System.out.println("CategoryDAO: Found category " + category.getName() + " with " + uniqueItems.size() + " unique items");
            
            return category;
            
        } catch (Exception e) {
            System.err.println("Error finding category with items by ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    /**
     * Find category by ID only
     */
    public Category findById(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Category.class, id);
        } catch (Exception e) {
            System.err.println("Error finding category by ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    /**
     * Find all categories
     */
    public List<Category> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error finding all categories: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }
    
    /**
     * Save category
     */
    public boolean save(Category category) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(category);
            tx.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Error saving category: " + e.getMessage());
            e.printStackTrace();
            if (tx.isActive()) {
                tx.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }
}
