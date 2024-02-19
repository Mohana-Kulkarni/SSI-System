package com.example.ssisystem.entity;

public class UserDetails  {
    private String userDid;
    private String firstName;
    private String lastName;
    private String address;
    private String dateOfBirth;
    private String gender;
    private String placeOfBirth;
    private String proofId;
    private String docType;

    public UserDetails() {
    }



    public UserDetails(String firstName, String lastName, String address, String dateOfBirth, String gender, String placeOfBirth, String proofId, String docType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.placeOfBirth = placeOfBirth;
        this.proofId = proofId;
        this.docType = docType;
    }

    public UserDetails(String userDid, String firstName, String lastName, String address, String dateOfBirth, String gender, String placeOfBirth, String proofId, String docType) {
        this.userDid = userDid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.placeOfBirth = placeOfBirth;
        this.proofId = proofId;
        this.docType = docType;
    }

    public String getUserDid() {
        return userDid;
    }

    public void setUserDid(String userDid) {
        this.userDid = userDid;

    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getProofId() {
        return proofId;
    }

    public void setProofId(String proofId) {
        this.proofId = proofId;
    }

    @Override
    public String toString() {
        return "UserDetails : {" + "\n" +
                "userDid='" + userDid + '\'' + "\n" +
                ", firstName='" + firstName + '\'' + "\n" +
                ", lastName='" + lastName + '\'' + "\n" +
                ", address='" + address + '\'' +"\n" +
                ", dateOfBirth='" + dateOfBirth + '\'' + "\n" +
                ", gender='" + gender + '\'' + "\n" +
                ", placeOfBirth='" + placeOfBirth + '\'' + "\n" +
                ", proofId='" + proofId + '\'' + "\n" +
                ", docType='" + docType + '\'' + "\n" +
                '}' +"\n" ;
    }
}
