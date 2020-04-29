package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    public CustomerEntity saveCustomer(CustomerEntity customerEntity) {
        return customerDao.createUser(customerEntity);
    }
}
