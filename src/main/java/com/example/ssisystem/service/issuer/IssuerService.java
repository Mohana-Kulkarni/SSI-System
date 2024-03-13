package com.example.ssisystem.service.issuer;

import com.example.ssisystem.entity.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IssuerService {
    void addIssuer(Issuer issuer) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    Issuer getIssuerById(String id) throws ExecutionException, InterruptedException;
    Issuer getIssuerByLogin(String email, String password) throws ExecutionException, InterruptedException;
    Issuer getIssuerByPublicDid(String did) throws ExecutionException, InterruptedException;
    List<Issuer> getIssuerByType(String type) throws ExecutionException, InterruptedException;
    List<UserDetails> getPendingRequestsByIssuer(String issuerId) throws ExecutionException, InterruptedException;
    List<UserDetails> getRejectedRequestsByIssuer(String issuerId) throws ExecutionException, InterruptedException;
    List<VerifiableCredentials> getVCsIssuedByIssuer(String issuerId) throws ExecutionException, InterruptedException;
    void addPendingRequests(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    void addIssuedVCs(String issuerDid,String vcId) throws ExecutionException, InterruptedException;
    void updateIssuer(String did, Issuer issuer) throws ExecutionException, InterruptedException;
    void issueVC(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException;

    void rejectRequest(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException;

}
