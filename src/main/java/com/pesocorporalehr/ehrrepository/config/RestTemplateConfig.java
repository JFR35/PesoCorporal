package com.pesocorporalehr.ehrrepository.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    @Value("${ehrbase.username}")
    private String ehrBaseUsername;

    @Value("${ehrbase.password}")
    private String ehrBasePassword;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Configurar los message converters
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        // Converter principal para JSON
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));

        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);

        // Añadir interceptor de autenticación
        restTemplate.setInterceptors(List.of(authInterceptor()));

        return restTemplate;
    }

    private ClientHttpRequestInterceptor authInterceptor() {
        return (request, body, execution) -> {
            String auth = ehrBaseUsername + ":" + ehrBasePassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            request.getHeaders().add("Authorization", "Basic " + encodedAuth);
            request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));

            return execution.execute(request, body);
        };
    }
}
