package com.order.process.service.impl;

import com.order.process.dto.UserDto;
import com.order.process.entity.User;
import com.order.process.repository.UserRepository;
import com.order.process.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

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
