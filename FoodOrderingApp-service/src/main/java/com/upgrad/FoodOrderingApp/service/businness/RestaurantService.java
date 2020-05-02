package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<RestaurantEntity> getRestaurantsByCategory(String categoryId) throws CategoryNotFoundException {
        if (categoryId == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryId);
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }


        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantDao.restaurantsByCategoryId(categoryEntity).getResultList();
        List<RestaurantEntity> restaurantEntities = new ArrayList<>();
        if (!restaurantCategoryEntities.isEmpty()) {
            restaurantCategoryEntities.stream()
                    .map(e -> restaurantEntities.add(e.getRestaurant()))
                    .collect(Collectors.toList());
            return restaurantEntities;

        } else {
            return Collections.emptyList();
        }
    }

    public RestaurantEntity getRestaurantByRestaurantId(String restaurantId) throws RestaurantNotFoundException {
        if (restaurantId == null) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }
        RestaurantEntity restaurantEntity = restaurantDao.restaurantsByRestaurantId(restaurantId);
        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        return restaurantEntity;
    }

    public List<CategoryItemEntity> getItemByCategoryId(RestaurantCategoryEntity restaurantCategoryEntity) throws RestaurantNotFoundException {

        List<CategoryItemEntity> categoryItemEntity = categoryDao.getItemByCategoryId(restaurantCategoryEntity.getCategoryId());

        return categoryItemEntity;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity, Double cu) throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {


        if (restaurantEntity.getId() == null) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        if (restaurantEntity.getCustomerRating() == null || (restaurantEntity.getCustomerRating() < 1 && restaurantEntity.getCustomerRating() > 5)) {
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        RestaurantEntity entity = restaurantDao.restaurantsByRestaurantId(restaurantEntity.getUuid());
        if (entity.getId().equals(restaurantEntity.getId())) {
            restaurantEntity.setCustomerRating(restaurantEntity.getCustomerRating());
            restaurantEntity.setNumberCustomersRated(entity.getNumberCustomersRated() + 1);
            return restaurantDao.updateRestaurantEntity(restaurantEntity);
        }
        return restaurantEntity;
    }


}
