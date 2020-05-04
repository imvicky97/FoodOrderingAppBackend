package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private CustomerService customerService;

    @Transactional
    public AddressEntity getAddressById(final Long addressId) {
        return addressDao.getAddressById(addressId);
    }

    @Transactional
    public AddressEntity getAddressByUUID(final String addressUuid, CustomerEntity customerEntity) throws AuthorizationFailedException, AddressNotFoundException {
        return addressDao.getAddressByUuid(addressUuid);
    }


    public CustomerAddressEntity getCustAddressByCustIdAddressId(CustomerAuthTokenEntity customerAuthTokenEntity, AddressEntity addressEntity) {
        return customerAddressDao.getCustAddressByCustIdAddressId(customerAuthTokenEntity.getCustomer(), addressEntity);

    }

    @Transactional
    public StateEntity getStateByUUID(String stateUuid) throws SaveAddressException, AddressNotFoundException {
        StateEntity stateEntity = stateDao.getStateByUuid(stateUuid);
        if (stateUuid.isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        } else if (stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        } else {
            return stateEntity;
        }

    }

    @Transactional
    public StateEntity getStateById(Long stateId) {
        return stateDao.getStateById(stateId);
    }

    @Transactional
    public AddressEntity saveAddress(AddressEntity addressEntity, String bearerToken)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        customerService.validateAccessToken(bearerToken);

        getStateByUUID(addressEntity.getState().getUuid());

        if (addressEntity.getCity() == null || addressEntity.getCity().isEmpty() ||
                addressEntity.getState() == null ||
                addressEntity.getFlatBuilNumber() == null || addressEntity.getFlatBuilNumber().isEmpty() ||
                addressEntity.getLocality() == null || addressEntity.getLocality().isEmpty() ||
                addressEntity.getPincode() == null || addressEntity.getPincode().isEmpty() ||
                addressEntity.getUuid() == null || addressEntity.getUuid().isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        }

        if (stateDao.getStateById(addressEntity.getState().getId()) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id.");
        }

        if (!addressEntity.getPincode().matches("^[1-9][0-9]{5}$")) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        addressEntity = addressDao.createAddress(addressEntity);

        //get the customerAuthToken details from customerDao
        CustomerAuthTokenEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(bearerToken);

        // Save the customer address
        final CustomerEntity customerEntity = customerDao.getCustomerByUuid(customerAuthTokenEntity.getUuid());
        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();

        customerAddressEntity.setAddress(addressEntity);
        customerAddressEntity.setCustomer(customerEntity);
        customerAddressDao.createCustomerAddress(customerAddressEntity);

        return addressEntity;
    }


    @Transactional
    public AddressEntity deleteAddress(String addressUuid, AddressEntity addressEntity)
            throws AuthorizationFailedException, AddressNotFoundException {

        if (addressUuid == null) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty.");
        }

        if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id.");
        }

        return addressDao.deleteAddressByUuid(addressEntity);
    }


    /*This method is to getAllAddress of the customerEntity.This method takes Customer Entity and returns list of AddressEntity.
     */
    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) {

        //Creating List of AddressEntities.
        List<AddressEntity> addressEntities = new LinkedList<>();

        //Calls Method of customerAddressDao,getAllCustomerAddressByCustomer and returns AddressList.
        List<CustomerAddressEntity> customerAddressEntities = customerAddressDao.getAllCustomerAddressByCustomer(customerEntity);
        if (customerAddressEntities != null) { //Checking if CustomerAddressEntity is null else extracting address and adding to the addressEntites list.
            customerAddressEntities.forEach(customerAddressEntity -> {
                addressEntities.add(customerAddressEntity.getAddress());
            });
        }

        return addressEntities;

    }

    public List<StateEntity> getAllStates() {
        return stateDao.getAllStates();
    }

}
