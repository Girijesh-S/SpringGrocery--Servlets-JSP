package com.example.dao;

import com.example.entity.UserDetails;
import com.example.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class UserDAO {
    
    public UserDetails findByPhone(String phone) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM UserDetails u WHERE u.phone = :phone", UserDetails.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    public void save(UserDetails user) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public UserDetails findById(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(UserDetails.class, id);
        } finally {
            em.close();
        }
    }
}