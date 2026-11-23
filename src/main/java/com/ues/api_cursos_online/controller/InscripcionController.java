package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.InscripcionRequestDTO;
import com.ues.api_cursos_online.dto.InscripcionResponseDTO;
import com.ues.api_cursos_online.service.InscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;

    /**
     * Lista todas las inscripciones.
     */
    @GetMapping
    public List<InscripcionResponseDTO> listar() {
        return inscripcionService.listar();
    }

    /**
     * Obtiene una inscripción por ID.
     */
    @GetMapping("/{id}")
    public InscripcionResponseDTO buscarPorId(@PathVariable Long id) {
        return inscripcionService.buscarPorId(id);
    }

    /**
     * Crea una nueva inscripción.
     */
    @PostMapping
    public InscripcionResponseDTO crear(
            @Valid @RequestBody InscripcionRequestDTO dto
    ) {
        return inscripcionService.crear(dto);
    }

    /**
     * Actualiza una inscripción.
     */
    @PutMapping("/{id}")
    public InscripcionResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InscripcionRequestDTO dto
    ) {
        return inscripcionService.actualizar(id, dto);
    }

    /**
     * Elimina una inscripción.
     */
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        inscripcionService.eliminar(id);
    }

    /**
     * Obtiene los cursos de un estudiante.
     */
    @GetMapping("/estudiante/{idUsuario}/cursos")
    public List<InscripcionResponseDTO> listarCursosDeEstudiante(
            @PathVariable Long idUsuario
    ) {
        return inscripcionService.listarPorEstudiante(idUsuario);
    }

    /**
     * Obtiene los estudiantes inscritos en un curso.
     */
    @GetMapping("/curso/{idCurso}/estudiantes")
    public List<InscripcionResponseDTO> listarEstudiantesDeCurso(
            @PathVariable Long idCurso
    ) {
        return inscripcionService.listarPorCurso(idCurso);
    }
}
