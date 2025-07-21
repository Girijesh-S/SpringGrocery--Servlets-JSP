package com.example.dao;

import com.example.entity.Item;
import com.example.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.util.List;

public class ItemDAO {
    
    public Item findById(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Item item = em.find(Item.class, id);
            return item;
        } catch (Exception e) {
            System.err.println("Error finding item by ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public Item findByIdWithQuantities(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Item item = em.createQuery(
                "SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.allowedQuantities WHERE i.id = :id", 
                Item.class)
                .setParameter("id", id)
                .getSingleResult();
            return item;
        } catch (NoResultException e) {
            System.err.println("No item found with ID: " + id);
            return null;
        } catch (Exception e) {
            System.err.println("Error finding item with quantities by ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public List<Item> findByCategoryId(Long categoryId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Item> items = em.createQuery(
                "SELECT i FROM Item i WHERE i.category.id = :categoryId ORDER BY i.name", 
                Item.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
            
            System.out.println("Found " + items.size() + " items for category ID: " + categoryId);
            return items;
        } catch (Exception e) {
            System.err.println("Error finding items by category ID " + categoryId + ": " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Return empty list instead of null
        } finally {
            em.close();
        }
    }
    
    /**
     * Find item by name and category to check for duplicates
     */
    public Item findByNameAndCategory(String name, Long categoryId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            List<Item> items = em.createQuery(
                "SELECT i FROM Item i WHERE i.name = :name AND i.category.id = :categoryId", 
                Item.class)
                .setParameter("name", name)
                .setParameter("categoryId", categoryId)
                .getResultList();
            
            return items.isEmpty() ? null : items.get(0);
        } catch (Exception e) {
            System.err.println("Error finding item by name and category: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    /**
     * Check if item exists by name and category
     */
    public boolean existsByNameAndCategory(String name, Long categoryId) {
        return findByNameAndCategory(name, categoryId) != null;
    }
    
    /**
     * Update an existing item - returns boolean to indicate success/failure
     */
    public boolean update(Item item) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // Ensure the item exists before updating
            Item existingItem = em.find(Item.class, item.getId());
            if (existingItem == null) {
                System.err.println("Cannot update - item with ID " + item.getId() + " not found");
                tx.rollback();
                return false;
            }
            
            // Merge the updated item
            em.merge(item);
            tx.commit();
            
            System.out.println("Successfully updated item: " + item.getName() + 
                             " (ID: " + item.getId() + ") - New stock: " + item.getAvailableStock());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error updating item " + item.getName() + " (ID: " + item.getId() + "): " + e.getMessage());
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
     * Save a new item - returns boolean to indicate success/failure
     */
    public boolean save(Item item) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(item);
            tx.commit();
            
            System.out.println("Successfully saved new item: " + item.getName());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error saving item " + item.getName() + ": " + e.getMessage());
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
     * Refresh item data from database to get latest values
     */
    public Item refresh(Item item) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Item refreshedItem = em.find(Item.class, item.getId());
            if (refreshedItem != null) {
                em.refresh(refreshedItem);
            }
            return refreshedItem;
        } catch (Exception e) {
            System.err.println("Error refreshing item " + item.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return item; // Return original if refresh fails
        } finally {
            em.close();
        }
    }
}