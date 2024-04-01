package com.example.ssisystem.entity;

import java.util.List;

public class Verifier {
    private String id;
    private String name;
    private String email;
    private String password;
    private String govId;
    private String walletId;
    private List<String> trustedIssuer;
    private String privateDid;
    private String publicDid;

    public Verifier() {
    }

    public Verifier(String name, String govId, String email, String password) {
        this.name = name;
        this.govId = govId;
        this.email = email;
        this.password = password;
    }

    public Verifier(String id, String name, String email, String govId, List<String> trustedIssuer, String privateDid, String publicDid, String walletId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.govId = govId;
        this.trustedIssuer = trustedIssuer;
        this.privateDid = privateDid;
        this.publicDid = publicDid;
        this.walletId = walletId;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivateDid() {
        return privateDid;
    }

    public void setPrivateDid(String privateDid) {
        this.privateDid = privateDid;
    }

    public String getPublicDid() {
        return publicDid;
    }

    public void setPublicDid(String publicDid) {
        this.publicDid = publicDid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGovId() {
        return govId;
    }

    public void setGovId(String govId) {
        this.govId = govId;
    }

    public List<String> getTrustedIssuer() {
        return trustedIssuer;
    }

    public void setTrustedIssuer(List<String> trustedIssuer) {
        this.trustedIssuer = trustedIssuer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }
}
