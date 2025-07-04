package com.pesocorporalehr.ehrrepository.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/templates/*.opt");

            if (resources.length == 0) {
                logger.warn("No se encontraron plantillas .opt en el classpath:/templates");
                return;
            }

            int subidas = 0;
            int existentes = 0;

            for (Resource resource : resources) {
                String templateName = resource.getFilename();
                if (templateName == null || !templateName.endsWith(".opt")) continue;

                String templateId = templateName.replace(".opt", "");

                if (!templateExists(templateId)) {
                    logger.info("Subiendo plantilla: {}", templateId);
                    uploadTemplateFromResource(resource, templateId);
                    subidas++;
                } else {
                    logger.info("La plantilla '{}' ya existe en EHRbase, no se volverá a subir.", templateId);
                    existentes++;
                }
            }

            logger.info("Resumen: {} plantillas subidas, {} ya existentes.", subidas, existentes);

        } catch (IOException e) {
            logger.error("Error leyendo las plantillas .opt desde classpath:/templates: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al subir plantillas: {}", e.getMessage(), e);
        }
    }

    public void uploadTemplateFromResource(Resource resource, String templateId) {
        try {
            String xml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);

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
            logger.error("No se pudo leer la plantilla '{}': {}", templateId, e.getMessage(), e);
        }
    }

    private boolean templateExists(String templateId) {
        logger.debug("Comprobando si existe plantilla '{}'", templateId);
        String checkUrl = ehrBaseUrl + "/definition/template/adl1.4/" + templateId;

        try {
            HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    checkUrl,
                    HttpMethod.GET,
                    request,
                    String.class);

            boolean exists = response.getStatusCode().is2xxSuccessful();
            if (exists) logger.debug("Plantilla '{}' encontrada en EHRbase.", templateId);
            return exists;

        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            logger.error("Error al comprobar existencia de '{}': {}", templateId, e.getMessage(), e);
            return false;
        }
    }
}
