package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.LeccionRequestDTO;
import com.ues.api_cursos_online.dto.LeccionResponseDTO;
import com.ues.api_cursos_online.service.LeccionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Endpoints REST del recurso Lección: /api/lecciones. */
@RestController
@RequestMapping("/api/lecciones")
@RequiredArgsConstructor
@Validated
public class LeccionController {

    private final LeccionService leccionService;

    /** GET /api/lecciones? idCurso=1 (filtro opcional por curso). */
    @GetMapping
    public ResponseEntity<List<LeccionResponseDTO>> listar(
            @RequestParam(required = false) @Positive Long idCurso) {
        return ResponseEntity.ok(leccionService.listar(idCurso));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeccionResponseDTO> obtenerPorId(
            @PathVariable @Positive Long id) {
        return ResponseEntity.ok(leccionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<LeccionResponseDTO> crear(
            @Valid @RequestBody LeccionRequestDTO dto) {
        LeccionResponseDTO creado = leccionService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getIdLeccion()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeccionResponseDTO> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody LeccionRequestDTO dto) {
        return ResponseEntity.ok(leccionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        leccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
