package com.example.ssisystem.entity;

import java.io.Serializable;
import java.util.List;

public class Issuer implements Serializable {
    String name;
    String govId;
    private String privateDid;
    private String publicDid;
    List<String> pending_requests;
    private List<String> issuedVCs;



    public Issuer() {
    }

    public Issuer(String name, String govId) {
        this.name = name;
        this.govId = govId;
    }

    public Issuer(String name, String govId,String publicDid, String privateDid, List<String> issuedVCs, List<String> pending_requests) {
        this.name = name;
        this.govId = govId;
        this.publicDid = publicDid;
        this.privateDid = privateDid;
        this.issuedVCs = issuedVCs;
        this.pending_requests = pending_requests;
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
    public List<String> getPending_requests() {
        return pending_requests;
    }

    public void setPending_requests(List<String> pending_requests) {
        this.pending_requests = pending_requests;
    }


}

