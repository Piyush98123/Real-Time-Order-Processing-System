package com.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
        emailService.sendMail("receiver gmail", msg, "Hi, your order failed!!");
        sendSms(msg);
    }

    // Using fast_2_sms service
    private void sendSms(String msg){
        String API_KEY="your api key";
        try {
            String message= URLEncoder.encode("Hi, Your payment for order "+msg+" has failed", "UTF-8");
            String route="q";
            String number="receiver mobile number";
            String url="https://www.fast2sms.com/dev/bulkV2?authorization="+API_KEY+"&message="+message+"&route="+route+"&numbers="+number;
            URL myUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection)myUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent","Mozilla/5.0");
            urlConnection.setRequestProperty("cache-control","no-cache");
            int code = urlConnection.getResponseCode();
            System.out.println("response code: "+code);
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while(true){
                String line = br.readLine();
                if(line==null) break;
                stringBuffer.append(line);
            }
            System.out.println(stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
