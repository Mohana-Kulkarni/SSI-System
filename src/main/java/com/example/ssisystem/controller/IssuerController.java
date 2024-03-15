package com.example.ssisystem.controller;

import com.example.ssisystem.constants.GlobalConstants;
import com.example.ssisystem.entity.*;
import com.example.ssisystem.exception.response.SuccessResponse;
import com.example.ssisystem.service.issuer.IssuerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/issuers")
public class IssuerController {

    @Autowired
    private IssuerService issuerService;

    @GetMapping("/id")
    public ResponseEntity<Issuer> getIssuerById(@RequestParam("id") String id) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        return ResponseEntity.status(HttpStatus.OK).body(issuerService.getIssuerById(id));
    }

    @GetMapping("/did")
    public ResponseEntity<Issuer> getIssuerByPublicDId(@RequestParam("did") String did) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        return ResponseEntity.status(HttpStatus.OK).body(issuerService.getIssuerByPublicDid(did));
    }

    @GetMapping("/wallet")
    public ResponseEntity<Issuer> getIssuerByWalletId(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(issuerService.getIssuerByWalletId(id));
    }

    @GetMapping("/type")
    public ResponseEntity<List<Issuer>> getIssuerByType(@RequestParam("type") String type) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(issuerService.getIssuerByType(type));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserDetails>> getPendingRequests(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(issuerService.getPendingRequestsByIssuer(id));
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<UserDetails>> getRejectedRequests(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(issuerService.getRejectedRequestsByIssuer(id));
    }

    @GetMapping("/issued")
    public ResponseEntity<List<VerifiableCredentials>> getIssuedVCs(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(issuerService.getVCsIssuedByIssuer(id));
    }
    @PostMapping("/login")
    public ResponseEntity<Issuer> getIssuerByLogin(@RequestBody Credentials credentials) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(issuerService.getIssuerByLogin(credentials.getEmail(), credentials.getPassword()));
    }

    @PostMapping("/request")
    public ResponseEntity<SuccessResponse> getVCRequest(@RequestBody Request request) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, ExecutionException, NoSuchProviderException, InterruptedException {
        if(issuerService.addPendingRequests(request.getUserId(), request.getIssuerDid())) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_200, GlobalConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_POST));
    }

    @PostMapping("/issueVC")
    public ResponseEntity<SuccessResponse> issueVC(@RequestBody Request request) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        if(issuerService.issueVC(request.getUserId(), request.getIssuerDid())) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_200, GlobalConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_POST));
    }
    @PostMapping("/")
    public ResponseEntity<SuccessResponse> addIssuer(@RequestBody Issuer issuer) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Map<String, String > map = issuerService.addIssuer(issuer);
        if(map.get("result").equals("true")) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_201, map));

        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_POST));
    }

    @PutMapping("/update/did")
    public ResponseEntity<SuccessResponse> updateIssuer(@RequestParam("did") String did, @RequestBody Issuer issuer) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException {
        if(issuerService.updateIssuer(did, issuer)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_200, GlobalConstants.MESSAGE_200));
        }

        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_UPDATE));
    }

    @PutMapping("/reject")
    public ResponseEntity<SuccessResponse> rejectRequest(@RequestBody Request request) throws ExecutionException, InterruptedException {
        if(issuerService.rejectRequest(request.getUserId(), request.getIssuerDid())) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SuccessResponse(GlobalConstants.STATUS_200, GlobalConstants.MESSAGE_200));
        }

        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new SuccessResponse(GlobalConstants.STATUS_417, GlobalConstants.MESSAGE_417_DELETE));
    }
}
