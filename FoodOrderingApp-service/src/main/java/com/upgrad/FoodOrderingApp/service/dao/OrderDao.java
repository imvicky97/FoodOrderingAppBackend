package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To get list of OrdersEntity by the restaurant if no result then null is returned
    public List<OrderEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<OrderEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByRestaurant", OrderEntity.class).setParameter("restaurant", restaurantEntity).getResultList();
            return ordersEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    //To save Order in the db
    public OrderEntity saveOrder(OrderEntity ordersEntity) {
        entityManager.persist(ordersEntity);
        return ordersEntity;
    }
}
