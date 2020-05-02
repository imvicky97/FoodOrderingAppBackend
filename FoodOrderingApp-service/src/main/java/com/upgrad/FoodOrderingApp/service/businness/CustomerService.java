package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


@Transactional
@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;



    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity  logout(String access_token) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getAccessToken(access_token);


        if(customerAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in");

        if( customerAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint");

        }
        final ZonedDateTime now = ZonedDateTime.now();

        if(customerAuthEntity != null && customerAuthEntity.getExpiresAt().compareTo(now) < 0 ) {
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        }

         customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        customerAuthDao.updateCustomerAuth(customerAuthEntity);


        return  customerAuthEntity;

    }

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

        String[] encrptedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encrptedPassword[0]);
        customerEntity.setPassword(encrptedPassword[1]);

        CustomerEntity exsitingContactNumber = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());
        if(exsitingContactNumber != null) {
            throw new SignUpRestrictedException("SGR-001","This contact number is already registered! Try other contact number.");
        }
        return customerDao.createUser(customerEntity);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contact_number, final String password) throws AuthenticationFailedException {
        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contact_number);


        if (customerEntity == null) throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setCustomer(customerEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            customerAuthEntity.setUuid(customerEntity.getUuid());
            customerAuthEntity.setLoginAt(now);
            customerAuthEntity.setExpiresAt(expiresAt);

            customerAuthDao.createAccessToken(customerAuthEntity);
            customerAuthDao.updateUser(customerAuthEntity);
            return customerAuthEntity;
        }else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }


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


//    public Object logout(String s) {
//    }
}
