package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class PaymentDao {

    @Autowired
    private EntityManager entityManager;

    public List<PaymentEntity> getPaymentMethods() {
        return entityManager.createNamedQuery("getAllPaymentMethods", PaymentEntity.class).getResultList();

    }
}
