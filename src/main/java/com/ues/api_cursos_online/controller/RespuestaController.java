package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.RespuestaRequestDTO;
import com.ues.api_cursos_online.dto.RespuestaResponseDTO;
import com.ues.api_cursos_online.service.RespuestaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Endpoints REST del recurso Respuesta: /api/respuestas */
@RestController
@Tag(name = "Respuestas", description = "CU20 - Respuestas de los profesores")
@RequestMapping("/api/respuestas")
@RequiredArgsConstructor
@Validated
public class RespuestaController {

    private final RespuestaService respuestaService;

    /** GET /api/respuestas?idPregunta=1&idUsuario=2 (filtros opcionales) */
    @GetMapping
    public ResponseEntity<List<RespuestaResponseDTO>> listar(
            @RequestParam(required = false) @Positive Long idPregunta,
            @RequestParam(required = false) @Positive Long idUsuario) {
        return ResponseEntity.ok(respuestaService.listar(idPregunta, idUsuario));
    }

    /** GET /api/respuestas/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaResponseDTO> obtenerPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(respuestaService.obtenerPorId(id));
    }

    /** POST /api/respuestas */
    @Operation(summary = "Responder una pregunta (CU20)")
    @PostMapping
    public ResponseEntity<RespuestaResponseDTO> crear(@Valid @RequestBody RespuestaRequestDTO dto) {
        RespuestaResponseDTO creada = respuestaService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getIdRespuesta()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    /** PUT /api/respuestas/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaResponseDTO> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody RespuestaRequestDTO dto) {
        return ResponseEntity.ok(respuestaService.actualizar(id, dto));
    }

    /** DELETE /api/respuestas/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        respuestaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
