package com.example.ssisystem.service.issuer;

import com.example.ssisystem.entity.Issuer;
import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.entity.VerifiableCredentials;
import com.example.ssisystem.service.did.DIDService;
import com.example.ssisystem.service.vc.VCService;
import com.faunadb.client.FaunaClient;
import com.faunadb.client.types.Value;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.faunadb.client.query.Language.*;
import static com.faunadb.client.query.Language.Value;

@Service
public class IssuerServiceImpl implements IssuerService{

    private FaunaClient faunaClient;
    private DIDService didServices;

    private VCService vcService;

    public IssuerServiceImpl(FaunaClient faunaClient, DIDService didServices,VCService vcService) {
        this.faunaClient= faunaClient;
        this.didServices = didServices;
        this.vcService = vcService;
    }
    @Override
    public void addIssuer(Issuer issuer) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        String name = issuer.getName();
        String govId = issuer.getGovId();
        faunaClient.query(
                Create(
                        Collection("Issuer"),
                        Obj(
                                "data",
                                Obj(
                                        "name", Value(name),
                                        "govId", Value(govId),
                                        "privateDid", Value(didServices.generatePrivateDid()),
                                        "publicDid", Value(didServices.generatePublicDid()),
                                        "issuedVCs", Value(new ArrayList<>())
                                )
                        )
                )
        );
    }

    @Override
    public Issuer getIssuerById(String id) throws ExecutionException, InterruptedException {
        Value res = faunaClient.query(Get(Ref(Collection("Issuer"), id))).get();
        return new Issuer(res.at("data", "name").to(String.class).get(),
                res.at("data", "govId").to(String.class).get(),
                res.at("data", "issuedVCs").collect(String.class).stream().toList(),
                res.at("data", "publicDid").to(String.class).get(),
                res.at("data", "privateDid").to(String.class).get());
    }

    @Override
    public Issuer getIssuerByPublicDid(String did) throws ExecutionException, InterruptedException {
        Value res = faunaClient.query(Get(Match(Index("issuer_by_publicDid"), Value(did)))).get();
        return new Issuer(res.at("data", "name").to(String.class).get(),
                res.at("data", "govId").to(String.class).get(),
                res.at("data", "issuedVCs").collect(String.class).stream().toList(),
                res.at("data", "publicDid").to(String.class).get(),
                res.at("data", "privateDid").to(String.class).get());
    }

    @Override
    public void updateIssuer(String did, String vcId) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerByPublicDid(did);
        String issuerId = getIssuerIdByDid(did);
        List<String> issuedVCs = new ArrayList<>();
        issuedVCs.addAll(issuer.getIssuedVCs());
        issuedVCs.add(vcId);
        faunaClient.query(Update(
                Ref(Collection("Issuer"), issuerId),
                Obj(
                        "data", Obj(
                                "name", Value(issuer.getName()),
                                "govId", Value(issuer.getGovId()),
                                "privateDid", Value(issuer.getPrivateDid()),
                                "publicDid", Value(issuer.getPublicDid()),
                                "issuedVCs", Value(issuedVCs)
                        )


                )
        ));
    }

    @Override
    public VerifiableCredentials issueVC(UserDetails userDetails, String issuerDid, String proofType, List<String> proofPurpose) throws ExecutionException, InterruptedException {
        Issuer issuer = getIssuerByPublicDid(issuerDid);
        VerifiableCredentials vc = vcService.issueCredentials(userDetails, issuer, proofType, proofPurpose, issuer.getPrivateDid());
        String vcId = vc.getId();
        updateIssuer(issuerDid, vcId);
        System.out.println("VC generated successfully!!");
        return vc;
    }

    private String getIssuerIdByDid(String did) throws ExecutionException, InterruptedException {
        Value value = faunaClient.query(Get(Match(Index("issuer_by_publicDid"), Value(did)))).get();
        return value.at("ref").to(Value.RefV.class).get().getId();
    }
}
