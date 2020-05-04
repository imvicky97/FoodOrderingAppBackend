package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "restaurant")
@NamedQueries(
        {
                @NamedQuery(name = "getAllRestaurants", query = "select r from RestaurantEntity r"),
                @NamedQuery(name = "getRestaurantByName", query = "select r from RestaurantEntity r where lower(r.restaurantName) LIKE lower(:restaurantName)"),
                @NamedQuery(name = "getRestaurantByUuid", query = "select r from RestaurantEntity r where r.uuid =:uuid"),
                @NamedQuery(name = "restaurantsByRating", query = "SELECT r FROM RestaurantEntity r ORDER BY r.customerRating DESC"),
        }
)
public class RestaurantEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "uuid")
    private String uuid;

    @Column(name = "restaurant_name")
    @NotNull
    private String restaurantName;

    @Column(name = "photo_url")
    @NotNull
    private String photoURL;

    @Column(name = "customer_rating")
    @NotNull
    private Double customerRating;

    @Column(name = "average_price_for_two")
    @NotNull
    private Integer averagePrice;

    @Column(name = "number_of_customers_rated")
    @NotNull
    private Integer numberCustomersRated;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id")
    @NotNull
    private AddressEntity address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhotoUrl() {
        return photoURL;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoURL = photoUrl;
    }

    public Double getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(double customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getAvgPrice() {
        return averagePrice;
    }

    public void setAvgPrice(Integer avgPrice) {
        this.averagePrice = avgPrice;
    }

    public Integer getNumberCustomersRated() {
        return numberCustomersRated;
    }

    public void setNumberCustomersRated(Integer numberCustomersRated) {
        this.numberCustomersRated = numberCustomersRated;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }
}
