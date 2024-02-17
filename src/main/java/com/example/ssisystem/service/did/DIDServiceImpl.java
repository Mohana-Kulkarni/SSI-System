package com.example.ssisystem.service.did;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;

import java.security.*;

@Service
public class DIDServiceImpl implements DIDService{
    @Override
    public String generatePrivateDid() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        String  privateKey = Keys.createEcKeyPair().getPrivateKey().toString();
        String address = Keys.getAddress(privateKey);

        String privateDid = String.format("did:%s:%s", "ethr", address.toLowerCase());

        return privateDid;
    }

    @Override
    public String generatePublicDid() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        String publicKey = Keys.createEcKeyPair().getPublicKey().toString();
        String address = Keys.getAddress(publicKey);
        String publicDid = String.format("did:%s:%s", "ethr", address.toLowerCase());

        return publicDid;
    }

    @Override
    public PrivateKey generatePrivateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair.getPrivate();
    }
}
