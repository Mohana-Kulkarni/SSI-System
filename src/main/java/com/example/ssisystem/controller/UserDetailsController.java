package com.example.ssisystem.controller;

import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.service.user.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;
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
    public Map<String, String> addUserDetails(@RequestBody UserDetails userDetails) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, ExecutionException, NoSuchProviderException, InterruptedException {
        return userDetailsService.addUserDetails(userDetails);
    }
    @PutMapping("/update/id")
    public void updateUserDetails(@RequestParam("id") String id, @RequestBody UserDetails userDetails) throws ExecutionException, InterruptedException {
        userDetailsService.updateUserDetails(id, userDetails);
    }
}
