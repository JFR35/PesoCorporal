package com.pesocorporalehr.ehrrepository.service.impl;


import com.pesocorporalehr.ehrrepository.service.VerifyEhrConnectionService;
import com.pesocorporalehr.ehrrepository.service.exception.EhrbaseConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VerifyEhrConnectionServiceImpl implements VerifyEhrConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(VerifyEhrConnectionService.class);

    @Value("${ehrbase.ping-endpoint}")
    private String pingEndpoint;

    private final RestTemplate restTemplate;

    public VerifyEhrConnectionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public boolean verifyEhrconn() throws EhrbaseConnectionException {
        logger.info("Verificando conectividad con EHRbase en {}", pingEndpoint);
        try {
            String response = restTemplate.getForObject(pingEndpoint, String.class);
            logger.info("EHRbase respondió al ping con éxito.");
            return true;
        } catch (Exception e) {
            logger.error("No se pudo establecer conexión con EHRbase", e);
            throw new EhrbaseConnectionException("No se pudo establecer conexión con EHRbase en: " + pingEndpoint, e);
        }
    }
}

