package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoryList;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantUpdatedResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CustomerService customerService;


    // Get all restaurants
    @GetMapping(path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() throws RestaurantNotFoundException {

        List<RestaurantEntity> restaurantEntityList = restaurantService.getAllRestaurants();                               //Calling getAllRestaurant function from restaurant Service
        RestaurantListResponse listResponse = new RestaurantListResponse();


        buildResponseFromRestaurant(restaurantEntityList, listResponse);

        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

    // Get restaurants by name
    @GetMapping(path = "/restaurant/name/{reastaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByName(@PathVariable("reastaurant_name") final String restaurantName) throws RestaurantNotFoundException {
        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByName(restaurantName);

        RestaurantListResponse listResponse = new RestaurantListResponse();

        if (restaurantEntityList.isEmpty()) {
            listResponse.setRestaurants(Collections.emptyList());
            return new ResponseEntity<>(listResponse, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(buildResponseFromRestaurant(restaurantEntityList, listResponse), HttpStatus.OK);
        }
    }

    // Get restaurants by category
    @GetMapping(path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategoryId(@PathVariable("category_id") final String categoryId) throws CategoryNotFoundException, RestaurantNotFoundException {
        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantByCategory(categoryId);

        RestaurantListResponse restaurantResponseList = new RestaurantListResponse();

        if (restaurantEntityList.isEmpty()) {
            restaurantResponseList.setRestaurants(Collections.emptyList());
            return new ResponseEntity<>(restaurantResponseList, HttpStatus.OK);

        } else {
            buildResponseFromRestaurant(restaurantEntityList, restaurantResponseList);
        }
        return new ResponseEntity<>(restaurantResponseList, HttpStatus.OK);
    }

    // Get Restaurant by id
    @GetMapping(path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByRestaurantId(@PathVariable("restaurant_id") final String restaurant_id) throws RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurant_id);


        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState();
        restaurantDetailsResponseAddressState.setId(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()));
        restaurantDetailsResponseAddressState.setStateName(restaurantEntity.getAddress().getState().getStateName());

        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress();
        restaurantDetailsResponseAddress.setId(UUID.fromString(restaurantEntity.getAddress().getUuid()));
        restaurantDetailsResponseAddress.setFlatBuildingName(restaurantEntity.getAddress().getFlatBuilNumber());
        restaurantDetailsResponseAddress.setLocality(restaurantEntity.getAddress().getLocality());
        restaurantDetailsResponseAddress.setCity(restaurantEntity.getAddress().getCity());
        restaurantDetailsResponseAddress.setPincode(restaurantEntity.getAddress().getCity());
        restaurantDetailsResponseAddress.setState(restaurantDetailsResponseAddressState);

        List<RestaurantCategoryEntity> restaurantCategoryEntityList = restaurantService.getCategoryByRestaurant(restaurantEntity);
        List<CategoryList> categoryLists = new ArrayList<>();
        List<ItemList> itemLists = new ArrayList<>();
        for (RestaurantCategoryEntity restaurantCategoryEntity : restaurantCategoryEntityList) {
            CategoryList categoryList = new CategoryList();

            categoryList.setId(UUID.fromString(restaurantCategoryEntity.getCategoryId().getUuid()));
            categoryList.setCategoryName(restaurantCategoryEntity.getCategoryId().getCategoryName());

            List<CategoryItemEntity> categoryItemEntityList = restaurantService.getItemByCategoryId(restaurantCategoryEntity);
            for (CategoryItemEntity categoryItemEntity : categoryItemEntityList) {
                ItemList itemList = new ItemList();
                itemList.setId(UUID.fromString(categoryItemEntity.getItemId().getUuid()));
                itemList.setItemName(categoryItemEntity.getItemId().getItemName());
                itemList.setPrice(categoryItemEntity.getItemId().getPrice());
                ItemList.ItemTypeEnum itemType = categoryItemEntity.getItemId().getType().equals("1") ? ItemList.ItemTypeEnum.NON_VEG : ItemList.ItemTypeEnum.VEG;
                itemList.setItemType(itemType);
                itemLists.add(itemList);
            }
            categoryList.setItemList(itemLists);
            categoryLists.add(categoryList);
        }

        RestaurantDetailsResponse restaurantResponseList = new RestaurantDetailsResponse().id(UUID.fromString(restaurantEntity.getUuid())).restaurantName(restaurantEntity.getRestaurantName())
                .photoURL(restaurantEntity.getPhotoUrl()).customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating())).averagePrice(restaurantEntity.getAvgPrice()).numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                .address(restaurantDetailsResponseAddress).categories(categoryLists);

        return new ResponseEntity(restaurantResponseList, HttpStatus.OK);
    }

    // Update restaurant details
    @PutMapping(path = "/restaurant/{restaurant_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@PathVariable("restaurant_id") final String restaurantId, @RequestHeader("authorization") final String authorization,
                                                                             @RequestParam("customerRating") final Double customerRating) throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setUuid(restaurantId);
        restaurantEntity.setCustomerRating(customerRating);

        customerService.getCustomer(authorization);

        RestaurantEntity restaurantDetailsEntity = restaurantService.updateRestaurantRating(restaurantEntity, customerRating);

        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse().id(UUID.fromString(restaurantDetailsEntity.getUuid())).status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<>(restaurantUpdatedResponse, HttpStatus.OK);
    }


    private RestaurantListResponse buildResponseFromRestaurant(List<RestaurantEntity> restaurantEntityList, RestaurantListResponse listResponse) throws RestaurantNotFoundException {
        for (RestaurantEntity restaurantEntity : restaurantEntityList) {
            List<RestaurantCategoryEntity> categoryEntities = restaurantService.getCategoryByRestaurant(restaurantEntity);         //Calling getCategoryByRestaurant

            String categoryNames = null;
            List<String> categoryNamesList = new ArrayList<>();

            for (RestaurantCategoryEntity ce : categoryEntities)                                       //Iterating RestaurantCategoryEntity
            {
                categoryNamesList.add(ce.getCategoryId().getCategoryName());                                  //Adding Category Name to the list
                Collections.sort(categoryNamesList);                                                          //Sorting the list
            }

            categoryNames = String.join(",", categoryNamesList);

            RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState();
            restaurantDetailsResponseAddressState.setId(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()));
            restaurantDetailsResponseAddressState.setStateName(restaurantEntity.getAddress().getState().getStateName());

            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress();
            restaurantDetailsResponseAddress.setId(UUID.fromString(restaurantEntity.getAddress().getUuid()));
            restaurantDetailsResponseAddress.setFlatBuildingName(restaurantEntity.getAddress().getFlatBuilNumber());
            restaurantDetailsResponseAddress.setLocality(restaurantEntity.getAddress().getLocality());
            restaurantDetailsResponseAddress.setCity(restaurantEntity.getAddress().getCity());
            restaurantDetailsResponseAddress.setPincode(restaurantEntity.getAddress().getPincode());
            restaurantDetailsResponseAddress.setState(restaurantDetailsResponseAddressState);

            RestaurantList restaurantListItem = new RestaurantList();
            restaurantListItem.setId(UUID.fromString(restaurantEntity.getUuid()));
            restaurantListItem.setRestaurantName(restaurantEntity.getRestaurantName());
            restaurantListItem.setPhotoURL(restaurantEntity.getPhotoUrl());
            restaurantListItem.setCustomerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
            restaurantListItem.setAveragePrice(restaurantEntity.getAvgPrice());
            restaurantListItem.setNumberCustomersRated(restaurantEntity.getNumberCustomersRated());
            restaurantListItem.setAddress(restaurantDetailsResponseAddress);
            restaurantListItem.setCategories(categoryNames);

            listResponse.addRestaurantsItem(restaurantListItem);
        }
        return listResponse;
    }

}
