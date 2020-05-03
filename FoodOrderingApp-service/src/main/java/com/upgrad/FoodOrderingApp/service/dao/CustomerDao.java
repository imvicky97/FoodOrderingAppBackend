package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
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

    public void updateUser(final CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
    }

    public CustomerEntity getCustomerById(String uuid) {
        try {
            return entityManager.createNamedQuery("getCustomerByUUID", CustomerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerAuthEntity getCustomerByAccessToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("customerByAccessToken" , CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public CustomerEntity getUserByEmail(String username) {
        try {
            return entityManager.createNamedQuery("getCustomerByEmail" , CustomerEntity.class).setParameter("email" , username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
