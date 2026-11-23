package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.AuthRequestDTO;
import com.ues.api_cursos_online.dto.AuthResponseDTO;
import com.ues.api_cursos_online.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "CU01 - Registro y CU02 - Inicio de sesión")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión con correo y contraseña (CU02)")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión invalidando el token")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada"));
    }

    @GetMapping("/validar")
    @Operation(summary = "Validar un token emitido por el login")
    public ResponseEntity<Map<String, Object>> validar(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        Long idUsuario = authService.validarToken(token);
        return ResponseEntity.ok(Map.of(
                "valido", idUsuario != null,
                "idUsuario", idUsuario == null ? 0L : idUsuario
        ));
    }
}
