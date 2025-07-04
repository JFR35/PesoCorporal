package com.pesocorporalehr.ehrrepository.controller;

import com.pesocorporalehr.ehrrepository.service.impl.CreateEhrWithSubjectServiceImpl;
import com.pesocorporalehr.ehrrepository.service.impl.GetAllEhrIdServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ehr")
public class GetAllEhrIdController {

    private final GetAllEhrIdServiceImpl createEhrIdService;
    private final CreateEhrWithSubjectServiceImpl createEhrWithSubjectService;

    public GetAllEhrIdController(GetAllEhrIdServiceImpl createEhrIdService, CreateEhrWithSubjectServiceImpl createEhrWithSubjectService) {
        this.createEhrIdService = createEhrIdService;
        this.createEhrWithSubjectService = createEhrWithSubjectService;
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

    @GetMapping("/{ehrId}")
    public ResponseEntity<?> getEhr(@PathVariable String ehrId) {
        try {
            String ehrDetails = createEhrIdService.getEhrDetails(ehrId);
            return ResponseEntity.ok(ehrDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("EHR no encontrado: " + e.getMessage());
        }
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

    @GetMapping("/paciente/{subjectId}/ehr")
    public ResponseEntity<?> obtenerEhrDePaciente(@PathVariable String subjectId) {
        try {
            String ehrId = createEhrIdService.getEhrBySubjectId(subjectId);
            if (ehrId != null) {
                return ResponseEntity.ok(Map.of(
                        "ehr_id", ehrId,
                        "subject_id", subjectId
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "No se encontró ningún EHR para subject_id",
                                "subject_id", subjectId
                        ));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al consultar EHR",
                            "message", e.getMessage()
                    ));
        }
    }
}
