package com.example.ssisystem.service.user;

import com.example.ssisystem.entity.UserDetails;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

public interface UserDetailsService {
    String addUserDetails(UserDetails userDetails) throws ExecutionException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    UserDetails getUserById(String id) throws ExecutionException, InterruptedException;
}
