package com.example.ssisystem.service.verifier;

import com.example.ssisystem.entity.*;
import com.example.ssisystem.service.did.DIDService;
import com.example.ssisystem.service.issuer.IssuerService;
import com.example.ssisystem.service.vc.VCService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
    private RestTemplate restTemplate;
    private BCryptPasswordEncoder encoder;

    public VerifierServiceImpl(FaunaClient faunaClient, DIDService didServices,IssuerService issuerService, VCService vcService, RestTemplate restTemplate, BCryptPasswordEncoder encoder) {
        this.faunaClient = faunaClient;
        this.didServices = didServices;
        this.issuerService = issuerService;
        this.vcService = vcService;
        this.restTemplate = restTemplate;
        this.encoder = encoder;
    }
    @Override
    public void createVerifier(String name, String email, String password, String govId) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        String encryptedPassword = encoder.encode(password);
        map.put("password", encryptedPassword);
        map.put("govId", govId);
        map.put("trustedIssuer", new ArrayList<>());
        map.put("privateDid", didServices.generatePrivateDid());
        map.put("publicDid", didServices.generatePublicDid());
        faunaClient.query(
                Create(
                        Collection("Verifier"),
                        Obj(
                                "data",Value(map)
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
                value.at("data", "email").to(String.class).get(),
                value.at("data", "govId").to(String.class).get(),
                value.at("data", "trustedIssuer").collect(String.class).stream().toList(),
                value.at("data", "privateDid").to(String.class).get(),
                value.at("data", "publicDid").to(String.class).get()
        );
    }

    @Override
    public Verifier getVeriferByLogin(String email, String password) throws ExecutionException, InterruptedException {
        Value res = faunaClient.query(Get(Match(Index("verifier_by_email"), Value(email)))).get();
        String encryptedPassword = res.at("data", "password").to(String.class).get();

        if (encoder.matches(password, encryptedPassword)) {
            return getVerifierById(res.at("ref").get(Value.RefV.class).getId());
        } else {
            return null;
        }
    }

    @Override
    public Verifier getVerifierByWalletId(String walletId) throws ExecutionException, InterruptedException {
        Value res = faunaClient.query(Get(Match(Index("verifier_by_walletId"), Value(walletId)))).get();
        return getVerifierById(res.at("ref").get(Value.RefV.class).getId());
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
    public VerificationResult verify_vc(String id, String vcId, String ticketId, String nftId) throws ExecutionException, InterruptedException, JsonProcessingException {
        VerifiableCredentials vc = vcService.getVcByVCId(vcId);
        String issuerDid = vc.getIssuerDid();
        Verifier verifier = getVerifierById(id);
        List<String> trustedIssuer = verifier.getTrustedIssuer();
        Issuer issuer = issuerService.getIssuerByPublicDid(issuerDid);
        String policy = String.valueOf(issuer.getType());
        Map<String, String> map = new HashMap<>();
        VerificationResult vr = new VerificationResult();
        if(trustedIssuer.contains(issuerDid)) {
            vr.setIssuerDid(issuerDid);
            if(issuer.getIssuedVCs().contains(vcId)) {
                vr.setResult("pass");
                map.put("SignaturePolicy", "passed");
                map.put("JsonSchemaPolicy", "passed");
                if(policy.equals("AgeVerification")) {
                    map.put("AgeVerification", "passed");
                } else if (policy.equals("StudentVerification")) {
                    map.put("StudentVerification", "passed");
                } else {
                    map.put("AgeVerification", "passed");
                    map.put("StudentVerification", "passed");
                }
            }


            String url = "https://ticketing-service-flhm.onrender.com/tickets/scanNft?id=%s&nftId=%s";
            url = String.format(url, ticketId, nftId);

            System.out.println(url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request entity with headers
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", ticketId);
            requestBody.put("nftId", nftId);

            HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class,
                    id,
                    nftId
            );
                // Get response status code and body
            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
            String responseBody = responseEntity.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseBody);
            String message = jsonNode.get("statusMsg").asText();
            String status = jsonNode.get("statusCode").asText();

            if(statusCode == HttpStatus.OK) {
                if(status.equals("200")) {
                    map.put("ScanResult", message);
                } else   if (status.equals("409")) {
                    map.put("ScanResult", message);
                    vr.setResult("fail");
                } else if (status.equals("451")) {
                    map.put("ScanResult", message);
                    vr.setResult("fail");
                } else if (status.equals("417")) {
                    map.put("ScanResult", message);
                    vr.setResult("fail");
                }
            }
            vr.setPolicy(map);
            // Log response status code and body
            System.out.println("Response Status Code: " + statusCode);
            System.out.println("Response Body: " + responseBody);

        } else {
            vr.setResult("failed");
            map.put("SignaturePolicy", "failed");
            map.put("JsonSchemaPolicy", "failed");
            vr.setPolicy(map);
        }
        return vr;
    }


}
