package com.example.ssisystem.entity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Issuer implements Serializable {
    String name;
    String email;
    String password;
    String govId;
    private String privateDid;
    private String publicDid;
    private String type;
    private String walletId;

    private List<String> pendingRequests;
    private List<String> issuedVCs;
    private List<String> rejectedRequests;

    public Issuer() {
    }

    public Issuer(String name, String govId, String email, String password, String walletId) {
        this.name = name;
        this.govId = govId;
        this.email = email;
        this.password = password;
        this.walletId = walletId;
    }

    public Issuer(String name, String email, String govId, String type, String walletId, String publicDid, String privateDid, List<String> issuedVCs, List<String> pendingRequests, List<String> rejectedRequests) {
        this.name = name;
        this.email = email;
        this.govId = govId;
        this.type = type;
        this.walletId = walletId;
        this.publicDid = publicDid;
        this.privateDid = privateDid;
        this.issuedVCs = issuedVCs;
        this.pendingRequests = pendingRequests;
        this.rejectedRequests = rejectedRequests;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setIssuedVCs(List<String> issuedVCs) {
        this.issuedVCs = issuedVCs;
    }
    public List<String> getIssuedVCs() {
        return issuedVCs;
    }
    public String getGovId() {
        return govId;
    }

    public void setGovId(String govId) {
        this.govId = govId;
    }
    public List<String> getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(List<String> pending_requests) {
        this.pendingRequests = pending_requests;
    }

    public String  getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(List<String> rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
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

