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
import java.util.Map;

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

    @Override
    public String getEhrDetails(String ehrId) {
        try {
            String url = String.format("%s/ehr/%s", ehrBaseUrl, ehrId);
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            logger.warn("Error al obtener EHR con ID: {}", ehrId, e);
            throw new RuntimeException("Error obteniendo EHR: " + e.getMessage());
        }
    }
    @Override
    public String getEhrBySubjectId(String subjectId) {
        try {
            String url = ehrBaseUrl + "/query/aql";

            // Crear el body con la AQL
            String aql = String.format(
                    "SELECT e/ehr_id/value FROM EHR e WHERE e/ehr_status/subject/external_ref/id/value = '%s'",
                    subjectId
            );

            Map<String, String> requestBody = Map.of("q", aql);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Hacer la request
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // Extraer el resultado del JSON
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("rows")) {
                List<List<String>> rows = (List<List<String>>) body.get("rows");
                if (!rows.isEmpty()) {
                    return rows.get(0).get(0); // Primer valor de la primera fila
                }
            }

            logger.warn("No se encontró ningún EHR para subject_id: {}", subjectId);
            return null;

        } catch (Exception e) {
            logger.error("Error al obtener el EHR para subject_id '{}'", subjectId, e);
            throw new RuntimeException("Fallo al obtener EHR: " + e.getMessage());
        }
    }

}
