package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class CategoryService {


    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private RestaurantCategoryDao restaurantCategoryDao;


    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return categoryDao.getAllCategory().getResultList();
    }

    public CategoryEntity getCategoryById(String categoryId) throws CategoryNotFoundException {
        CategoryEntity categoryEntities = categoryDao.getCategoryById(categoryId);
        if (categoryId == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        if (categoryEntities == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return categoryEntities;
    }

    public List<ItemEntity> getItemsById(CategoryEntity categoryEntity) {
        List<ItemEntity> itemEntities = new ArrayList<>();
        List<CategoryItemEntity> categoryItemEntity = categoryDao.getItemByCategoryId(categoryEntity);
        for (CategoryItemEntity ce : categoryItemEntity) {
            ItemEntity itemEntity = itemDao.getItemById(ce.getItemId().getUuid());
            itemEntities.add(itemEntity);
        }
        return itemEntities;
    }

    /* This method is to get Categories By Restaurant and returns list of CategoryEntity. Its takes restaurantUuid as the input.
    If error throws exception with error code and error message.
    */
    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUuid) {

        //Calls getRestaurantByUuid of restaurantDao to get RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);

        //Calls getCategoriesByRestaurant of restaurantCategoryDao to get list of RestaurantCategoryEntity
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getCategoriesByRestaurant(restaurantEntity.getUuid());

        //Creating the list of the Category entity to be returned.
        List<CategoryEntity> categoryEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            categoryEntities.add(restaurantCategoryEntity.getCategoryId());
        });
        return categoryEntities;
    }
}
