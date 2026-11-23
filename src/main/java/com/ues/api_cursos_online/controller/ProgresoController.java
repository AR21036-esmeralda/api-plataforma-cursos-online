package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.ProgresoRequestDTO;
import com.ues.api_cursos_online.dto.ProgresoResponseDTO;
import com.ues.api_cursos_online.service.ProgresoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progresos")
@RequiredArgsConstructor
public class ProgresoController {

    private final ProgresoService progresoService;

    /**
     * Lista todos los registros de progreso.
     */
    @GetMapping
    public List<ProgresoResponseDTO> listar() {
        return progresoService.listar();
    }

    /**
     * Obtiene un registro de progreso por ID.
     */
    @GetMapping("/{id}")
    public ProgresoResponseDTO buscarPorId(
            @PathVariable Long id
    ) {
        return progresoService.buscarPorId(id);
    }

    /**
     * Crea un nuevo registro de progreso.
     */
    @PostMapping
    public ProgresoResponseDTO crear(
            @Valid @RequestBody ProgresoRequestDTO dto
    ) {
        return progresoService.crear(dto);
    }

    /**
     * Actualiza un registro de progreso.
     */
    @PutMapping("/{id}")
    public ProgresoResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProgresoRequestDTO dto
    ) {
        return progresoService.actualizar(id, dto);
    }

    /**
     * Elimina un registro de progreso.
     */
    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id
    ) {
        progresoService.eliminar(id);
    }

    /**
     * Obtiene todo el progreso de un estudiante.
     */
    @GetMapping("/estudiante/{idUsuario}")
    public List<ProgresoResponseDTO> listarPorEstudiante(
            @PathVariable Long idUsuario
    ) {
        return progresoService.listarPorEstudiante(idUsuario);
    }

    /**
     * Obtiene el progreso de un estudiante
     * dentro de un curso concreto.
     */
    @GetMapping(
            "/estudiante/{idUsuario}/curso/{idCurso}"
    )
    public List<ProgresoResponseDTO> listarPorEstudianteYCurso(
            @PathVariable Long idUsuario,
            @PathVariable Long idCurso
    ) {
        return progresoService.listarPorEstudianteYCurso(
                idUsuario,
                idCurso
        );
    }

    /**
     * Obtiene el progreso de un estudiante
     * en una lección concreta.
     */
    @GetMapping(
            "/estudiante/{idUsuario}/leccion/{idLeccion}"
    )
    public ProgresoResponseDTO buscarPorUsuarioYLeccion(
            @PathVariable Long idUsuario,
            @PathVariable Long idLeccion
    ) {
        return progresoService.buscarPorUsuarioYLeccion(
                idUsuario,
                idLeccion
        );
    }
}