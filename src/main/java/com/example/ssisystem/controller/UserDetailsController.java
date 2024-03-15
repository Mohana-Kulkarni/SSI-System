package com.example.ssisystem.controller;

import com.example.ssisystem.constants.GlobalConstants;
import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.exception.response.SuccessResponse;
import com.example.ssisystem.service.user.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserDetails> getUserDetailsById(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDetailsService.getUserById(id));
    }

    @PostMapping("/")
    public ResponseEntity<SuccessResponse> addUserDetails(@RequestBody UserDetails userDetails) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, ExecutionException, NoSuchProviderException, InterruptedException {
        Map<String, String> result =  userDetailsService.addUserDetails(userDetails);
        if(result.get("result").equals("true")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_200, result));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_POST));
    }
    @PutMapping("/update/id")
    public ResponseEntity<SuccessResponse> updateUserDetails(@RequestParam("id") String id, @RequestBody UserDetails userDetails) throws ExecutionException, InterruptedException {
        if(userDetailsService.updateUserDetails(id, userDetails)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_200, GlobalConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_UPDATE));
    }
}
