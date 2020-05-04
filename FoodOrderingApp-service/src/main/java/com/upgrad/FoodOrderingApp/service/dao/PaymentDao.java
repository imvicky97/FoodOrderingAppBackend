package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class PaymentDao {

    @Autowired
    private EntityManager entityManager;

    //To get all payment methods
    public List<PaymentEntity> getPaymentMethods() {
        return entityManager.createNamedQuery("getAllPaymentMethods", PaymentEntity.class).getResultList();

    }

    //To get Payment By UUID from the db
    public PaymentEntity getPaymentByUUID(String paymentId) {
        try {
            return entityManager.createNamedQuery("getPaymentByUUID", PaymentEntity.class).setParameter("uuid", paymentId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
