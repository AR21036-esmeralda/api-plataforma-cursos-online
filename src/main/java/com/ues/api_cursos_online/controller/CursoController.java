package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.CursoRequestDTO;
import com.ues.api_cursos_online.dto.CursoResponseDTO;
import com.ues.api_cursos_online.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Endpoints REST del recurso Curso: /api/cursos */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    /** GET /api/cursos?estado=true&idCategoria=1&idProfesor=2 (todos los filtros son opcionales) */
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listar(
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idProfesor) {
        return ResponseEntity.ok(cursoService.listar(estado, idCategoria, idProfesor));
    }

    /** GET /api/cursos/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.obtenerPorId(id));
    }

    /** POST /api/cursos */
    @PostMapping
    public ResponseEntity<CursoResponseDTO> crear(@Valid @RequestBody CursoRequestDTO dto) {
        CursoResponseDTO creado = cursoService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getIdCurso()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    /** PUT /api/cursos/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody CursoRequestDTO dto) {
        return ResponseEntity.ok(cursoService.actualizar(id, dto));
    }

    /** DELETE /api/cursos/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
