package com.example.ssisystem.controller;

import com.example.ssisystem.constants.GlobalConstants;
import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.requests.VCRequest;
import com.example.ssisystem.entity.VerifiableCredentials;
import com.example.ssisystem.exception.response.SuccessResponse;
import com.example.ssisystem.service.user.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
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

    @GetMapping("/VCS")
    public ResponseEntity<List<VerifiableCredentials>> getVCsByUserDid(@RequestParam("userDid") String userDid) throws ExecutionException, InterruptedException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDetailsService.getAllVCsByUserID(userDid));
    }

    @PostMapping("/availableVCs")
    public ResponseEntity<List<VerifiableCredentials>> getAvailableVCs(@RequestBody VCRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDetailsService.getVCsByUserIdAndIssuers(request.getIssuers(), request.getUserDid()));
    }

    @PostMapping("/")
    public ResponseEntity<SuccessResponse> addUserDetails(@RequestParam("userId") String userId, @RequestBody UserDetails userDetails) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, ExecutionException, NoSuchProviderException, InterruptedException {
        Map<String, String> result =  userDetailsService.addUserDetails(userId, userDetails);
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
