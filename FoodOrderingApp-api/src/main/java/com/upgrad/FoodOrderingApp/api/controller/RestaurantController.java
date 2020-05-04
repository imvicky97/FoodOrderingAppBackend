package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoryList;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantUpdatedResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;


    // Get all restaurants
    @GetMapping(path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() throws RestaurantNotFoundException {

        //Calls restaurantsByRating method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByRating();

        //Creating restaurant list for the response
        List<RestaurantList> restaurantLists = new LinkedList<>();
        for (RestaurantEntity restaurantEntity : restaurantEntities) { //Looping for each restaurant entity in restaurantEntities

            //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
            List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            //To concat the category names.
            List<String> categoryNames = categoryEntities.stream()
                    .map(CategoryEntity::getCategoryName)
                    .collect(Collectors.toList());
            String categories = String.join(",", categoryNames);


            //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
            RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                    .stateName(restaurantEntity.getAddress().getState().getStateName());

            //Creating the RestaurantDetailsResponseAddress for the RestaurantList
            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                    .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                    .city(restaurantEntity.getAddress().getCity())
                    .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNumber())
                    .locality(restaurantEntity.getAddress().getLocality())
                    .pincode(restaurantEntity.getAddress().getPincode())
                    .state(restaurantDetailsResponseAddressState);

            //Creating RestaurantList to add to list of RestaurantList
            RestaurantList restaurantList = new RestaurantList()
                    .id(UUID.fromString(restaurantEntity.getUuid()))
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .averagePrice(restaurantEntity.getAvgPrice())
                    .categories(categories)
                    .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                    .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .photoURL(restaurantEntity.getPhotoUrl())
                    .address(restaurantDetailsResponseAddress);

            //Adding it to the list
            restaurantLists.add(restaurantList);

        }

        //Creating the RestaurantListResponse by adding the list of RestaurantList
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    // Get restaurants by name
    @GetMapping(path = "/restaurant/name/{reastaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByName(@PathVariable("reastaurant_name") final String restaurantName) throws RestaurantNotFoundException {
        //Calls restaurantsByName method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByName(restaurantName);

        if (!restaurantEntities.isEmpty()) {//Checking if the restaurant entity is empty

            //Creating restaurant list for the response
            List<RestaurantList> restaurantLists = new LinkedList<>();
            for (RestaurantEntity restaurantEntity : restaurantEntities) {  //Looping for each restaurant entity in restaurantEntities

                //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
                List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());

                List<String> categoryNames = categoryEntities.stream()
                        .map(CategoryEntity::getCategoryName)
                        .collect(Collectors.toList());
                String categories = String.join(",", categoryNames);

                //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
                RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                        .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                        .stateName(restaurantEntity.getAddress().getState().getStateName());

                //Creating the RestaurantDetailsResponseAddress for the RestaurantList
                RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                        .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                        .city(restaurantEntity.getAddress().getCity())
                        .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNumber())
                        .locality(restaurantEntity.getAddress().getLocality())
                        .pincode(restaurantEntity.getAddress().getPincode())
                        .state(restaurantDetailsResponseAddressState);

                //Creating RestaurantList to add to list of RestaurantList
                RestaurantList restaurantList = new RestaurantList()
                        .id(UUID.fromString(restaurantEntity.getUuid()))
                        .restaurantName(restaurantEntity.getRestaurantName())
                        .averagePrice(restaurantEntity.getAvgPrice())
                        .categories(categories)
                        .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                        .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                        .photoURL(restaurantEntity.getPhotoUrl())
                        .address(restaurantDetailsResponseAddress);

                //Adding it to the list
                restaurantLists.add(restaurantList);

            }

            //Creating the RestaurantListResponse by adding the list of RestaurantList
            RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
            return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new RestaurantListResponse(), HttpStatus.OK);
        }

    }

    // Get restaurants by category
    @GetMapping(path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategoryId(@PathVariable("category_id") final String categoryId) throws CategoryNotFoundException, RestaurantNotFoundException {
        //Calls restaurantByCategory method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantByCategory(categoryId);

        //Creating restaurant list for the response
        List<RestaurantList> restaurantLists = new LinkedList<>();
        for (RestaurantEntity restaurantEntity : restaurantEntities) { //Looping for each restaurant entity in restaurantEntities

            //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
            List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            List<String> categoryNames = categoryEntities.stream()
                    .map(CategoryEntity::getCategoryName)
                    .collect(Collectors.toList());
            String categories = String.join(",", categoryNames);


            //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
            RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                    .stateName(restaurantEntity.getAddress().getState().getStateName());

            //Creating the RestaurantDetailsResponseAddress for the RestaurantList
            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                    .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                    .city(restaurantEntity.getAddress().getCity())
                    .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNumber())
                    .locality(restaurantEntity.getAddress().getLocality())
                    .pincode(restaurantEntity.getAddress().getPincode())
                    .state(restaurantDetailsResponseAddressState);

            //Creating RestaurantList to add to list of RestaurantList
            RestaurantList restaurantList = new RestaurantList()
                    .id(UUID.fromString(restaurantEntity.getUuid()))
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .averagePrice(restaurantEntity.getAvgPrice())
                    .categories(categories)
                    .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                    .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .photoURL(restaurantEntity.getPhotoUrl())
                    .address(restaurantDetailsResponseAddress);

            //Adding it to the list
            restaurantLists.add(restaurantList);

        }

        //Creating the RestaurantListResponse by adding the list of RestaurantList
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }

    // Get Restaurant by id
    @GetMapping(path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByRestaurantId(@PathVariable("restaurant_id") final String restaurantUuid) throws RestaurantNotFoundException {
//Calls restaurantByUUID method of restaurantService to get the restaurant entity.
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
        List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantUuid);

        //Creating category Lists  for the response
        List<CategoryList> categoryLists = new LinkedList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {  //Looping for each CategoryEntity in categoryEntities

            //Calls getItemsByCategoryAndRestaurant of itemService to get list of itemEntities.
            List<ItemEntity> itemEntities = itemService.getItemsByCategoryAndRestaurant(restaurantUuid, categoryEntity.getUuid());
            //Creating Item List for the CategoryList.
            List<ItemList> itemLists = new LinkedList<>();
            itemEntities.forEach(itemEntity -> {
                ItemList itemList = new ItemList()
                        .id(UUID.fromString(itemEntity.getUuid()))
                        .itemName(itemEntity.getItemName())
                        .price(itemEntity.getPrice())
                        .itemType(ItemList.ItemTypeEnum.valueOf(itemEntity.getType()));

                itemLists.add(itemList);
            });

            //Creating new category list to add listof category list
            CategoryList categoryList = new CategoryList()
                    .itemList(itemLists)
                    .id(UUID.fromString(categoryEntity.getUuid()))
                    .categoryName(categoryEntity.getCategoryName());

            //adding to the categoryLists
            categoryLists.add(categoryList);
        }

        //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                .stateName(restaurantEntity.getAddress().getState().getStateName());

        //Creating the RestaurantDetailsResponseAddress for the RestaurantList
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                .city(restaurantEntity.getAddress().getCity())
                .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNumber())
                .locality(restaurantEntity.getAddress().getLocality())
                .pincode(restaurantEntity.getAddress().getPincode())
                .state(restaurantDetailsResponseAddressState);

        //Creating the RestaurantDetailsResponse by adding the list of categoryList and other details.
        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse()
                .restaurantName(restaurantEntity.getRestaurantName())
                .address(restaurantDetailsResponseAddress)
                .averagePrice(restaurantEntity.getAvgPrice())
                .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .photoURL(restaurantEntity.getPhotoUrl())
                .categories(categoryLists);

        return new ResponseEntity<>(restaurantDetailsResponse, HttpStatus.OK);
    }

    // Update restaurant details
    @PutMapping(path = "/restaurant/{restaurant_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@PathVariable("restaurant_id") final String restaurantId, @RequestHeader("authorization") final String authorization,
                                                                             @RequestParam("customer_rating") final Double customerRating) throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

        //Access the accessToken from the request Header
        final String accessToken = authorization.split("Bearer ")[1];

        //Calls customerService getCustomerMethod to check the validity of the customer.this methods returns the customerEntity.
        customerService.getCustomer(accessToken);

        //Calls restaurantByUUID method of restaurantService to get the restaurant entity.
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

        //Calls updateRestaurantRating and passes restaurantentity found and customer rating and return the updated entity.
        restaurantService.updateRestaurantRating(restaurantEntity, customerRating);

        //Creating RestaurantUpdatedResponse containing the UUID of the updated Restaurant and the success message.
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(restaurantId))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return new ResponseEntity<>(restaurantUpdatedResponse, HttpStatus.OK);
    }

}
