package com.example.ssisystem.entity;

import java.util.List;

public class ProofUtil {

    private String proofType;
    private String verificationMethod;
    private String jws;
    private String created;
    private List<String> proofPurpose;

    public ProofUtil() {
    }

    public ProofUtil(String proofType, String verificationMethod, String jws, String created, List<String> proofPurpose) {
        this.proofType = proofType;
        this.verificationMethod = verificationMethod;
        this.jws = jws;
        this.created = created;
        this.proofPurpose = proofPurpose;
    }

    public String getProofType() {
        return proofType;
    }

    public List<String> getProofPurpose() {
        return proofPurpose;
    }

    public void setProofPurpose(List<String> proofPurpose) {
        this.proofPurpose = proofPurpose;
    }

    public void setProofType(String proofType) {
        this.proofType = proofType;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public String getJws() {
        return jws;
    }

    public void setJws(String jws) {
        this.jws = jws;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "ProofUtil : {" + "\n" +
                "proofType='" + proofType + '\'' + "\n" +
                ", verificationMethod='" + verificationMethod + '\'' + "\n" +
                ", jws='" + jws + '\'' + "\n" +
                ", created='" + created + '\'' + "\n" +
                ", proofPurpose='" + proofPurpose + '\'' +"\n" +
                '}' + "\n";
    }
}
