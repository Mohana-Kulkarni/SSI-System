package com.example.ssisystem.service.issuer;

import com.example.ssisystem.entity.*;
import com.example.ssisystem.service.did.DIDService;
import com.example.ssisystem.service.user.UserDetailsService;
import com.example.ssisystem.service.user.UserDetailsServiceImpl;
import com.example.ssisystem.service.vc.VCService;
import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.faunadb.client.query.Language.*;
import static com.faunadb.client.query.Language.Value;

@Service
public class IssuerServiceImpl implements IssuerService{

    private FaunaClient faunaClient;
    private DIDService didServices;
    private UserDetailsService userDetailsService;
    private VCService vcService;

    public IssuerServiceImpl(FaunaClient faunaClient, DIDService didServices,VCService vcService, UserDetailsService userDetailsService) {
        this.faunaClient= faunaClient;
        this.didServices = didServices;
        this.vcService = vcService;
        this.userDetailsService = userDetailsService;
    }
    @Override
    public void addIssuer(Issuer issuer) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        String name = issuer.getName();
        String govId = issuer.getGovId();
        Map<String, Object> map = new HashMap<>();
        map .put("name", name);
        map.put("govId", govId);
        map.put("type", issuer.getType());
        map.put("privateDid", didServices.generatePrivateDid());
        map.put("publicDid", didServices.generatePublicDid());
        map.put("pendingRequests", new ArrayList<>());
        map.put("issuedVCs", new ArrayList<>());
        map.put("rejectedRequests", new ArrayList<>());
        faunaClient.query(
                Create(
                        Collection("Issuer"),
                        Obj(
                                "data", Value(map)
                        )
                )
        );
    }

    @Override
    public Issuer getIssuerById(String id) throws ExecutionException, InterruptedException {
        Value res = faunaClient.query(Get(Ref(Collection("Issuer"), id))).get();

        return new Issuer(res.at("data", "name").to(String.class).get(),
                res.at("data", "govId").to(String.class).get(),
                res.at("data", "type").to(String.class).get(),
                res.at("data", "publicDid").to(String.class).get(),
                res.at("data", "privateDid").to(String.class).get(),
                res.at("data", "issuedVCs").collect(String.class).stream().toList(),
                res.at("data", "pendingRequests").collect(String.class).stream().toList());
    }

    @Override
    public Issuer getIssuerByPublicDid(String did) throws ExecutionException, InterruptedException {
        Value res = faunaClient.query(Get(Match(Index("issuer_by_publicDid"), Value(did)))).get();
        return new Issuer(res.at("data", "name").to(String.class).get(),
                res.at("data", "govId").to(String.class).get(),
                res.at("data", "type").to(String.class).get(),
                res.at("data", "publicDid").to(String.class).get(),
                res.at("data", "privateDid").to(String.class).get(),
                res.at("data", "issuedVCs").collect(String.class).stream().toList(),
                res.at("data", "pendingRequests").collect(String.class).stream().toList());
    }


    @Override
    public void addPendingRequests(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Issuer issuer = getIssuerByPublicDid(issuerDid);
        List<String> pendingRequests = new ArrayList<>();
        for (String id: issuer.getPendingRequests()) {
            pendingRequests.add(id);
        }
        pendingRequests.add(userDetailsId);
        issuer.setPendingRequests(pendingRequests);

        updateIssuer(issuerDid, issuer);

    }

    @Override
    public void addIssuedVCs(String issuerDid, String vcId) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerByPublicDid(issuerDid);
        List<String> issuedVCs = new ArrayList<>();
        issuedVCs.addAll(issuer.getPendingRequests());
        issuedVCs.add(vcId);

        updateIssuer(issuerDid, issuer);
    }
    @Override
    public void updateIssuer(String did, Issuer issuer) throws ExecutionException, InterruptedException {
        String issuerId = getIssuerIdByDid(did);

        String name = issuer.getName();
        String govId = issuer.getGovId();
        Map<String, Object> map = new HashMap<>();
        map .put("name", name);
        map.put("govId", govId);
        map.put("type", issuer.getType());
        map.put("privateDid", issuer.getPrivateDid());
        map.put("publicDid", issuer.getPublicDid());
        map.put("pendingRequests", issuer.getPendingRequests());
        map.put("issuedVCs", issuer.getIssuedVCs());
        faunaClient.query(Update(
                Ref(Collection("Issuer"), issuerId),
                Obj(
                        "data", Value(map)
                )
        ));
    }

    @Override
    public void issueVC(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerByPublicDid(issuerDid);
        UserDetails userDetails = userDetailsService.getUserById(userDetailsId);
        VerificationResult result = verifyPolicy(userDetails, issuer.getType());
        String vcId;
        boolean res;
        if (result.getResult().equals("fail")) {
            vcId = "";
            res = false;
            userDetails.setVerificationResult(result);
        } else {
            VerifiableCredentials vc = vcService.issueCredentials(userDetails,issuer, issuer.getType(), issuer.getPrivateDid());
            vcId = vc.getId();
            res = true;
        }
        userDetails.setVerificationResult(result);
        userDetailsService.updateUserDetails(userDetailsId, userDetails);
        removePendingRequest(issuerDid, userDetailsId, vcId, res);
        System.out.println("VC generated successfully!!");
    }


    private String getIssuerIdByDid(String did) throws ExecutionException, InterruptedException {
        Value value = faunaClient.query(Get(Match(Index("issuer_by_publicDid"), Value(did)))).get();
        return value.at("ref").to(Value.RefV.class).get().getId();
    }



    private void removePendingRequest(String did, String userDetailsId, String vcId, boolean res) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerByPublicDid(did);
        List<String> pendingRequests = new ArrayList<>();
        pendingRequests.addAll(issuer.getPendingRequests());
        pendingRequests.remove(userDetailsId);
        issuer.setPendingRequests(pendingRequests);
        if(res) {
            List<String> issuedVCs = new ArrayList<>();
            issuedVCs.addAll(issuer.getIssuedVCs());
            issuedVCs.add(vcId);
            issuer.setIssuedVCs(issuedVCs);
        }
        updateIssuer(did, issuer);
    }

    private boolean checkAgeLimit(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate dateOfBirth = LocalDate.parse(dob, formatter);

        int year = dateOfBirth.getYear();
        if(LocalDate.now().getYear() - year >= 18 ) {
            return true;
        }
        return false;
    }

    private boolean checkStudentId(String proofType) {
        if (proofType.equals("StudentID")){
            return true;
        }
        return false;
    }

    private VerificationResult verifyPolicy(UserDetails userDetails, String policyType) {
        VerificationResult result = new VerificationResult();
        Map<String, String> map = new HashMap<>();
        if(userDetails.equals(null) || userDetails.getAddress().isEmpty() || userDetails.getUserDid().isEmpty() || userDetails.getDateOfBirth().isEmpty() || userDetails.getDocType().isEmpty() || userDetails.getFirstName().isEmpty() || userDetails.getLastName().isEmpty() || userDetails.getGender().isEmpty() || userDetails.getPlaceOfBirth().isEmpty() || userDetails.getProofId().isEmpty()) {
            result.setResult("fail");
            map.put("JsonSchemaPolicy", "failed");
        }
        if(policyType.equals("AgeVerification")) {
            if(!checkAgeLimit(userDetails.getDateOfBirth())) {
                System.out.println("Age verification failed!!!");
                result.setResult("fail");
                map.put("AgeVerification", "failed");
                result.setPolicy(map);
            } else {
                result.setResult("pass");
                map.put("AgeVerification", "passed");
                result.setPolicy(map);
            }
        } else if (policyType.equals("StudentVerification")) {
            if(!checkStudentId(userDetails.getDocType())) {
                System.out.println("Student verification failed!!!");
                result.setResult("fail");
                map.put("StudentVerification", "failed");
                result.setPolicy(map);
            } else {
                result.setResult("pass");
                map.put("StudentVerification", "passed");
                result.setPolicy(map);
            }
        } else if (policyType.equals("Both")) {
            if(!checkAgeLimit(userDetails.getDateOfBirth()) && !checkStudentId(userDetails.getDocType())) {
                System.out.println("Both policy verification failed!!!");
                result.setResult("fail");
                map.put("AgeVerification", "failed");
                map.put("StudentVerification", "failed");
                result.setPolicy(map);
            } else if (!checkAgeLimit(userDetails.getDateOfBirth())) {
                System.out.println("Age verification failed!!!");
                result.setResult("fail");
                map.put("AgeVerification", "failed");
                map.put("StudentVerification", "passed");
                result.setPolicy(map);
            } else if(!checkStudentId(userDetails.getDocType())) {
                System.out.println("Student verification failed!!!");
                result.setResult("fail");
                map.put("AgeVerification", "passed");
                map.put("StudentVerification", "failed");
                result.setPolicy(map);
            } else {
                result.setResult("pass");
                map.put("AgeVerification", "passed");
                map.put("StudentVerification", "passed");
                result.setPolicy(map);
            }
        }
        return result;
    }
}
