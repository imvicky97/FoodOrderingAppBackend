package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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


    @GetMapping(path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() throws RestaurantNotFoundException {

        List<RestaurantEntity> restaurantEntityList = restaurantService.getAllRestaurants();                               //Calling getAllRestaurant function from restaurant Service
        RestaurantListResponse listResponse = new RestaurantListResponse();


        buildResponseFromRestaurant(restaurantEntityList, listResponse);

        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/restaurant/name/{reastaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByName(@PathVariable("reastaurant_name") final String restaurantName) throws RestaurantNotFoundException {
        List<RestaurantEntity> restaurantEntityList = restaurantService.getRestaurantByRestaurantName(restaurantName);
        RestaurantListResponse listResponse = new RestaurantListResponse();

        if (restaurantEntityList.isEmpty()) {
            listResponse.setRestaurants(Collections.emptyList());
            return new ResponseEntity<>(listResponse, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(buildResponseFromRestaurant(restaurantEntityList, listResponse), HttpStatus.OK);
        }
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
            restaurantDetailsResponseAddress.setId(restaurantEntity.getAddress().getUuid());
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
