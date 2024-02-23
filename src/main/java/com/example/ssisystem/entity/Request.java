package com.example.ssisystem.entity;

public class Request {
    private String userId;
    private String issuerDid;

    public Request() {
    }

    public Request(String userId, String issuerDid) {
        this.userId = userId;
        this.issuerDid = issuerDid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIssuerDid() {
        return issuerDid;
    }

    public void setIssuerDid(String issuerDid) {
        this.issuerDid = issuerDid;
    }
}
