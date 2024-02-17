package com.example.ssisystem.controller;

import com.example.ssisystem.entity.Issuer;
import com.example.ssisystem.entity.UserResponse;
import com.example.ssisystem.entity.VerifiableCredentials;
import com.example.ssisystem.service.issuer.IssuerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/issuers")
public class IssuerController {

    @Autowired
    private IssuerService issuerService;

    @GetMapping("/id")
    public Issuer getIssuerById(@RequestParam("id") String id) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        return issuerService.getIssuerById(id);
    }

    @GetMapping("/did")
    public Issuer getIssuerByPublicDId(@RequestParam("did") String did) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        return issuerService.getIssuerByPublicDid(did);
    }

    @PostMapping("/issueVC")
    public VerifiableCredentials issueVC(@RequestBody UserResponse userResponse) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        return issuerService.issueVC(userResponse.getUserDetails(), userResponse.getIssuerDid(), userResponse.getType(), userResponse.getPolicies());
    }
    @PostMapping("/")
    public void addIssuer(@RequestBody Issuer issuer) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        issuerService.addIssuer(issuer);
    }

    @PutMapping("/update/did")
    public void updateIssuer(@RequestParam("did") String did, @RequestParam("vcId") String vcId) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        issuerService.updateIssuer(did, vcId);
    }
}
