package com.pesocorporalehr.ehrrepository.config;

import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClient;
import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClientConfig;
import org.ehrbase.openehr.sdk.client.openehrclient.defaultrestclient.DefaultRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * Si hago todo con rest esta clase se puede borrar
 */
@Configuration
public class EhrbaseSdkConfig {


    @Value("${ehrbase.url}")
    private String ehrBaseUrl;

    @Value("${ehrbase.username}")
    private String ehrBaseUsername;


    @Value("${ehrbase.password}")
    private String ehrBasePassword;

    @Bean
    public OpenEhrClient openEhrClient() {
        OpenEhrClientConfig config = new OpenEhrClientConfig(
                URI.create(ehrBaseUrl)
        );
        return new DefaultRestClient(config);
    }
}