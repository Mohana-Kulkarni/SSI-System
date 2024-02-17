package com.example.ssisystem.entity;

public class VerifiableCredentials {
        private String id;
        private UserDetails details;
        private String issuerDid;
        private String type;
        private String issuanceDate;
        private String expirationDate;
        private String validFrom;
        private ProofUtil proof;

        public VerifiableCredentials(UserDetails details, String issuerDid, String type) {
            this.details = details;
            this.issuerDid = issuerDid;
            this.type = type;
        }

        public VerifiableCredentials(String id, UserDetails details, String issuerDid, String type, String issuanceDate, String expirationDate, String validFrom, ProofUtil proof) {
            this.id = id;
            this.details = details;
            this.issuerDid = issuerDid;
            this.type = type;
            this.issuanceDate = issuanceDate;
            this.expirationDate = expirationDate;
            this.validFrom = validFrom;
            this.proof = proof;
        }

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

        public String getIssuerDid() {
            return issuerDid;
        }

        public void setIssuerDid(String issuerDid) {
            this.issuerDid = issuerDid;
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

        @Override
        public String toString() {
            return "VerifiableCredentials{" +
                    "id='" + id + '\'' +
                    ", details=" + details +
                    ", issuerDid='" + issuerDid + '\'' +
                    ", type='" + type + '\'' +
                    ", issuanceDate='" + issuanceDate + '\'' +
                    ", expirationDate='" + expirationDate + '\'' +
                    ", validFrom='" + validFrom + '\'' +
                    ", proof=" + proof +
                    '}';
        }

}
