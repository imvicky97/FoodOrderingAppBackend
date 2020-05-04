package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
public class CustomerDao {

    @Autowired
    private EntityManager entityManager;

    public CustomerEntity createUser(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    /**
     * @param contact_number
     * @return User with the given contact_number
     */
    public CustomerEntity getCustomerByContactNumber(final String contact_number) {
        try {
            return entityManager.createNamedQuery("getCustomerByContactNumber", CustomerEntity.class).setParameter("contact_number", contact_number).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // Gets the customer details from the database based on uuid
    public CustomerEntity getCustomerByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("getCustomerByUUID", CustomerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthTokenEntity getCustomerAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public void updateUser(final CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
    }

    public CustomerEntity getCustomerById(Integer uuid) {
        try {
            return entityManager.createNamedQuery("getCustomerByUUID", CustomerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity getUserByEmail(String username) {
        try {
            return entityManager.createNamedQuery("getCustomerByEmail", CustomerEntity.class).setParameter("email", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // Creates auth token by persisting the record in the database
    public void createAuthToken(final CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
    }

    // Updates the customer details to the database
    public void updateCustomer(final CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
    }

    //
    public void updateCustomerAuth(final CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.merge(customerAuthTokenEntity);
    }

}
