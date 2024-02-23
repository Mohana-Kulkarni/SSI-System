package com.example.ssisystem.service.vc;

import com.example.ssisystem.entity.Issuer;
import com.example.ssisystem.entity.ProofUtil;
import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.entity.VerifiableCredentials;
import com.example.ssisystem.service.did.DIDService;
import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.Signature;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
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


            VerifiableCredentials  vc = new VerifiableCredentials(userDetails, issuer.getPublicDid(), "LD_Proof");

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


            faunaClient.query(
                    Create(
                            Collection("Verifiable_Credentials"),
                            Obj(
                                    "data", Obj(
                                            "vcId", Value(vc.getId()),
                                            "issuer", Value(vc.getIssuerDid()),
                                            "subject", Value(vc.getDetails()),
                                            "expirationDate", Value(vc.getExpirationDate()),
                                            "proof", Value(vc.getProof())
                                    )
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
        Value value = faunaClient.query(Get(Ref(Collection("Verifiable_Credentials"), id))).get();
        String expirationDate = value.at("data", "expirationDate").to(String.class).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        LocalDateTime expiration = LocalDateTime.parse(expirationDate, formatter);
        LocalDateTime issuanceDate = expiration.minusDays(30);
        String issuanceStr = issuanceDate.format(formatter);
        UserDetails userDetails = new UserDetails(
                value.at("data", "subject", "userDid").to(String.class).get(),
                value.at("data", "subject", "firstName").to(String.class).get(),
                value.at("data", "subject", "lastName").to(String.class).get(),
                value.at("data", "subject", "address").to(String.class).get(),
                value.at("data", "subject", "dateOfBirth").to(String.class).get(),
                value.at("data", "subject", "gender").to(String.class).get(),
                value.at("data", "subject", "placeOfBirth").to(String.class).get(),
                value.at("data", "subject", "proofId").to(String.class).get(),
                value.at("data", "subject", "docType").to(String.class).get()
        );
        ProofUtil proof = new ProofUtil(
                value.at("data", "proof", "proofType").to(String.class).get(),
                value.at("data", "proof", "verificationMethod").to(String.class).get(),
                value.at("data", "proof", "jws").to(String.class).get(),
                value.at("data", "proof", "created").to(String.class).get(),
                value.at("data", "proof", "proofPurpose").to(String.class).get()
        );
        return new VerifiableCredentials(
                value.at("data", "vcId").to(String.class).get(),
                userDetails,
                value.at("data", "issuer").to(String.class).get(),
                "LD_Proof",
                issuanceStr,
                expirationDate,
                issuanceStr,
                proof
        );
    }

    @Override
    public VerifiableCredentials getVcByVCId(String vcId) throws ExecutionException, InterruptedException {
        Value value = faunaClient.query(Get(Match(Index("verifiable_credentials_by_vcId"), Value(vcId)))).get();
        String expirationDate = value.at("data", "expirationDate").to(String.class).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        LocalDateTime expiration = LocalDateTime.parse(expirationDate, formatter);
        LocalDateTime issuanceDate = expiration.minusDays(30);
        String issuanceStr = issuanceDate.format(formatter);
        UserDetails userDetails = new UserDetails(
                value.at("data", "subject", "userDid").to(String.class).get(),
                value.at("data", "subject", "firstName").to(String.class).get(),
                value.at("data", "subject", "lastName").to(String.class).get(),
                value.at("data", "subject", "address").to(String.class).get(),
                value.at("data", "subject", "dateOfBirth").to(String.class).get(),
                value.at("data", "subject", "gender").to(String.class).get(),
                value.at("data", "subject", "placeOfBirth").to(String.class).get(),
                value.at("data", "subject", "proofId").to(String.class).get(),
                value.at("data", "subject", "docType").to(String.class).get()
        );
        ProofUtil proof = new ProofUtil(
                value.at("data", "proof", "proofType").to(String.class).get(),
                value.at("data", "proof", "verificationMethod").to(String.class).get(),
                value.at("data", "proof", "jws").to(String.class).get(),
                value.at("data", "proof", "created").to(String.class).get(),
                value.at("data", "proof", "proofPurpose").to(String.class).get()
        );
        return new VerifiableCredentials(
                value.at("data", "vcId").to(String.class).get(),
                userDetails,
                value.at("data", "issuer").to(String.class).get(),
                "LD_Proof",
                issuanceStr,
                expirationDate,
                issuanceStr,
                proof
        );
    }
}
