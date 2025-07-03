package com.pesocorporalehr.ehrrepository.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class TemplateUploaderService {

    private final Logger logger = LoggerFactory.getLogger(TemplateUploaderService.class);

    private final RestTemplate restTemplate;

    @Value("${ehrbase.url}")
    private String ehrBaseUrl;

    @Value("${ehrbase.template-upload-url}")
    private String templateUploadUrl;

    public TemplateUploaderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource folder = new ClassPathResource("templates");

            File[] files = folder.getFile().listFiles((dir, name) -> name.endsWith(".opt"));

            if (files == null || files.length == 0) {
                logger.warn("No se encontraron plantillas .opt en el directorio /resources/templates");
                return;
            }

            int subidas = 0;
            int existentes = 0;

            for (File resource : files) {
                String templateName = resource.getName();
                String templateId = templateName.replace(".opt", "");

                if (!templateExists(templateId)) {
                    logger.info("Subiendo plantilla: {}", templateId);
                    uploadTemplateFromResources(templateName, templateId);
                    subidas++;
                } else {
                    logger.info("La plantilla '{}' ya existe en EHRbase, no se volverá a subir.", templateId);
                    existentes++;
                }
            }

            logger.info("Resumen: {} plantillas subidas, {} ya existentes.", subidas, existentes);

        } catch (IOException e) {
            logger.error("Error leyendo el directorio de plantillas: {}", e.getMessage());
        }
    }

    public void uploadTemplateFromResources(String templateName, String templateId) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + templateName);
            String xml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML); // El interceptor ya añade Authorization y Accept

            HttpEntity<String> request = new HttpEntity<>(xml, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    templateUploadUrl,
                    request,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Plantilla '{}' subida con éxito.", templateId);
            } else {
                logger.warn("Código inesperado al subir plantilla '{}': {}", templateId, response.getStatusCode());
            }

        } catch (HttpClientErrorException.Conflict e) {
            logger.info("La plantilla '{}' ya existe (409 Conflict)", templateId);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer la plantilla desde resources: " + templateName, e);
        }
    }

    private boolean templateExists(String templateId) {
        logger.info("Comprobando si la plantilla .opt existe: {}", templateId);
        String checkUrl = ehrBaseUrl + "/rest/openehr/v1/definition/template/adl1.4/" + templateId;

        try {
            HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders()); // interceptor añade auth

            ResponseEntity<String> response = restTemplate.exchange(
                    checkUrl,
                    HttpMethod.GET,
                    request,
                    String.class);

            boolean exists = response.getStatusCode().is2xxSuccessful();
            if (exists) logger.debug("La plantilla '{}' existe en EHRbase.", templateId);
            return exists;

        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            logger.error("Error verificando existencia de plantilla '{}': {}", templateId, e.getMessage());
            return false;
        }
    }
}
