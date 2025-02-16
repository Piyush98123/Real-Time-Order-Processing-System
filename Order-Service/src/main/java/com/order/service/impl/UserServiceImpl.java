package com.order.service.impl;

import com.order.dto.UserDto;
import com.order.entity.User;
import com.order.repository.UserRepository;
import com.order.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public String create(UserDto userDto) {
        log.info("user registration data {}",userDto);
        User user = new User();
        user.setUserEmail(userDto.getUserEmail());
        user.setPan(userDto.getPan());
        user.setUserName(userDto.getUserName());
        user.setMobNumber(userDto.getMobNumber());
        long userId = Instant.now().toEpochMilli() % 1000000;
        user.setUserId(userId);
        user.setDob(userDto.getDob());
        userRepository.save(user);
        // Kafka logic to notify notification microservice
        log.info("user registered: "+user.getUserId());
        return "User registered successfully: "+user.getUserId();

    }


}
