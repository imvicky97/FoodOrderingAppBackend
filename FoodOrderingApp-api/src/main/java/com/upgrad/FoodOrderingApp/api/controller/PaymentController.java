package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Get all payment methods
    @GetMapping(path = "/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> payment() {
        PaymentListResponse paymentListResponse = new PaymentListResponse();
        List<PaymentEntity> paymentEntityList = paymentService.getAllPaymentMethods();

        if (!paymentEntityList.isEmpty()) {
            for (PaymentEntity paymentEntity : paymentEntityList) {
                PaymentResponse paymentResponse = new PaymentResponse();
                paymentResponse.setId(UUID.fromString(paymentEntity.getUuid()));
                paymentResponse.setPaymentName(paymentEntity.getPaymentName());
                paymentListResponse.addPaymentMethodsItem(paymentResponse);
            }
            return new ResponseEntity<>(paymentListResponse, HttpStatus.OK);
        } else {
            paymentListResponse.setPaymentMethods(Collections.emptyList());
            return new ResponseEntity<>(paymentListResponse, HttpStatus.OK);

        }
    }
}
