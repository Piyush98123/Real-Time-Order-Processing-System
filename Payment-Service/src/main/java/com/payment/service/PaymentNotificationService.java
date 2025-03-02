package com.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentNotificationService {

    @Autowired
    EmailService emailService;

    @KafkaListener(topics = "payment-notification", groupId = "payment-notification")
    public void pollOrderStatus(String data) {
        String msg="";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            msg = objectMapper.readValue(data, String.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        emailService.sendMail("taranjeetsingh36958@gmail.com", msg, "Hi, your order failed!!");

    }
}
