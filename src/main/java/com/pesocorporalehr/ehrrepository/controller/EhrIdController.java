package com.pesocorporalehr.ehrrepository.controller;

import com.pesocorporalehr.ehrrepository.service.impl.CreateEhrWithSubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ehr")
public class EhrIdController {

    private final CreateEhrWithSubjectService createEhrWithSubjectService;

    public EhrIdController(CreateEhrWithSubjectService createEhrWithSubjectService) {
        this.createEhrWithSubjectService = createEhrWithSubjectService;
    }

    @PostMapping("/paciente/{subjectId}/ehr")
    public ResponseEntity<?> crearEhrParaPaciente(@PathVariable String subjectId) {
        try {
            String namespace = "default";
            String ehrId = createEhrWithSubjectService.createEhrWithSubjectService(subjectId, namespace);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "ehr_id", ehrId,
                            "subject_id", subjectId,
                            "namespace", namespace,
                            "message", "EHR creado exitosamente"
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al crear EHR",
                            "message", e.getMessage()
                    ));
        }
    }
}
