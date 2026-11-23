package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.ProgresoRequestDTO;
import com.ues.api_cursos_online.dto.ProgresoResponseDTO;
import com.ues.api_cursos_online.service.ProgresoService;
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
@RequestMapping("/api/progresos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Progreso", description = "CU07, CU08, CU09 - Seguimiento de lecciones")
public class ProgresoController {

    private final ProgresoService progresoService;

    @GetMapping
    public List<ProgresoResponseDTO> listar() {
        return progresoService.listar();
    }

    @GetMapping("/{id}")
    public ProgresoResponseDTO buscarPorId(@PathVariable @Positive Long id) {
        return progresoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar o marcar progreso de una lección (CU08)")
    public ProgresoResponseDTO crear(@Valid @RequestBody ProgresoRequestDTO dto) {
        return progresoService.crear(dto);
    }

    @PutMapping("/{id}")
    public ProgresoResponseDTO actualizar(@PathVariable @Positive Long id,
                                          @Valid @RequestBody ProgresoRequestDTO dto) {
        return progresoService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        progresoService.eliminar(id);
    }

    @GetMapping("/estudiante/{idUsuario}")
    @Operation(summary = "Consultar el progreso de un estudiante (CU09)")
    public List<ProgresoResponseDTO> listarPorEstudiante(@PathVariable @Positive Long idUsuario) {
        return progresoService.listarPorEstudiante(idUsuario);
    }

    @GetMapping("/estudiante/{idUsuario}/curso/{idCurso}")
    public List<ProgresoResponseDTO> listarPorEstudianteYCurso(
            @PathVariable @Positive Long idUsuario,
            @PathVariable @Positive Long idCurso) {
        return progresoService.listarPorEstudianteYCurso(idUsuario, idCurso);
    }

    @GetMapping("/estudiante/{idUsuario}/leccion/{idLeccion}")
    public ProgresoResponseDTO buscarPorUsuarioYLeccion(
            @PathVariable @Positive Long idUsuario,
            @PathVariable @Positive Long idLeccion) {
        return progresoService.buscarPorUsuarioYLeccion(idUsuario, idLeccion);
    }
}
