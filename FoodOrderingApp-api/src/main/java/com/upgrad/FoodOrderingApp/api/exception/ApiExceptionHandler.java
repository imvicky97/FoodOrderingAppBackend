package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> restaurantNotFoundException(RestaurantNotFoundException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ErrorResponse> paymentNotFoundException(PaymentMethodNotFoundException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.NOT_FOUND
        );
    }


    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException ex, WebRequest webReq) {
        return new ResponseEntity<>(new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> categoryNotFoundException(CategoryNotFoundException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> couponNotFoundException(CouponNotFoundException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException ex, WebRequest webReq) {
        return new ResponseEntity<>(new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> updateCustomerException(UpdateCustomerException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> invalidRatingException(InvalidRatingException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> SaveAddressException(SaveAddressException exc, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> AddressNotFoundException(AddressNotFoundException exc, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND);
    }
}
