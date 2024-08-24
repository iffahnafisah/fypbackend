package com.example.demo.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.user.model.UserRequest;
import com.example.demo.user.model.UserResponse;
import com.example.demo.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
	
    @PostMapping("/create")
    public UserResponse createUser(@RequestBody UserRequest request) {
    	return userService.saveUser(request);
    }
	
}
