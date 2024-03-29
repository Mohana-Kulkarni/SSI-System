package com.example.ssisystem.entity;

public class VerifiableCredentials {
        private String id;
        private UserDetails details;
        private Issuer issuer;
        private String type;
        private String issuanceDate;
        private String expirationDate;
        private String validFrom;
        private ProofUtil proof;
        private String status;

    public VerifiableCredentials(UserDetails details, Issuer issuer, String type) {
            this.details = details;
            this.issuer = issuer;
            this.type = type;
        }

    public VerifiableCredentials(String id, UserDetails details, Issuer issuer, String type, String issuanceDate, String expirationDate, String validFrom, ProofUtil proof, String status) {
        this.id = id;
        this.details = details;
        this.issuer = issuer;
        this.type = type;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.validFrom = validFrom;
        this.proof = proof;
        this.status = status;
    }

//    public VerifiableCredentials(String id, UserDetails details, String type, String issuanceDate, String expirationDate, String validFrom, ProofUtil proof) {
//        this.id = id;
//        this.details = details;
//        this.type = type;
//        this.issuanceDate = issuanceDate;
//        this.expirationDate = expirationDate;
//        this.validFrom = validFrom;
//        this.proof = proof;
//    }
    public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public UserDetails getDetails() {
            return details;
        }

        public void setDetails(UserDetails details) {
            this.details = details;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIssuanceDate() {
            return issuanceDate;
        }

        public void setIssuanceDate(String issuanceDate) {
            this.issuanceDate = issuanceDate;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
        }

        public String getValidFrom() {
            return validFrom;
        }

        public void setValidFrom(String validFrom) {
            this.validFrom = validFrom;
        }

        public ProofUtil getProof() {
            return proof;
        }

        public void setProof(ProofUtil proof) {
            this.proof = proof;
        }


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    @Override
        public String toString() {
            return "VerifiableCredentials{" +
                    "id='" + id + '\'' +
                    ", details=" + details +
                    ", type='" + type + '\'' +
                    ", issuanceDate='" + issuanceDate + '\'' +
                    ", expirationDate='" + expirationDate + '\'' +
                    ", validFrom='" + validFrom + '\'' +
                    ", proof=" + proof +
                    '}';
        }

}
