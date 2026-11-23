package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.CursoRequestDTO;
import com.ues.api_cursos_online.dto.CursoResponseDTO;
import com.ues.api_cursos_online.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Endpoints REST del recurso Curso: /api/cursos */
@RestController
@Tag(name = "Cursos", description = "CU03, CU22 - Gestión de cursos")
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
    @Operation(summary = "Crear un curso (CU03)")
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

    /** PATCH /api/cursos/{id}/profesor/{idProfesor} - asignar o cambiar profesor (CU22) */
    @Operation(summary = "Asignar o cambiar el profesor del curso (CU22)")
    @PatchMapping("/{id}/profesor/{idProfesor}")
    public ResponseEntity<CursoResponseDTO> asignarProfesor(@PathVariable Long id,
                                                            @PathVariable Long idProfesor) {
        return ResponseEntity.ok(cursoService.asignarProfesor(id, idProfesor));
    }

    /** PATCH /api/cursos/{id}/desactivar */
    @Operation(summary = "Desactivar un curso (CU22)")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<CursoResponseDTO> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.desactivar(id));
    }

    /** PATCH /api/cursos/{id}/activar */
    @Operation(summary = "Activar un curso (CU22)")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<CursoResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.activar(id));
    }

    /** DELETE /api/cursos/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
