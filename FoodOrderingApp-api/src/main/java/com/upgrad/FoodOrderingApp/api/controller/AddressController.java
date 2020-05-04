package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.AddressList;
import com.upgrad.FoodOrderingApp.api.model.AddressListResponse;
import com.upgrad.FoodOrderingApp.api.model.AddressListState;
import com.upgrad.FoodOrderingApp.api.model.DeleteAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.StatesList;
import com.upgrad.FoodOrderingApp.api.model.StatesListResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;

    // Save address endpoint requests for all the attributes in “SaveAddressRequest” about the address
    // and saves an address successfully.
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader(value = "authorization", required = true) String authorization,
                                                           @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        // Splits the Bearer authorization text as Bearer and bearerToken
        String[] bearerToken = authorization.split("Bearer ");
        customerService.getCustomer(bearerToken[1]);


        // Adds all the address attributes provided to the address entity
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setActive(1);

        // Calls the saveAddress method of address service with the provided attributes
        AddressEntity savedAddressEntity = addressService.saveAddress(addressEntity, bearerToken[1]);

        // Loads the SaveAddressResponse with the uuid of the new address created and the respective status message
        SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(savedAddressEntity.getUuid())
                .status("ADDRESS SUCCESSFULLY REGISTERED");

        // Returns the SaveAddressResponse with resource created http status
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    // Get All Saved Addresses endpoint requests Bearer authorization of the customer
    // and gets the addresses successfully.
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllPermanentAddress(@RequestHeader("authorization") String authorization)
            throws AuthorizationFailedException {
        //Access the accessToken from the request Header
        String accessToken = authorization.split("Bearer ")[1];

        //Calls customerService getCustomer Method to check the validity of the customer.this methods returns the customerEntity  to be get all address.
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        //Calls getAllAddress method of addressService to get list of address
        List<AddressEntity> addressEntities = addressService.getAllAddress(customerEntity);
        Collections.reverse(addressEntities); //Reversing the list to show last saved as first.

        //Creating list of  AddressList using the Model AddressList & this List will be added to AddressListResponse
        List<AddressList> addressLists = new LinkedList<>();
        //Looping in for each address in the addressEntities & then Created AddressList using the each address data and adds to addressLists.
        addressEntities.forEach(addressEntity -> {
            AddressListState addressListState = new AddressListState()
                    .stateName(addressEntity.getState().getStateName())
                    .id(UUID.fromString(addressEntity.getState().getUuid()));
            AddressList addressList = new AddressList()
                    .id(UUID.fromString(addressEntity.getUuid()))
                    .city(addressEntity.getCity())
                    .flatBuildingName(addressEntity.getFlatBuilNumber())
                    .locality(addressEntity.getLocality())
                    .pincode(addressEntity.getPincode())
                    .state(addressListState);
            addressLists.add(addressList);
        });

        //Creating the AddressListResponse by adding the addressLists.
        AddressListResponse addressListResponse = new AddressListResponse().addresses(addressLists);
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    // Delete Saved Address endpoint requests Bearer authorization of the customer and Address UUID as path variable
    // and deletes the address successfully.
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@RequestHeader("authorization") String authorization,
                                                               @PathVariable("address_id") String addressUuid)
            throws AuthorizationFailedException, AddressNotFoundException {

        // Splits the Bearer authorization text as Bearer and bearerToken
        String[] bearerToken = authorization.split("Bearer ");

        CustomerEntity customer = customerService.getCustomer(bearerToken[1]);

        CustomerAuthTokenEntity customerAuthTokenEntity = customerService.getCustomerAuthToken(bearerToken[1]);

        AddressEntity address = addressService.getAddressByUUID(addressUuid, customer);

        addressService.getCustAddressByCustIdAddressId(customerAuthTokenEntity, address);

        // Calls the deleteAddress with addressId and bearerToken as argument
        AddressEntity deletedAddress = addressService.deleteAddress(addressUuid, address);

        // Loads the DeleteAddressResponse with uuid of the address and the respective status message
        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(deletedAddress.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        // Returns the DeleteAddressResponse with OK http status
        return new ResponseEntity<>(deleteAddressResponse, HttpStatus.OK);
    }

    // Get All States endpoint gets all the states successfully.
    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() throws AuthorizationFailedException {

        // Calls the getAllStates from addressService
        List<StateEntity> stateEntityList = addressService.getAllStates();
        StatesListResponse stateListResponse = new StatesListResponse();

        // Loops thru the stateEntityList to load the StatesListResponse
        for (StateEntity se : stateEntityList) {
            StatesList state = new StatesList();
            state.setStateName(se.getStateName());
            state.setId(UUID.fromString(se.getUuid()));
            stateListResponse.addStatesItem(state);
        }

        return new ResponseEntity<StatesListResponse>(stateListResponse, HttpStatus.OK);
    }
}
