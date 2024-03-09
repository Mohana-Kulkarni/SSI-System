package com.example.ssisystem.service.user;

import com.example.ssisystem.entity.UserDetails;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface UserDetailsService {
    Map<String, String> addUserDetails(UserDetails userDetails) throws ExecutionException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    UserDetails getUserById(String id) throws ExecutionException, InterruptedException;
    void updateUserDetails(String id, UserDetails userDetails) throws ExecutionException, InterruptedException;
}
