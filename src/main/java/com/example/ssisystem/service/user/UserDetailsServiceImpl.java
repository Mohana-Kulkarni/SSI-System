package com.example.ssisystem.service.user;

import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.service.did.DIDService;
import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import static com.faunadb.client.query.Language.*;
import static com.faunadb.client.query.Language.Value;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    private FaunaClient faunaClient;
    private DIDService didService;
    public UserDetailsServiceImpl(FaunaClient faunaClient, DIDService didService) {
        this.faunaClient = faunaClient;
        this.didService = didService;
    }
    @Override
    public String addUserDetails(UserDetails userDetails) throws ExecutionException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        userDetails.setUserDid(didService.generatePublicDid());
        Value val = faunaClient.query(
                Create(
                        Collection("UserDetails"),
                        Obj(
                                "data", Value(userDetails)
                        )
                )
        ).get();
        return val.at("ref").to(Value.RefV.class).get().getId();
    }

    @Override
    public UserDetails getUserById(String id) throws ExecutionException, InterruptedException {
        Value val = faunaClient.query(Get(Ref(Collection("UserDetails"), id))).get();
        return new UserDetails(
                val.at("data", "userDid").to(String.class).get(),
                val.at("data", "firstName").to(String.class).get(),
                val.at("data", "lastName").to(String.class).get(),
                val.at("data", "address").to(String.class).get(),
                val.at("data", "dateOfBirth").to(String.class).get(),
                val.at("data", "gender").to(String.class).get(),
                val.at("data","placeOfBirth").to(String.class).get(),
                val.at("data", "proofId").to(String.class).get(),
                val.at("data", "docType").to(String.class).get()
        );
    }
}
