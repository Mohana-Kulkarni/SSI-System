package com.example.ssisystem.service.issuer;

import com.example.ssisystem.entity.Issuer;
import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.entity.UserResponse;
import com.example.ssisystem.entity.VerifiableCredentials;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IssuerService {
    void addIssuer(Issuer issuer) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    Issuer getIssuerById(String id) throws ExecutionException, InterruptedException;
    Issuer getIssuerByPublicDid(String did) throws ExecutionException, InterruptedException;
//    void addPendingRequests(String userDetailsId) throws ExecutionException, InterruptedException;
    void updateIssuer(String did, String vcId) throws ExecutionException, InterruptedException;
    VerifiableCredentials issueVC(UserDetails userDetails, String issuerDid, String proofType, List<String> proofPurpose) throws ExecutionException, InterruptedException;
}
