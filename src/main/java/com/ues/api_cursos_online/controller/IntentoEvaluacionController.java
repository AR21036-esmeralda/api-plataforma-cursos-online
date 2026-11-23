package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.IntentoEvaluacionRequestDTO;
import com.ues.api_cursos_online.dto.IntentoEvaluacionResponseDTO;
import com.ues.api_cursos_online.service.IntentoEvaluacionService;
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
@RequestMapping("/api/intentos-evaluacion")
@RequiredArgsConstructor
@Validated
@Tag(name = "Evaluaciones realizadas", description = "CU10 - Realizar evaluaciones")
public class IntentoEvaluacionController {

    private final IntentoEvaluacionService intentoService;

    @PostMapping("/evaluacion/{idEvaluacion}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar que un estudiante realizó una evaluación (CU10)")
    public IntentoEvaluacionResponseDTO registrar(
            @PathVariable @Positive Long idEvaluacion,
            @Valid @RequestBody IntentoEvaluacionRequestDTO dto) {
        return intentoService.registrar(idEvaluacion, dto);
    }

    @GetMapping("/estudiante/{idUsuario}")
    public List<IntentoEvaluacionResponseDTO> listarPorEstudiante(
            @PathVariable @Positive Long idUsuario) {
        return intentoService.listarPorEstudiante(idUsuario);
    }

    @GetMapping("/evaluacion/{idEvaluacion}")
    public List<IntentoEvaluacionResponseDTO> listarPorEvaluacion(
            @PathVariable @Positive Long idEvaluacion) {
        return intentoService.listarPorEvaluacion(idEvaluacion);
    }
}
