package com.example.ssisystem.controller;

import com.example.ssisystem.constants.GlobalConstants;
import com.example.ssisystem.requests.Credentials;
import com.example.ssisystem.entity.VerificationResult;
import com.example.ssisystem.entity.Verifier;
import com.example.ssisystem.exception.response.SuccessResponse;
import com.example.ssisystem.service.verifier.VerifierService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/verifiers")
public class VerifierController {
    @Autowired
    private VerifierService verifierService;

    @GetMapping("/id")
    public ResponseEntity<Verifier> getVerifierById(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(verifierService.getVerifierById(id));
    }
    @GetMapping("/wallet")
    public ResponseEntity<Verifier> getVerifierByWalletId(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(verifierService.getVerifierByWalletId(id));
    }
    @GetMapping("/result")
    public ResponseEntity<VerificationResult> verifyVCs(@RequestParam("id") String id, @RequestParam("vcId") String vcId, @RequestParam("ticketId") String ticketId, @RequestParam("nftId") String nftId) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(verifierService.verify_vc(id, vcId, ticketId, nftId));
    }
    @PostMapping("/login")
    public ResponseEntity<Verifier> getVerifierByLogin(@RequestBody Credentials credentials) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(verifierService.getVeriferByLogin(credentials.getEmail(), credentials.getPassword()));
    }

    @PostMapping("/")
    public ResponseEntity<SuccessResponse> addVerifier(@RequestBody Verifier verifier) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Map<String, String> map = verifierService.createVerifier(verifier.getName(), verifier.getEmail(), verifier.getPassword(), verifier.getGovId(), verifier.getWalletId());
        if(map.get("result").equals("true")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_201, GlobalConstants.MESSAGE_201_Verifier));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_POST));
    }

    @PostMapping("/issuer")
    public ResponseEntity<SuccessResponse> addTrustedIssuer(@RequestParam("id") String id, @RequestParam("did") String did) throws ExecutionException, InterruptedException {
        if(verifierService.addTrustedIssuers(id, did)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_200, GlobalConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_POST));
    }


    @PutMapping("/update")
    public ResponseEntity<SuccessResponse> updateverifier(@RequestParam("id") String id, @RequestBody Verifier verifier) {
        if(verifierService.updateVerifier(id, verifier)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_200, GlobalConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_UPDATE));

    }
}
