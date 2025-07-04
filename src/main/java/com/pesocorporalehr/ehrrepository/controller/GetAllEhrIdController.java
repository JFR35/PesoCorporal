package com.pesocorporalehr.ehrrepository.controller;

import com.pesocorporalehr.ehrrepository.service.impl.GetAllEhrIdServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ehr")
public class GetAllEhrIdController {

    private final GetAllEhrIdServiceImpl createEhrIdService;

    public GetAllEhrIdController(GetAllEhrIdServiceImpl createEhrIdService) {
        this.createEhrIdService = createEhrIdService;
    }

    @GetMapping
    public ResponseEntity<?> listAllEhrs() {
        try {
            List<String> ehrIds = createEhrIdService.getAllEhrIds();
            return ResponseEntity.ok(Map.of("ehrIds", ehrIds));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al listar EHRs", "details", e.getMessage()));
        }
    }
}
