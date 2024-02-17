package com.example.ssisystem.service.did;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;

public interface DIDService {
    String generatePrivateDid() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    String generatePublicDid() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    PrivateKey generatePrivateKey() throws NoSuchAlgorithmException;
}
