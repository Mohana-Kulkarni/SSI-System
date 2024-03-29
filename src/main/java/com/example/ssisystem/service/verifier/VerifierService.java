package com.example.ssisystem.service.verifier;

import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.entity.VerificationResult;
import com.example.ssisystem.entity.Verifier;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface VerifierService {
    Map<String, String> createVerifier(String name, String email, String password, String govId, String walletId) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    boolean addTrustedIssuers(String id, String issuerDid) throws ExecutionException, InterruptedException;
    Verifier getVerifierById(String id) throws ExecutionException, InterruptedException;
    Verifier getVeriferByLogin(String email, String password) throws ExecutionException, InterruptedException;
    Verifier getVerifierByWalletId(String walletId) throws ExecutionException, InterruptedException;
    boolean updateVerifier(String id, Verifier verifier);
    VerificationResult verify_vc(String id, String vcId, String ticketId, String nftId) throws ExecutionException, InterruptedException, JsonProcessingException;

}
