package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.RolRequestDTO;
import com.ues.api_cursos_online.dto.RolResponseDTO;
import com.ues.api_cursos_online.service.RolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Roles", description = "Administración de roles de usuario")
public class RolController {

    private final RolService rolService;

    @GetMapping
    public List<RolResponseDTO> listar() {
        return rolService.listar();
    }

    @GetMapping("/{id}")
    public RolResponseDTO obtenerPorId(@PathVariable @Positive Long id) {
        return rolService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolResponseDTO crear(@Valid @RequestBody RolRequestDTO dto) {
        return rolService.crear(dto);
    }

    @PutMapping("/{id}")
    public RolResponseDTO actualizar(@PathVariable @Positive Long id,
                                     @Valid @RequestBody RolRequestDTO dto) {
        return rolService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        rolService.eliminar(id);
    }
}
