package com.example.ssisystem.requests;

import java.util.List;

public class DidRequest {
    private List<String> issuersDid;

    public DidRequest() {
    }

    public DidRequest(List<String> issuersDid) {
        this.issuersDid = issuersDid;
    }

    public List<String> getIssuersDid() {
        return issuersDid;
    }

    public void setIssuersDid(List<String> issuersDid) {
        this.issuersDid = issuersDid;
    }
}
