package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> restaurantNotFoundException(RestaurantNotFoundException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.CONFLICT
        );
    }


    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> categoryNotFoundException(CategoryNotFoundException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> invalidRatingException(InvalidRatingException ex, WebRequest webReq) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ex.getCode()).message(ex.getErrorMessage()),
                HttpStatus.NOT_ACCEPTABLE
        );
    }


}
