package com.example.ssisystem.service.issuer;

import com.example.ssisystem.constants.GlobalConstants;
import com.example.ssisystem.entity.*;
import com.example.ssisystem.exception.classes.ResourceAlreadyExistsException;
import com.example.ssisystem.exception.classes.ResourceNotFoundException;
import com.example.ssisystem.service.did.DIDService;
import com.example.ssisystem.service.user.UserDetailsService;
import com.example.ssisystem.service.vc.VCService;
import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.faunadb.client.query.Language.*;
import static com.faunadb.client.query.Language.Value;

@Service
public class IssuerServiceImpl implements IssuerService{

    private FaunaClient faunaClient;
    private DIDService didServices;
    private UserDetailsService userDetailsService;
    private VCService vcService;
    private BCryptPasswordEncoder encoder;

    public IssuerServiceImpl(FaunaClient faunaClient, DIDService didServices,VCService vcService, UserDetailsService userDetailsService, BCryptPasswordEncoder encoder) {
        this.faunaClient= faunaClient;
        this.didServices = didServices;
        this.vcService = vcService;
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
    }
    @Override
    public Map<String, String> addIssuer(Issuer issuer) throws ExecutionException, InterruptedException {
        Map<String, String> res = new HashMap<>();
        try{
//            try{
//                Issuer issuer1 = getIssuerByWalletId(issuer.getWalletId());
//            } catch (ResourceAlreadyExistsException e) {
//                throw new ResourceAlreadyExistsException("Issuer Already Exists");
//            }

            String name = issuer.getName();
            String govId = issuer.getGovId();
            String encryptedPassword = encoder.encode(issuer.getPassword());
            Map<String, Object> map = new HashMap<>();
            map .put("name", name);
            map.put("govId", govId);
            map.put("email", issuer.getEmail());
            map.put("password", encryptedPassword);
            map.put("walletId", issuer.getWalletId());
            map.put("type", issuer.getType());
            map.put("privateDid", didServices.generatePrivateDid());
            String publicDid = didServices.generatePublicDid();
            map.put("publicDid", publicDid);
            map.put("pendingRequests", new ArrayList<>());
            map.put("issuedVCs", new ArrayList<>());
            map.put("rejectedRequests", new ArrayList<>());
            Value val = faunaClient.query(
                    Create(
                            Collection("Issuer"),
                            Obj(
                                    "data", Value(map)
                            )
                    )
            ).get();
            res.put("result", "true");
            res.put("publicDid", publicDid);
            res.put("id", val.at("ref").get(Value.RefV.class).getId());
            return res;
        }catch (ResourceAlreadyExistsException e) {
            throw new ResourceAlreadyExistsException("Issuer Already Exists!!!");
        } catch (Exception e){
            throw new RuntimeException(GlobalConstants.MESSAGE_417_POST);
        }

    }

    @Override
    public Issuer getIssuerById(String id) throws ExecutionException, InterruptedException {
        try {
            Value res = faunaClient.query(Get(Ref(Collection("Issuer"), id))).get();
            String issuerType = res.at("data", "type").to(String.class).get();

            return new Issuer(
                    res.at("ref").get(Value.RefV.class).getId(),
                    res.at("data", "name").to(String.class).get(),
                    res.at("data", "email").to(String.class).get(),
                    res.at("data", "govId").to(String.class).get(),
                    issuerType,
                    res.at("data", "walletId").to(String.class).get(),
                    res.at("data", "publicDid").to(String.class).get(),
                    res.at("data", "privateDid").to(String.class).get(),
                    res.at("data", "issuedVCs").collect(String.class).stream().toList(),
                    res.at("data", "pendingRequests").collect(String.class).stream().toList(),
                    res.at("data", "rejectedRequests").collect(String.class).stream().toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "Id", id);
        }
    }

