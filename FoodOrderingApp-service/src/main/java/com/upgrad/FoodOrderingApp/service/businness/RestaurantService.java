package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;


    /**
     * The method implements the business logic for getAllRestaurants endpoint.
     */
    public List<RestaurantEntity> getAllRestaurants() {
        List<RestaurantEntity> restaurantEntity = restaurantDao.getAllRestaurants().getResultList();
        return restaurantEntity;
    }

    public List<RestaurantCategoryEntity> getCategoryByRestaurant(RestaurantEntity restaurantEntity) throws RestaurantNotFoundException {

        List<RestaurantCategoryEntity> restaurantCategoryEntity = restaurantDao.getCategoryByRestaurant(restaurantEntity);
        return restaurantCategoryEntity;
    }

    public List<RestaurantEntity> getRestaurantByRestaurantName(String restaurantName) throws RestaurantNotFoundException {
        if (restaurantName == null) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }
        List<RestaurantEntity> restaurantEntity = restaurantDao.getRestaurantsByName(restaurantName);

        return restaurantEntity;
    }

}
