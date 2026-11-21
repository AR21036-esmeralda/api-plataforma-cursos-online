package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.EvaluacionRequestDTO;
import com.ues.api_cursos_online.dto.EvaluacionResponseDTO;
import com.ues.api_cursos_online.service.EvaluacionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Endpoints REST del recurso Evaluación: /api/evaluaciones */
@RestController
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
@Validated
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    /** GET /api/evaluaciones?idCurso=1 (filtro opcional por curso) */
    @GetMapping
    public ResponseEntity<List<EvaluacionResponseDTO>> listar(
            @RequestParam(required = false) @Positive Long idCurso) {
        return ResponseEntity.ok(evaluacionService.listar(idCurso));
    }

    /** GET /api/evaluaciones/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionResponseDTO> obtenerPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(evaluacionService.obtenerPorId(id));
    }

    /** POST /api/evaluaciones */
    @PostMapping
    public ResponseEntity<EvaluacionResponseDTO> crear(@Valid @RequestBody EvaluacionRequestDTO dto) {
        EvaluacionResponseDTO creada = evaluacionService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getIdEvaluacion()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    /** PUT /api/evaluaciones/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionResponseDTO> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody EvaluacionRequestDTO dto) {
        return ResponseEntity.ok(evaluacionService.actualizar(id, dto));
    }

    /** DELETE /api/evaluaciones/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        evaluacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
