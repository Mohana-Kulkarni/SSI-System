package com.example.ssisystem.controller;

import com.example.ssisystem.service.did.DIDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
@RequestMapping("/did")
public class DIDController {
    @Autowired
    private DIDService didServices;
    @GetMapping("/public")
    public String getPublicDid() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return didServices.generatePublicDid();
    }
}
