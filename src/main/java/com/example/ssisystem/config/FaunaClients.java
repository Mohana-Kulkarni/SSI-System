package com.example.ssisystem.config;

import com.faunadb.client.FaunaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

@Configuration
public class FaunaClients {

    @Value("https://db.${fauna.region}.fauna.com/")
    private String faunaUrl;

    @Value("${fauna.secret}")
    private String faunaSecret;

    @Bean
    FaunaClient getFaunaClient() throws MalformedURLException {
        System.out.println("Secret Key: " + faunaSecret);
        return com.faunadb.client.FaunaClient.builder()
                .withEndpoint(faunaUrl)
                .withSecret(faunaSecret)
                .build();

    }
}
