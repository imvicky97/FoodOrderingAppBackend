package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;


    public CategoryEntity getCategoryById(String categoryId) {
        try {
            return entityManager.createNamedQuery("getCategoryById", CategoryEntity.class).setParameter("uuid", categoryId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


}
