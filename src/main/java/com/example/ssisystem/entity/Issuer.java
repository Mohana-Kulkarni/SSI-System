package com.example.ssisystem.entity;

import java.io.Serializable;
import java.util.List;

public class Issuer implements Serializable {
    String name;
    String govId;
    private String privateDid;
    private String publicDid;
    private List<String> issuedVCs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Issuer() {
    }

    public Issuer(String name, String govId) {
        this.name = name;
        this.govId = govId;
    }

    public Issuer(String name, String govId, List<String> issuedVCs, String publicDid, String privateDid) {
        this.name = name;
        this.govId = govId;
        this.issuedVCs = issuedVCs;
        this.publicDid = publicDid;
        this.privateDid = privateDid;
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


}

