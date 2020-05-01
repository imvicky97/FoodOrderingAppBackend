package com.upgrad.FoodOrderingApp.service.dao;



import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerAuthEntity getAccessTokenByCustomerId(String customer_id) {
        try {
            return entityManager.createNamedQuery("getCustomerAuthByCustomerId", CustomerAuthEntity.class).setParameter("customer_id", customer_id).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Gets the user authentication info based on the access token.
     *
     * @param accessToken access token of the user whose details has to be fetched.
     * @return A single user auth object or null
     */
    public CustomerAuthEntity getUserAuthByAccessToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthByAccessToken", CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthEntity createAccessToken(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    public void updateUser(final CustomerAuthEntity updatedCustomerAuthEntity) {
        entityManager.merge(updatedCustomerAuthEntity);
    }

}

