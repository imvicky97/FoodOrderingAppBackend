package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {


    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemDao itemDao;


    public List<CategoryEntity> getAllCategories() {
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
}
