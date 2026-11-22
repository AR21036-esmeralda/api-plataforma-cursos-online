package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.PreguntaRequestDTO;
import com.ues.api_cursos_online.dto.PreguntaResponseDTO;
import com.ues.api_cursos_online.dto.RespuestaResponseDTO;
import com.ues.api_cursos_online.service.PreguntaService;
import com.ues.api_cursos_online.service.RespuestaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Endpoints REST del recurso Pregunta: /api/preguntas */
@RestController
@RequestMapping("/api/preguntas")
@RequiredArgsConstructor
@Validated
public class PreguntaController {

    private final PreguntaService preguntaService;
    private final RespuestaService respuestaService;

    /** GET /api/preguntas?idLeccion=1&idUsuario=4 (filtros opcionales) */
    @GetMapping
    public ResponseEntity<List<PreguntaResponseDTO>> listar(
            @RequestParam(required = false) @Positive Long idLeccion,
            @RequestParam(required = false) @Positive Long idUsuario) {
        return ResponseEntity.ok(preguntaService.listar(idLeccion, idUsuario));
    }

    /** GET /api/preguntas/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<PreguntaResponseDTO> obtenerPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(preguntaService.obtenerPorId(id));
    }

    /** GET /api/preguntas/{id}/respuestas : respuestas de una pregunta */
    @GetMapping("/{id}/respuestas")
    public ResponseEntity<List<RespuestaResponseDTO>> respuestasDeLaPregunta(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(respuestaService.listar(id, null));
    }

    /** POST /api/preguntas */
    @PostMapping
    public ResponseEntity<PreguntaResponseDTO> crear(@Valid @RequestBody PreguntaRequestDTO dto) {
        PreguntaResponseDTO creada = preguntaService.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getIdPregunta()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    /** PUT /api/preguntas/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<PreguntaResponseDTO> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody PreguntaRequestDTO dto) {
        return ResponseEntity.ok(preguntaService.actualizar(id, dto));
    }

    /** DELETE /api/preguntas/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        preguntaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
