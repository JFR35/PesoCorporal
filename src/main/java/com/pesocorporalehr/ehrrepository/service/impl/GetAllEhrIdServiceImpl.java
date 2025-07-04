package com.pesocorporalehr.ehrrepository.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pesocorporalehr.ehrrepository.service.GetAllEhrIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GetAllEhrIdServiceImpl implements GetAllEhrIdService {

    @Value("${ehrbase.url}")
    private String ehrBaseUrl;

    private final Logger logger = LoggerFactory.getLogger(GetAllEhrIdServiceImpl.class);

    private final RestTemplate restTemplate;

    public GetAllEhrIdServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<String> getAllEhrIds() {
        try {
            // Envolvér la query AQL en un objeto JSON
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonQuery = mapper.createObjectNode();
            jsonQuery.put("q", "SELECT e/ehr_id/value FROM EHR e");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(jsonQuery), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    ehrBaseUrl + "/query/aql",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            String responseBody = response.getBody();
            logger.debug("Respuesta bruta del servidor EHRbase:\n{}", responseBody);
            JsonNode root = mapper.readTree(response.getBody());

            List<String> ehrIds = new ArrayList<>();
            for (JsonNode row : root.path("rows")) {
                ehrIds.add(row.get(0).asText());
            }

            return ehrIds;

        } catch (Exception e) {
            logger.error("Error ejecutando query de EHRs", e);
            return Collections.emptyList();
        }
    }
}
