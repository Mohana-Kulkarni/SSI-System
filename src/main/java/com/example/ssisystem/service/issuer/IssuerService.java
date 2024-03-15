package com.example.ssisystem.service.issuer;

import com.example.ssisystem.entity.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IssuerService {
    Map<String, String> addIssuer(Issuer issuer) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    Issuer getIssuerById(String id) throws ExecutionException, InterruptedException;
    Issuer getIssuerByLogin(String email, String password) throws ExecutionException, InterruptedException;
    Issuer getIssuerByPublicDid(String did) throws ExecutionException, InterruptedException;
    Issuer getIssuerByWalletId(String walletId) throws ExecutionException, InterruptedException;
    List<Issuer> getIssuerByType(String type) throws ExecutionException, InterruptedException;
    List<UserDetails> getPendingRequestsByIssuer(String issuerId) throws ExecutionException, InterruptedException;
    List<UserDetails> getRejectedRequestsByIssuer(String issuerId) throws ExecutionException, InterruptedException;
    List<VerifiableCredentials> getVCsIssuedByIssuer(String issuerId) throws ExecutionException, InterruptedException;
    boolean addPendingRequests(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    boolean addIssuedVCs(String issuerDid,String vcId) throws ExecutionException, InterruptedException;
    boolean updateIssuer(String did, Issuer issuer) throws ExecutionException, InterruptedException;
    boolean issueVC(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException;

    boolean rejectRequest(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException;

}
