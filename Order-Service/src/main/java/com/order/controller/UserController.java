package com.order.controller;

import com.order.dto.UserDto;
import com.order.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user/")
public class UserController {


    @Autowired
    UserService userService;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.create(userDto));
    }

}
