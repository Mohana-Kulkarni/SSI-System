package com.example.ssisystem.controller;

import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.entity.UserResponse;
import com.example.ssisystem.service.user.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/userDetails")
public class UserDetailsController {

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/id")
    public UserDetails getUserDetailsById(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return userDetailsService.getUserById(id);
    }

    @PostMapping("/")
    public void addUserDetails(@RequestBody UserDetails userDetails) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, ExecutionException, NoSuchProviderException, InterruptedException {
        userDetailsService.addUserDetails(userDetails);
    }
}
