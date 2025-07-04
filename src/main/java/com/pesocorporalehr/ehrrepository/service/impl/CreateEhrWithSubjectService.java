package com.pesocorporalehr.ehrrepository.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pesocorporalehr.ehrrepository.service.EhrIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreateEhrWithSubjectService implements EhrIdService {

    private final Logger logger = LoggerFactory.getLogger(CreateEhrWithSubjectService.class);

    @Value("${ehrbase.url}")
    private String ehrBaseUrl;

    private final RestTemplate restTemplate;

    public CreateEhrWithSubjectService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String createEhrWithSubjectService(String subjectId, String namespace) {
        try {
            String url = ehrBaseUrl + "/ehr";

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // Construir subject con PARTY_SELF + external_ref
            LinkedHashMap<String, Object> id = new LinkedHashMap<>();
            id.put("_type", "GENERIC_ID");
            id.put("value", subjectId);
            id.put("scheme", "ehr_subjects");

            LinkedHashMap<String, Object> externalRef = new LinkedHashMap<>();
            externalRef.put("_type", "PARTY_REF");
            externalRef.put("id", id);
            externalRef.put("namespace", namespace);
            externalRef.put("type", "PERSON");

            LinkedHashMap<String, Object> subject = new LinkedHashMap<>();
            subject.put("_type", "PARTY_SELF");
            subject.put("external_ref", externalRef);

            // EHR_STATUS
            LinkedHashMap<String, Object> name = new LinkedHashMap<>();
            name.put("_type", "DV_TEXT");
            name.put("value", "EHR Status");

            LinkedHashMap<String, Object> ehrStatus = new LinkedHashMap<>();
            ehrStatus.put("_type", "EHR_STATUS");
            ehrStatus.put("archetype_node_id", "openEHR-EHR-EHR_STATUS.generic.v1");
            ehrStatus.put("name", name);
            ehrStatus.put("subject", subject);
            ehrStatus.put("is_modifiable", true);
            ehrStatus.put("is_queryable", true);

            // EHR_CREATE_REQUEST
            LinkedHashMap<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("_type", "EHR_CREATE_REQUEST");
            requestBody.put("ehr_status", ehrStatus);

            // Log para depuración
            ObjectMapper mapper = new ObjectMapper();
            logger.debug("Request body: {}", mapper.writeValueAsString(requestBody));

            //HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(ehrStatus, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, Map.class
            );

            String locationHeader = response.getHeaders().getFirst("Location");
            if (locationHeader != null) {
                String ehrId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
                logger.info("✅ EHR creado exitosamente para '{}@{}': {}", subjectId, namespace, ehrId);
                return ehrId;
            }

            throw new RuntimeException("No se recibió Location header en la respuesta");

        } catch (Exception e) {
            logger.error("❌ Error creando EHR para paciente '{}@{}'", subjectId, namespace, e);
            throw new RuntimeException("Fallo en la creación de EHR: " + e.getMessage());
        }
    }
}
