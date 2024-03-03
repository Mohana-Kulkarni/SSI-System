package com.example.ssisystem.service.vc;

import com.example.ssisystem.entity.Issuer;
import com.example.ssisystem.entity.UserDetails;
import com.example.ssisystem.entity.VerifiableCredentials;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface VCService {
    VerifiableCredentials issueCredentials(UserDetails userDetails, Issuer issuer, String proofPurpose, String privateDid);
    VerifiableCredentials getVCById(String id) throws ExecutionException, InterruptedException;
    VerifiableCredentials getVcByVCId(String vcId) throws ExecutionException, InterruptedException;
    void deleteVCs() throws ExecutionException, InterruptedException;

}
