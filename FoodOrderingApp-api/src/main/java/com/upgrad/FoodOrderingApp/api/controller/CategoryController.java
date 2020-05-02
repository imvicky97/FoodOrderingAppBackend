package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping(path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> categories() {
        List<CategoryEntity> categoryEntityList = categoryService.getAllCategories();
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();

        List<CategoryListResponse> categoryListResponse = new ArrayList<>();


        if (!categoryEntityList.isEmpty()) {
            for (CategoryEntity categoryEntity : categoryEntityList) {
                categoryListResponse.add(new CategoryListResponse().id(UUID.fromString(categoryEntity.getUuid())).categoryName(categoryEntity.getCategoryName()));
            }
            categoriesListResponse.setCategories(categoryListResponse);
            return new ResponseEntity<>(categoriesListResponse, HttpStatus.OK);

        } else {
            categoriesListResponse.setCategories(Collections.emptyList());
            return new ResponseEntity<>(categoriesListResponse, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable("category_id") final String categoryId) throws CategoryNotFoundException {

        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);

        List<ItemEntity> itemEntityList = categoryService.getItemsById(categoryEntity);
        CategoryDetailsResponse categoryListResponse = new CategoryDetailsResponse().id(UUID.fromString(categoryEntity.getUuid())).categoryName(categoryEntity.getCategoryName());

        for (ItemEntity itemEntity : itemEntityList) {
            ItemList itemList = new ItemList();
            itemList.setId(UUID.fromString(itemEntity.getUuid()));
            itemList.setItemName(itemEntity.getItemName());
            itemList.setPrice(itemEntity.getPrice());
            ItemList.ItemTypeEnum itemType = itemEntity.getType().equals("1") ? ItemList.ItemTypeEnum.NON_VEG : ItemList.ItemTypeEnum.VEG;
            itemList.setItemType(itemType);
            categoryListResponse.addItemListItem(itemList);
        }

        return new ResponseEntity<>(categoryListResponse, HttpStatus.OK);
    }
}