    @Override
    public Issuer getIssuerByLogin(String email, String password) throws ExecutionException, InterruptedException {
        try {
            Value res = faunaClient.query(Get(Match(Index("issuer_by_email"), Value(email)))).get();
            String encryptedPassword = res.at("data", "password").to(String.class).get();

            String encryptedEnteredPassword = encoder.encode(password);

            if(!encoder.matches(password, encryptedPassword)) {
                throw new RuntimeException("Invalid Credentials!!");
            }
            return getIssuerById(res.at("ref").get(Value.RefV.class).getId());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Issuer", "Email", email);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Credentials!!");
        }
    }

    @Override
    public Issuer getIssuerByPublicDid(String did) throws ExecutionException, InterruptedException {
        try {
            Value res = faunaClient.query(Get(Match(Index("issuer_by_publicDid"), Value(did)))).get();
            return getIssuerById(res.at("ref").get(Value.RefV.class).getId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "did", did);
        }
    }

    @Override
    public Issuer getIssuerByWalletId(String walletId) throws ExecutionException, InterruptedException {
        try {
            Value res = faunaClient.query(Get(Match(Index("issuer_by_walletId"), Value(walletId)))).get();
            return getIssuerById(res.at("ref").get(Value.RefV.class).getId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "walletId", walletId);
        }
    }

    @Override
    public List<Issuer> getIssuerByType(String type) throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<Value> res = faunaClient.query(Paginate(Documents(Collection("Issuer"))));
            Value value = res.join();
            List<Value> valueList = value.at("data").collect(Value.class).stream().toList();
            List<Issuer> issuers = new ArrayList<>();
            for (Value val : valueList) {
                String issuerId = ((Value.RefV)val).getId();
                Issuer issuer = getIssuerById(issuerId);
                if (issuer.getType().equals(type)) {
                    issuers.add(issuer);
                }
            }
            return issuers;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "type", type);
        }
    }

    @Override
    public List<Issuer> getIssuersFromDidList(List<String> dids) throws ExecutionException, InterruptedException {
        List<Issuer> issuers = new ArrayList<>();
        for (String did: dids) {
            Issuer issuer = getIssuerByPublicDid(did);
            issuers.add(issuer);
        }
        return issuers;
    }

    @Override
    public List<UserDetails> getPendingRequestsByIssuer(String issuerId) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerById(issuerId);
        try {
            List<UserDetails> requests = new ArrayList<>();
            for(String id: issuer.getPendingRequests()) {
                try {
                    UserDetails details = userDetailsService.getUserById(id);
                    requests.add(details);
                } catch (ResourceNotFoundException e) {
                    throw new ResourceNotFoundException("UserDeails", "id", id);
                }
            }
            return requests;
        }
        catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "id", issuerId);
        }
    }

    @Override
    public List<UserDetails> getRejectedRequestsByIssuer(String issuerId) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerById(issuerId);
        try {
            List<UserDetails> rejected = new ArrayList<>();
            for(String id: issuer.getRejectedRequests()) {
                UserDetails details = userDetailsService.getUserById(id);
                rejected.add(details);
            }
            return rejected;

        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "id", issuerId);
        }
    }

    @Override
    public List<VerifiableCredentials> getVCsIssuedByIssuer(String issuerId) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerById(issuerId);
        try {
            List<VerifiableCredentials> vcs = new ArrayList<>();
            for(String id: issuer.getIssuedVCs()) {
                VerifiableCredentials vc = vcService.getVcByVCId(id);
                vcs.add(vc);
            }
            return vcs;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "id", issuerId);
        }
    }


    @Override
    public boolean addPendingRequests(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerByPublicDid(issuerDid);
        try {
            List<String> rejectedRequests;
            if(issuer.getRejectedRequests() == null) {
                rejectedRequests = new ArrayList<>();
            } else {
                rejectedRequests = new ArrayList<>();
                rejectedRequests.addAll(issuer.getRejectedRequests());
            }

            if(rejectedRequests.contains(userDetailsId)) {
                rejectedRequests.remove(userDetailsId);
                UserDetails details = userDetailsService.getUserById(userDetailsId);
                List<VerificationResult> resultList = new ArrayList<>();
                if(details.getVerificationResult() != null) {
                    resultList.addAll(details.getVerificationResult());
                }
                for (VerificationResult result : details.getVerificationResult()) {
                    if(result.getIssuerDid().equals(issuerDid)) {
                        resultList.remove(result);
                    }
                }
                details.setVerificationResult(resultList);
                userDetailsService.updateUserDetails(userDetailsId, details);
                issuer.setRejectedRequests(rejectedRequests);
            }

            List<String> pendingRequests = new ArrayList<>();
            for (String id: issuer.getPendingRequests()) {
                pendingRequests.add(id);
            }
            if(!pendingRequests.contains(userDetailsId)) {
                pendingRequests.add(userDetailsId);
                issuer.setPendingRequests(pendingRequests);
            }
            try {
                updateIssuer(issuerDid, issuer);
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "did", issuerDid);
        }

    }

    @Override
    public boolean addIssuedVCs(String issuerDid, String vcId) throws ExecutionException, InterruptedException {
        try {
            Issuer issuer = getIssuerByPublicDid(issuerDid);
            List<String> issuedVCs = new ArrayList<>();
            issuedVCs.addAll(issuer.getPendingRequests());
            issuedVCs.add(vcId);

            try {
                updateIssuer(issuerDid, issuer);
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "did", issuerDid);
        }
    }
    @Override
    public boolean updateIssuer(String did, Issuer issuer) throws ExecutionException, InterruptedException {
        try {
            Issuer issuer1 = getIssuerByPublicDid(did);
            try {
                String name = issuer.getName();
                String govId = issuer.getGovId();
                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("govId", govId);
                map.put("email", issuer.getEmail());
                map.put("type", issuer.getType());
                map.put("privateDid", issuer1.getPrivateDid());
                map.put("publicDid", issuer1.getPublicDid());
                map.put("pendingRequests", issuer1.getPendingRequests());
                map.put("rejectedRequests", issuer1.getRejectedRequests());
                map.put("issuedVCs", issuer1.getIssuedVCs());
                faunaClient.query(Update(
                        Ref(Collection("Issuer"), issuer1.getId()),
                        Obj(
                                "data", Value(map)
                        )
                )).join();
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "did", did);
        }

    }

    @Override
    public boolean issueVC(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerByPublicDid(issuerDid);
        try {
            UserDetails userDetails = userDetailsService.getUserById(userDetailsId);
            try {
                VerificationResult result = verifyPolicy(userDetails, String.valueOf(issuer.getType()));
                result.setIssuerDid(issuerDid);
                String vcId;
                boolean res;
                if (result.getResult().equals("fail")) {
                    vcId = "";
                    res = false;
                    List<VerificationResult> resultList = new ArrayList<>();
                    resultList.addAll(userDetails.getVerificationResult());
                    resultList.add(result);
                    userDetails.setVerificationResult(resultList);
                    System.out.println("Problem in generating VC !!");

                } else {
                    VerifiableCredentials vc = vcService.issueCredentials(userDetails, issuer, issuer.getType(), issuer.getPrivateDid());
                    vcId = vc.getId();
                    res = true;
                }
                List<String> issuedVCs;
                if(userDetails.getIssuedVCs() == null) {
                    issuedVCs = new ArrayList<>();
                } else {
                    issuedVCs = new ArrayList<>();
                    issuedVCs.addAll(userDetails.getIssuedVCs());
                }
                if(res){
                    issuedVCs.add(vcId);
                    userDetails.setIssuedVCs(issuedVCs);
                    userDetailsService.updateUserDetails(userDetailsId, userDetails);
                    removePendingRequest(issuerDid, userDetailsId, vcId, res);
                    System.out.println("VC generated successfully!!");
                    return true;
                } else if(!res) {
                    removePendingRequest(issuerDid, userDetailsId, vcId, res);
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                throw new ResourceNotFoundException("UserDetails", "id", userDetailsId);
            }
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("UserDetails", "id", userDetailsId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "did", issuerDid);
        }
    }

    @Override
    public boolean rejectRequest(String userDetailsId, String issuerDid) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerByPublicDid(issuerDid);
        try {
            UserDetails details = userDetailsService.getUserById(userDetailsId);
            try {
                List<String> pendingRequests = new ArrayList<>();
                pendingRequests.addAll(issuer.getPendingRequests());
                pendingRequests.remove(userDetailsId);
                issuer.setPendingRequests(pendingRequests);
                List<String> rejectedRequests = new ArrayList<>();
                rejectedRequests.addAll(issuer.getRejectedRequests());
                rejectedRequests.add(userDetailsId);
                issuer.setRejectedRequests(rejectedRequests);
                VerificationResult result = new VerificationResult();
                result.setResult("fail");
                result.setIssuerDid(issuerDid);
                Map<String, String> map = new HashMap<>();
                map.put("Document Verification", "failed");
                result.setPolicy(map);
                List<VerificationResult> resultList = new ArrayList<>();
                resultList.addAll(details.getVerificationResult());
                resultList.add(result);
                details.setVerificationResult(resultList);
                try {
                    userDetailsService.updateUserDetails(userDetailsId, details);
                    updateIssuer(issuerDid, issuer);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } catch (Exception e) {
                throw new ResourceNotFoundException("UserDetails", "id", userDetailsId);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "did", issuerDid);
        }
    }


    private String getIssuerIdByDid(String did) throws ExecutionException, InterruptedException {
        try {
            Value value = faunaClient.query(Get(Match(Index("issuer_by_publicDid"), Value(did)))).get();
            return value.at("ref").to(Value.RefV.class).get().getId();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Issuer", "did", did);
        }
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
        } else {
            List<String> rejectedRequests = new ArrayList<>();
            rejectedRequests.addAll(issuer.getRejectedRequests());
            rejectedRequests.add(userDetailsId);
            issuer.setRejectedRequests(rejectedRequests);
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
