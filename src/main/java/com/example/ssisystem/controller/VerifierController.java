package com.example.ssisystem.controller;

import com.example.ssisystem.entity.Credentials;
import com.example.ssisystem.entity.VerificationResult;
import com.example.ssisystem.entity.Verifier;
import com.example.ssisystem.service.verifier.VerifierService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/verifiers")
public class VerifierController {
    @Autowired
    private VerifierService verifierService;

    @GetMapping("/id")
    public Verifier getVerifierById(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return verifierService.getVerifierById(id);
    }
    @GetMapping("/wallet")
    public Verifier getVerifierByWalletId(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return verifierService.getVerifierByWalletId(id);
    }
    @GetMapping("/result")
    public VerificationResult verifyVCs(@RequestParam("id") String id, @RequestParam("vcId") String vcId, @RequestParam("ticketId") String ticketId, @RequestParam("nftId") String nftId) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
        return verifierService.verify_vc(id, vcId, ticketId, nftId);
    }
    @PostMapping("/login")
    public Verifier getVerifierByLogin(@RequestBody Credentials credentials) throws ExecutionException, InterruptedException {
        return verifierService.getVeriferByLogin(credentials.getEmail(), credentials.getPassword());
    }

    @PostMapping("/")
    public void addVerifier(@RequestBody Verifier verifier) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        verifierService.createVerifier(verifier.getName(), verifier.getEmail(), verifier.getPassword(), verifier.getGovId());
    }

    @PostMapping("/issuer")
    public void addTrustedIssuer(@RequestParam("id") String id, @RequestParam("did") String did) throws ExecutionException, InterruptedException {
        verifierService.addTrustedIssuers(id, did);
    }


    @PutMapping("/update")
    public void updateverifier(@RequestParam("id") String id, @RequestBody Verifier verifier) {
        verifierService.updateVerifier(id, verifier);
    }
}
