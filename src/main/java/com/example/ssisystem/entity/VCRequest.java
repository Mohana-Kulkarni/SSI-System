package com.example.ssisystem.entity;

import java.util.List;

public class VCRequest {

    private String userDid;
    private List<String> issuers;

    public VCRequest() {
    }

    public VCRequest(String userDid, List<String> issuers) {
        this.userDid = userDid;
        this.issuers = issuers;
    }

    public String getUserDid() {
        return userDid;
    }

    public void setUserDid(String userDid) {
        this.userDid = userDid;
    }

    public List<String> getIssuers() {
        return issuers;
    }

    public void setIssuers(List<String> issuers) {
        this.issuers = issuers;
    }
}
