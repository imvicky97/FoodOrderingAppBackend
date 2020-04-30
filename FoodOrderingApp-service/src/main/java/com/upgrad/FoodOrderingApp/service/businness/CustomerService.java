package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Transactional
@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if(!isCustomerEntittyValid(customerEntity)){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if(!isEmailAddressValid(customerEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002","Invalid email-id format!");

        }
        if(!isContactNumberValid(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003","Invalid contact number!");
        }

        if(!isPasswordString(customerEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004","Weak password!");
        }

        CustomerEntity exsitingContactNumber = customerDao.getUserByContactNumber(customerEntity.getContactNumber());
        if(exsitingContactNumber != null) {
            throw new SignUpRestrictedException("SGR-001","This contact number is already registered! Try other contact number.");
        }
        return customerDao.createUser(customerEntity);
    }
    private boolean isCustomerEntittyValid(CustomerEntity customerEntity) {
        if(customerEntity.getFirstName()!=null &&
                customerEntity.getEmail()!= null &&
                customerEntity.getContactNumber()!=null &&
                customerEntity.getPassword()!= null)
            return true;
        return false;

    }

    private boolean isEmailAddressValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private boolean isContactNumberValid(String phoneNumber) {
        return (phoneNumber.matches("\\d{10}"));
    }

    private boolean isPasswordString(String password) {
        return (password.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"));
    }
}
