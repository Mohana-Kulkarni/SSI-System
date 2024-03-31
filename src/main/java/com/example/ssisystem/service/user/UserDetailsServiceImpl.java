package com.example.ssisystem.service.user;

import com.example.ssisystem.constants.GlobalConstants;
import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.entity.VerifiableCredentials;
import com.example.ssisystem.entity.VerificationResult;
import com.example.ssisystem.exception.classes.ResourceNotFoundException;
import com.example.ssisystem.service.did.DIDService;
import com.example.ssisystem.service.vc.VCService;
import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.faunadb.client.query.Language.*;
import static com.faunadb.client.query.Language.Value;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    private FaunaClient faunaClient;
    private DIDService didService;
    private VCService vcService;
    private RestTemplate restTemplate;
    public UserDetailsServiceImpl(FaunaClient faunaClient, DIDService didService, VCService vcService, RestTemplate restTemplate) {
        this.faunaClient = faunaClient;
        this.didService = didService;
        this.vcService = vcService;
        this.restTemplate = restTemplate;
    }
    @Override
    public Map<String, String> addUserDetails(String userId, UserDetails userDetails) throws ExecutionException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            String userDid = didService.generatePublicDid();
            userDetails.setUserDid(userDid);
            Map<String, Object> map = new HashMap<>();
            map.put("firstName", userDetails.getFirstName());
            map.put("lastName", userDetails.getLastName());
            map.put("address", userDetails.getAddress());
            map.put("userDid", userDetails.getUserDid());
            map.put("dateOfBirth", userDetails.getDateOfBirth());
            map.put("placeOfBirth", userDetails.getPlaceOfBirth());
            map.put("proofId", userDetails.getProofId());
            map.put("gender", userDetails.getGender());
            map.put("docType", userDetails.getDocType());
            map.put("verificationResult", new ArrayList<VerificationResult>());
            map.put("issuedVCs", new ArrayList<>());
            Value val = faunaClient.query(
                    Create(
                            Collection("UserDetails"),
                            Obj(
                                    "data", Value(map)
                            )
                    )
            ).get();
            String id =  val.at("ref").to(Value.RefV.class).get().getId();
            Map<String, String> result = new HashMap<>();
            result.put("result", "true");
            result.put("id", id);
            result.put("userDid", userDid);

            String url = "https://ticketing-service-flhm.onrender.com/users/details/?id=%s&detailsId=%s";
            url = String.format(url, userId, id);

            System.out.println(url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request entity with headers
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", userId);
            requestBody.put("detailsId", id);

            HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class,
                    userId,
                    id
            );
            // Get response status code and body
//            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
//            String responseBody = responseEntity.getBody();
//
            return result;
        } catch (Exception e) {
            throw new RuntimeException(GlobalConstants.MESSAGE_417_POST);
        }
    }

    @Override
    public UserDetails getUserById(String id) throws ExecutionException, InterruptedException {
        try {
            Value val = faunaClient.query(Get(Ref(Collection("UserDetails"), id))).get();
            return new UserDetails(
                    val.at("ref").to(Value.RefV.class).get().getId(),
                    val.at("data", "userDid").to(String.class).get(),
                    val.at("data", "firstName").to(String.class).get(),
                    val.at("data", "lastName").to(String.class).get(),
                    val.at("data", "address").to(String.class).get(),
                    val.at("data", "dateOfBirth").to(String.class).get(),
                    val.at("data", "gender").to(String.class).get(),
                    val.at("data","placeOfBirth").to(String.class).get(),
                    val.at("data", "proofId").to(String.class).get(),
                    val.at("data", "docType").to(String.class).get(),
                    val.at("data", "verificationResult").collect(VerificationResult.class).stream().toList(),
                    val.at("data", "issuedVCs").collect(String.class).stream().toList()
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("UserDetails", "id", id);
        }
    }

    @Override
    public boolean updateUserDetails(String id, UserDetails userDetails) throws ExecutionException, InterruptedException {
        try {
            getUserById(id);
            try {
                faunaClient.query(Update
                        (Ref(Collection("UserDetails"), id),
                                Obj(
                                        "data", Value(userDetails)
                                )
                        )).get();
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("UserDetails", "id", id);
        }

    }

    @Override
    public List<VerifiableCredentials> getAllVCsByUserID(String userDetailsDid) throws ExecutionException, InterruptedException {
        try {
            getUserIdByDid(userDetailsDid);
            try {
                CompletableFuture<Value> res = faunaClient.query(Paginate(Documents(Collection("Verifiable_Credentials"))));
                Value value = res.join();
                List<Value> valueList = value.at("data").collect(Value.class).stream().toList();
                List<VerifiableCredentials> vcs = new ArrayList<>();
                for(Value val : valueList) {
                    VerifiableCredentials vc = vcService.getVCById(((Value.RefV)val).getId());
                    if (vc.getDetails().getUserDid().equals(userDetailsDid)) {
                        vcs.add(vc);
                    }
                }
                return vcs;
            } catch (Exception e) {
                throw new RuntimeException("VCs not found with id" + userDetailsDid);
            }
        }catch (Exception e) {
            throw new ResourceNotFoundException("UserDetails" , "did", userDetailsDid);
        }
    }

    @Override
    public List<VerifiableCredentials> getVCsByUserIdAndIssuers(List<String> issuers, String userDid) throws ExecutionException, InterruptedException {
        try {
            getUserIdByDid(userDid);
            try {
                List<VerifiableCredentials> vcList = new ArrayList<>();
                List<VerifiableCredentials> vcs = getAllVCsByUserID(userDid);
                for(VerifiableCredentials vc : vcs) {
                    if(issuers.contains(vc.getIssuer().getPublicDid())) {
                        vcList.add(vc);
                    }
                }
                return vcList;
            } catch (Exception e) {
                throw new RuntimeException("VCs not found with userDid" + userDid);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("UserDetails", "did", userDid);
        }
    }


    public String getUserIdByDid(String did) throws ExecutionException, InterruptedException {
        Value value = faunaClient.query(Get(Match(Index("user_by_did"), Value(did)))).get();
        return value.at("ref").get(Value.RefV.class).getId();
    }

}
