package com.example.ssisystem.service.vc;

import com.example.ssisystem.entity.*;
import com.example.ssisystem.exception.classes.ResourceNotFoundException;
import com.example.ssisystem.service.did.DIDService;
import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.Signature;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.faunadb.client.query.Language.*;
import static com.faunadb.client.query.Language.Value;

@Service
public class VCServiceImpl implements VCService{

    private FaunaClient faunaClient;
    private DIDService didServices;

    public VCServiceImpl(FaunaClient faunaClient, DIDService didServices) {
        this.faunaClient = faunaClient;
        this.didServices = didServices;
    }
    @Override
    public VerifiableCredentials issueCredentials(UserDetails userDetails, Issuer issuer, String proofPurpose, String privateDid) {
        try {
            String id = UUID.randomUUID().toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
            String issuanceDate = LocalDateTime.now().format(formatter);
            String expirationDate = LocalDateTime.now().plusDays(30).format(formatter);
            String validFrom = issuanceDate;


            VerifiableCredentials  vc = new VerifiableCredentials(userDetails, new Issuer(issuer.getName(), issuer.getPublicDid()), "LD_Proof");

            vc.setId(id);
            vc.setExpirationDate(expirationDate);
            vc.setIssuanceDate(issuanceDate);
            vc.setValidFrom(validFrom);


            ProofUtil proof = new ProofUtil();

            if(issuer.getPrivateDid().equals(privateDid)) {
                proof.setProofType("Ed25519Signature2018");
                proof.setVerificationMethod(issuer.getPublicDid() + "#key");
                PrivateKey privateKey = didServices.generatePrivateKey();
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initSign(privateKey);
                byte[] signatureBytes = signature.sign();
                String signatureValue = Base64.getEncoder().encodeToString(signatureBytes);
                proof.setJws(signatureValue);
                proof.setProofPurpose(proofPurpose);
                proof.setCreated(issuanceDate);
            }

            vc.setProof(proof);

            Map<String, Object> map = new HashMap<>();
            map.put("vcId", vc.getId());
            map.put("issuer", vc.getIssuer());
            map.put("subject", vc.getDetails());
            map.put("expirationDate", expirationDate);
            map.put("proof", vc.getProof());
            map.put("status", "active");


            faunaClient.query(
                    Create(
                            Collection("Verifiable_Credentials"),
                            Obj(
                                    "data", Value(map)
                            )
                    )
            );
            return vc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VerifiableCredentials getVCById(String id) throws ExecutionException, InterruptedException {
        try {
            Value value = faunaClient.query(Get(Ref(Collection("Verifiable_Credentials"), id))).get();
            String expirationDate = value.at("data", "expirationDate").to(String.class).get();
            String issuanceStr = value.at("data", "proof", "created").to(String.class).get();
            UserDetails userDetails = new UserDetails(
                    value.at("ref").to(Value.RefV.class).get().getId(),
                    value.at("data", "subject", "userDid").to(String.class).get(),
                    value.at("data", "subject", "firstName").to(String.class).get(),
                    value.at("data", "subject", "lastName").to(String.class).get(),
                    value.at("data", "subject", "address").to(String.class).get(),
                    value.at("data", "subject", "dateOfBirth").to(String.class).get(),
                    value.at("data", "subject", "gender").to(String.class).get(),
                    value.at("data", "subject", "placeOfBirth").to(String.class).get(),
                    value.at("data", "subject", "proofId").to(String.class).get(),
                    value.at("data", "subject", "docType").to(String.class).get(),
                    value.at("data", "subject", "verificationResult").collect(VerificationResult.class).stream().toList(),
                    value.at("data", "subject", "issuedVCs").collect(String.class).stream().toList()
            );
            ProofUtil proof = new ProofUtil(
                    value.at("data", "proof", "proofType").to(String.class).get(),
                    value.at("data", "proof", "verificationMethod").to(String.class).get(),
                    value.at("data", "proof", "jws").to(String.class).get(),
                    value.at("data", "proof", "created").to(String.class).get(),
                    value.at("data", "proof", "proofPurpose").to(String.class).get()
            );
            Issuer issuer = new Issuer(
                    value.at("data", "issuer", "name").to(String.class).get(),
                    value.at("data", "issuer", "publicDid").to(String.class).get()
            );
            return new VerifiableCredentials(
                    value.at("data", "vcId").to(String.class).get(),
                    userDetails,
                    issuer,
                    "LD_Proof",
                    issuanceStr,
                    expirationDate,
                    issuanceStr,
                    proof,
                    value.at("data", "status").to(String.class).get()
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("VC", "id", id);
        }
    }

    @Override
    public VerifiableCredentials getVcByVCId(String vcId) throws ExecutionException, InterruptedException {
        try {
            Value value = faunaClient.query(Get(Match(Index("verifiable_credentials_by_vcId"), Value(vcId)))).get();
            String id = value.at("ref").get(Value.RefV.class).getId();
            return getVCById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("VC", "vcId", vcId);
        }
    }


    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void updateVCStatus() throws ExecutionException, InterruptedException {
        CompletableFuture<Value> result = faunaClient.query(
                Paginate(Documents(Collection("Verifiable_Credentials")))
        );
        Value value = result.join();
        List<Value> res = value.at("data").collect(Value.class).stream().toList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        for(Value val : res) {
            String id = val.get(Value.RefV.class).getId();
            VerifiableCredentials vc = getVCById(id);
            String expirationDate = vc.getExpirationDate();
            LocalDateTime date = LocalDateTime.parse(expirationDate, formatter);
            if(now.isAfter(date)) {
                vc.setStatus("expired");
                faunaClient.query(Update(
                        Ref(Collection("Verifiable_Credentials"), id),
                        Obj(
                                "data", Value(vc)
                        )
                ));
            }
        }
    }
}
