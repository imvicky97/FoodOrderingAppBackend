package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

@Repository
public class RestaurantDao {
    @PersistenceContext
    private EntityManager entityManager;

    //To get the list of restaurant by ratings from db
    public List<RestaurantEntity> restaurantsByRating() {
        try {
            return entityManager.createNamedQuery("restaurantsByRating", RestaurantEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public TypedQuery<RestaurantEntity> getAllRestaurants() {
        try {
            return entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class);
        } catch (NoResultException nre) {
            return null;
        }
    }

    //To get restaurant by UUID from db
    public RestaurantEntity getRestaurantByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("getRestaurantByUuid", RestaurantEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<RestaurantCategoryEntity> getCategoryByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("getCategoriesByRestaurant", RestaurantCategoryEntity.class).setParameter("restaurantId", restaurantEntity).getResultList();
        } catch (NoResultException nre) {
            return Collections.emptyList();
        }
    }

    public List<RestaurantEntity> getRestaurantsByName(String restaurantName) {
        try {
            return entityManager.createNamedQuery("getRestaurantByName", RestaurantEntity.class).setParameter("restaurantName", "%" + restaurantName + "%").getResultList();
        } catch (NoResultException nre) {
            return Collections.emptyList();
        }
    }

    public TypedQuery<RestaurantCategoryEntity> restaurantsByCategoryId(CategoryEntity categoryEntity) {
        try {
            return entityManager.createNamedQuery("getRestaurantsByCategory", RestaurantCategoryEntity.class).setParameter("categoryId", categoryEntity);
        } catch (NoResultException nre) {
            return null;
        }
    }

    public RestaurantEntity restaurantsByRestaurantId(String uuid) {
        try {
            return entityManager.createNamedQuery("getRestaurantByUuid", RestaurantEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public RestaurantEntity updateRestaurantEntity(RestaurantEntity restaurantEntity) {
        entityManager.merge(restaurantEntity);
        return restaurantEntity;
    }

}
