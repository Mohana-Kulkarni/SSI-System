package com.example.ssisystem.controller;

import com.example.ssisystem.entity.*;
import com.example.ssisystem.service.issuer.IssuerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
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

    @GetMapping("/type")
    public List<Issuer> getIssuerByType(@RequestParam("type") String type) throws ExecutionException, InterruptedException {
        return issuerService.getIssuerByType(type);
    }

    @GetMapping("/pending")
    public List<UserDetails> getPendingRequests(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return issuerService.getPendingRequestsByIssuer(id);
    }

    @GetMapping("/rejected")
    public List<UserDetails> getRejectedRequests(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return issuerService.getRejectedRequestsByIssuer(id);
    }

    @GetMapping("/issued")
    public List<VerifiableCredentials> getIssuedVCs(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return issuerService.getVCsIssuedByIssuer(id);
    }
    @PostMapping("/login")
    public Issuer getIssuerByLogin(@RequestBody Credentials credentials) throws ExecutionException, InterruptedException {
        return issuerService.getIssuerByLogin(credentials.getEmail(), credentials.getPassword());
    }

    @PostMapping("/request")
    public void getVCRequest(@RequestBody Request request) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, ExecutionException, NoSuchProviderException, InterruptedException {
        issuerService.addPendingRequests(request.getUserId(), request.getIssuerDid());
    }

    @PostMapping("/issueVC")
    public void issueVC(@RequestBody Request request) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        issuerService.issueVC(request.getUserId(), request.getIssuerDid());
    }
    @PostMapping("/")
    public void addIssuer(@RequestBody Issuer issuer) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        issuerService.addIssuer(issuer);
    }

    @PutMapping("/update/did")
    public void updateIssuer(@RequestParam("did") String did, @RequestBody Issuer issuer) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        issuerService.updateIssuer(did, issuer);
    }

    @PutMapping("/reject")
    public void rejectRequest(@RequestBody Request request) throws ExecutionException, InterruptedException {
        issuerService.rejectRequest(request.getUserId(), request.getIssuerDid());
    }
}
