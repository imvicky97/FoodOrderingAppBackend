package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.plugin2.message.GetAuthenticationReplyMessage;

import java.util.Base64;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.POST, path= "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> SignUp( @RequestBody final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
//        customerEntity.setSalt("1234abc");


        final CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse registered = new SignupCustomerResponse().id(createdCustomerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(registered, HttpStatus.CREATED);


    }

    @RequestMapping(method = RequestMethod.POST, path = "/customer/login",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader final String authorization) throws AuthenticationFailedException {
        String[] decodeArray = new String[2]; 
        try {
            byte[] decode = Base64.getDecoder().decode(authorization.split(" ")[1]);
            String decodeText = new String(decode);
           decodeArray = decodeText.split(":");
        } catch (Exception ex)
        {
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }


        if(decodeArray.length < 2)
        {
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }

        CustomerAuthEntity customerAuthEntity = customerService.authenticate(decodeArray[0],decodeArray[1]);

        CustomerEntity customer = customerAuthEntity.getCustomer();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(customer.getUuid());
        loginResponse.setFirstName(customer.getFirstName());
        loginResponse.setLastName(customer.getLastName());
        loginResponse.setEmailAddress(customer.getEmail());
        loginResponse.setContactNumber(customer.getContactNumber());
        loginResponse.setMessage("LOGGED IN SUCCESSFULLY");


        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token",customerAuthEntity.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse,headers, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, path =  "/customer/logout", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String access_token) throws AuthorizationFailedException {
        String[] accessToken = access_token.split("Bearer ");
        String userAccessToken=null;
        if(accessToken.length < 2)
            userAccessToken=accessToken[0];
        else
            userAccessToken=accessToken[1];

        CustomerAuthEntity customerAuthEntity = customerService.logout(userAccessToken);
        if(customerAuthEntity==null)
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in");
        LogoutResponse CustomerLogoutResponse = new LogoutResponse();

        CustomerLogoutResponse.id(customerAuthEntity.getCustomer().getUuid().toString())
         .message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(CustomerLogoutResponse,HttpStatus.OK);

    }


    @PutMapping(path = "/customer",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<UpdateCustomerResponse> Update( @RequestHeader ("authorization") final String access_token,  @RequestBody final  UpdateCustomerRequest updateCustomerRequest ) throws  UpdateCustomerException,AuthorizationFailedException {
        String[] accessToken = access_token.split("Bearer ");
        String userAccessToken=null;
        if(accessToken.length < 2)
            userAccessToken=accessToken[0];
        else
            userAccessToken=accessToken[1];

        if(updateCustomerRequest.getFirstName().isEmpty())
        {
            throw new UpdateCustomerException("UCR-002","First Name field should not be empty.");
        }
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());
        CustomerEntity updatedCustomerEntity =  customerService.updateCustomer(customerEntity);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        updateCustomerResponse.setId(updatedCustomerEntity.getUuid());
        updateCustomerResponse.setFirstName(updatedCustomerEntity.getFirstName());
        updateCustomerResponse.setLastName(updatedCustomerEntity.getLastName());
        updateCustomerResponse.setStatus("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

       return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse,HttpStatus.OK);

    }


        @PutMapping(path = "/customer/password", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<UpdatePasswordResponse> ChangePassword (@RequestHeader("authorization") final String access_token ,@RequestBody final UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException, UpdateCustomerException {
            String[] accessToken = access_token.split("Bearer ");
            String userAccessToken=null;
            if(accessToken.length < 2)
                userAccessToken=accessToken[0];
            else
                userAccessToken=accessToken[1];

            if(updatePasswordRequest.getOldPassword().isEmpty() || updatePasswordRequest.getNewPassword().isEmpty()){
                throw new UpdateCustomerException("UCR-003","No field should be empty");
            }

            CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);
            String oldPwd=updatePasswordRequest.getOldPassword();
            String newPwd=updatePasswordRequest.getNewPassword();
            CustomerEntity updateCustomerPassword = customerService.updateCustomerPassword(oldPwd,newPwd,customerEntity);
            UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse();
            updatePasswordResponse.setId(updateCustomerPassword.getUuid());
            updatePasswordResponse.setStatus("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

            return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse,HttpStatus.OK);

        }
}
