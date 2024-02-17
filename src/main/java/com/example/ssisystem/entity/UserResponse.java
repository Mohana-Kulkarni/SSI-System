package com.example.ssisystem.entity;

import java.util.List;

public class UserResponse {
    private UserDetails userDetails;
    private String type;
    private List<String> policies;
    private String issuerDid;

    public UserResponse() {
    }

    public UserResponse(UserDetails userDetails, String type, List<String> policies, String issuerDid) {
        this.userDetails = userDetails;
        this.type = type;
        this.policies = policies;
        this.issuerDid = issuerDid;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPolicies() {
        return policies;
    }

    public void setPolicies(List<String> policies) {
        this.policies = policies;
    }

    public String getIssuerDid() {
        return issuerDid;
    }

    public void setIssuerDid(String issuerDid) {
        this.issuerDid = issuerDid;
    }
}
