package com.example.ssisystem.service.verifier;

import com.example.ssisystem.entity.*;
import com.example.ssisystem.service.did.DIDService;
import com.example.ssisystem.service.issuer.IssuerService;
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
public class VerifierServiceImpl implements VerifierService{
    private FaunaClient faunaClient;
    private DIDService didServices;
    private IssuerService issuerService;
    private VCService vcService;

    public VerifierServiceImpl(FaunaClient faunaClient, DIDService didServices,IssuerService issuerService, VCService vcService) {
        this.faunaClient = faunaClient;
        this.didServices = didServices;
        this.issuerService = issuerService;
        this.vcService = vcService;
    }
    @Override
    public void createVerifier(String name, String govId) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        faunaClient.query(
                Create(
                        Collection("Verifier"),
                        Obj(
                                "data",
                                Obj(
                                        "name", Value(name),
                                        "govId", Value(govId),
                                        "trustedIssuer", Value(new ArrayList<>()),
                                        "privateDid", Value(didServices.generatePrivateDid()),
                                        "publicDid", Value(didServices.generatePublicDid())
                                )
                        )
                )
        );
    }

    @Override
    public void addTrustedIssuers(String id, String issuerDid) throws ExecutionException, InterruptedException {
        Verifier verifier = getVerifierById(id);
        List<String > trustedIssuer;
        if(verifier.getTrustedIssuer().isEmpty()) {
            trustedIssuer = new ArrayList<>();
        } else {
            trustedIssuer = new ArrayList<>();
            trustedIssuer.addAll(verifier.getTrustedIssuer());
        }
        trustedIssuer.add(issuerDid);
        verifier.setTrustedIssuer(trustedIssuer);
        updateVerifier(id, verifier);
        System.out.println("Verifier Updated Successfully!!");
    }

    @Override
    public Verifier getVerifierById(String id) throws ExecutionException, InterruptedException {
        Value value = faunaClient.query(Get(Ref(Collection("Verifier"), id))).get();
        return new Verifier(
                value.at("ref").to(Value.RefV.class).get().getId(),
                value.at("data", "name").to(String.class).get(),
                value.at("data", "govId").to(String.class).get(),
                value.at("data", "trustedIssuer").collect(String.class).stream().toList(),
                value.at("data", "privateDid").to(String.class).get(),
                value.at("data", "publicDid").to(String.class).get()
        );
    }

    @Override
    public void updateVerifier(String id, Verifier verifier) {
        faunaClient.query(
                Update(
                        Ref(Collection("Verifier"), id),
                        Obj(
                                "data",
                                Obj(
                                        "name", Value(verifier.getName()),
                                        "govId", Value(verifier.getGovId()),
                                        "trustedIssuer", Value(verifier.getTrustedIssuer()),
                                        "privateDid", Value(verifier.getPrivateDid()),
                                        "publicDid", Value(verifier.getPublicDid())
                                )
                        )
                )
        );
    }

    @Override
    public VerificationResult verify_vc(String id, String vcId) throws ExecutionException, InterruptedException {
        VerifiableCredentials vc = vcService.getVcByVCId(vcId);
        String issuerDid = vc.getIssuerDid();
        Verifier verifier = getVerifierById(id);
        List<String> trustedIssuer = verifier.getTrustedIssuer();
        Issuer issuer = issuerService.getIssuerByPublicDid(issuerDid);
        Map<String, String> map = new HashMap<>();
        VerificationResult vr = new VerificationResult();
        if(trustedIssuer.contains(issuerDid)) {
            if(issuer.getIssuedVCs().contains(vcId)) {
                map.put("SignaturePolicy", "passed");
                map.put("JsonSchemaPolicy", "passed");
                return verifyPolicy(map, vc.getDetails(), vc.getProof().getProofPurpose());
            }
        } else {
            vr.setResult("failed");
            map.put("SignaturePolicy", "failed");
            map.put("JsonSchemaPolicy", "failed");
            vr.setPolicy(map);
        }
        return vr;
    }

    private VerificationResult verifyPolicy(Map<String, String> map, UserDetails userDetails, List<String> policies) {
        VerificationResult result = new VerificationResult();
        if(policies.size() == 1) {
            if(policies.get(0).equals("AgeVerification")) {
                if(checkAgeLimit(userDetails.getDateOfBirth())) {
                    map.put("AgeVerification", "passed");
                    result.setResult("passed");
                } else {
                    map.put("AgeVerification", "failed");
                    result.setResult("failed");
                }
            } else if(policies.get(0).equals("StudentVerification")) {
                if(checkStudentId(userDetails.getDocType())) {
                    map.put("StudentVerification", "passed");
                    result.setResult("passed");
                } else {
                    map.put("StudentVerification", "failed");
                    result.setResult("failed");
                }
            }
        } else if(policies.get(0).equals("AgeVerification") && policies.get(1).equals("StudentVerification")){
            if(checkAgeLimit(userDetails.getDateOfBirth()) && checkStudentId(userDetails.getDocType())) {
                map.put("AgeVerification", "passed");
                map.put("StudentVerification", "passed");
                result.setResult("passed");
            } else if(!checkAgeLimit(userDetails.getDateOfBirth())){
                map.put("StudentVerification", "passed");
                map.put("AgeVerification", "failed");
                result.setResult("failed");
            } else if (!checkStudentId(userDetails.getDocType())) {
                map.put("StudentVerification", "failed");
                map.put("AgeVerification", "passed");
                result.setResult("failed");
            }
        }
        result.setPolicy(map);
        return result;
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
}
