package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.CalificacionRequestDTO;
import com.ues.api_cursos_online.dto.CalificacionResponseDTO;
import com.ues.api_cursos_online.service.CalificacionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Endpoints REST del recurso Calificación: /api/calificaciones */
@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
@Validated
public class CalificacionController {

    private final CalificacionService calificacionService;

    /** GET /api/calificaciones?idUsuario=4&idEvaluacion=1 (filtros opcionales) */
    @GetMapping
    public ResponseEntity<List<CalificacionResponseDTO>> listar(
            @RequestParam(required = false) @Positive Long idUsuario,
            @RequestParam(required = false) @Positive Long idEvaluacion) {
        return ResponseEntity.ok(calificacionService.listar(idUsuario, idEvaluacion));
    }

    /** GET /api/calificaciones/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<CalificacionResponseDTO> obtenerPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(calificacionService.obtenerPorId(id));
    }

    /** POST /api/calificaciones */
    @PostMapping
    public ResponseEntity<CalificacionResponseDTO> crear(@Valid @RequestBody CalificacionRequestDTO dto) {
        CalificacionResponseDTO creada = calificacionService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getIdCalificacion()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    /** PUT /api/calificaciones/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<CalificacionResponseDTO> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CalificacionRequestDTO dto) {
        return ResponseEntity.ok(calificacionService.actualizar(id, dto));
    }

    /** DELETE /api/calificaciones/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        calificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
