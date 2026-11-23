package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.InscripcionRequestDTO;
import com.ues.api_cursos_online.dto.InscripcionResponseDTO;
import com.ues.api_cursos_online.service.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
@Validated
@Tag(name = "Inscripciones", description = "CU05, CU06, CU13, CU19 - Relación estudiante-curso")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @GetMapping
    public List<InscripcionResponseDTO> listar() {
        return inscripcionService.listar();
    }

    @GetMapping("/{id}")
    public InscripcionResponseDTO buscarPorId(@PathVariable @Positive Long id) {
        return inscripcionService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Inscribirse en un curso (CU05)")
    public InscripcionResponseDTO crear(@Valid @RequestBody InscripcionRequestDTO dto) {
        return inscripcionService.crear(dto);
    }

    @PutMapping("/{id}")
    public InscripcionResponseDTO actualizar(@PathVariable @Positive Long id,
                                             @Valid @RequestBody InscripcionRequestDTO dto) {
        return inscripcionService.actualizar(id, dto);
    }

    @PatchMapping("/{id}/baja")
    @Operation(summary = "Darse de baja de un curso (CU13)")
    public InscripcionResponseDTO darDeBaja(@PathVariable @Positive Long id) {
        return inscripcionService.darDeBaja(id);
    }

    @PatchMapping("/{id}/reactivar")
    public InscripcionResponseDTO reactivar(@PathVariable @Positive Long id) {
        return inscripcionService.reactivar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        inscripcionService.eliminar(id);
    }

    @GetMapping("/estudiante/{idUsuario}/cursos")
    @Operation(summary = "Consultar los cursos inscritos de un estudiante (CU06)")
    public List<InscripcionResponseDTO> listarCursosDeEstudiante(
            @PathVariable @Positive Long idUsuario,
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivas) {
        return inscripcionService.listarPorEstudiante(idUsuario, incluirInactivas);
    }

    @GetMapping("/curso/{idCurso}/estudiantes")
    @Operation(summary = "Consultar los estudiantes inscritos en un curso (CU19)")
    public List<InscripcionResponseDTO> listarEstudiantesDeCurso(
            @PathVariable @Positive Long idCurso,
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivas) {
        return inscripcionService.listarPorCurso(idCurso, incluirInactivas);
    }
}
