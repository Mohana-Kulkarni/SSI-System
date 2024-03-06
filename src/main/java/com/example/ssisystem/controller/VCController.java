package com.example.ssisystem.controller;

import com.example.ssisystem.entity.VerifiableCredentials;
import com.example.ssisystem.service.vc.VCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/vcs")
public class VCController {

    @Autowired
    VCService vcService;

    @GetMapping("/id")
    public VerifiableCredentials getVCById(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return vcService.getVCById(id);
    }

    @GetMapping("/vcId")
    public VerifiableCredentials getVCByVCId(@RequestParam("vcId") String vcId) throws ExecutionException, InterruptedException {
        return vcService.getVcByVCId(vcId);
    }
}

