package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.UsuarioRequestDTO;
import com.ues.api_cursos_online.dto.UsuarioResponseDTO;
import com.ues.api_cursos_online.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Usuarios", description = "CU01, CU21 - Gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un usuario por id")
    public UsuarioResponseDTO obtenerPorId(@PathVariable @Positive Long id) {
        return usuarioService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar usuario (CU01)")
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.crear(dto);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizar(@PathVariable @Positive Long id,
                                         @Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.actualizar(id, dto);
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un usuario (CU21)")
    public UsuarioResponseDTO desactivar(@PathVariable @Positive Long id) {
        return usuarioService.desactivar(id);
    }

    @PatchMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar un usuario (CU21)")
    public UsuarioResponseDTO reactivar(@PathVariable @Positive Long id) {
        return usuarioService.reactivar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        usuarioService.eliminar(id);
    }
}
