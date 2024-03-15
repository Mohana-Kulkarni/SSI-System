package com.example.ssisystem.controller;

import com.example.ssisystem.entity.VerifiableCredentials;
import com.example.ssisystem.service.vc.VCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<VerifiableCredentials> getVCById(@RequestParam("id") String id) throws ExecutionException, InterruptedException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(vcService.getVCById(id));
    }

    @GetMapping("/vcId")
    public ResponseEntity<VerifiableCredentials> getVCByVCId(@RequestParam("vcId") String vcId) throws ExecutionException, InterruptedException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(vcService.getVcByVCId(vcId));
    }
}

