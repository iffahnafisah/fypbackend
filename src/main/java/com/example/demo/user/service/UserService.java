package com.example.demo.user.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.user.entities.User;
import com.example.demo.user.model.UserRequest;
import com.example.demo.user.model.UserResponse;
import com.example.demo.user.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public UserResponse saveUser(UserRequest request) {
    	
    	User user = new User();
    	
    	user.setUsername(request.getUsername());
    	user.setPassword(request.getPassword());
    	user.setEmail(request.getEmail());
    	
    	User savedUser = userRepository.save(user);
    	
    	UserResponse response = new UserResponse();
    	BeanUtils.copyProperties(savedUser, response);
    	
    	return response;
    }
	
}
