package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.AuthRequestDTO;
import com.ues.api_cursos_online.dto.AuthResponseDTO;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Implementación del caso de uso CU02 - Iniciar sesión. */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /** Tokens emitidos por el login (en memoria, suficiente para la defensa). */
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public AuthResponseDTO login(AuthRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new BusinessRuleException("El usuario está desactivado");
        }

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        String token = UUID.randomUUID().toString();
        tokens.put(token, usuario.getIdUsuario());

        return AuthResponseDTO.builder()
                .token(token)
                .idUsuario(usuario.getIdUsuario())
                .nombreUsuario(usuario.getNombreUsuario() + " " + usuario.getApellidoUsuario())
                .email(usuario.getEmail())
                .rol(usuario.getRol().getNombreRol())
                .estado(usuario.getEstado())
                .mensaje("Inicio de sesión correcto")
                .build();
    }

    /** Devuelve el id del usuario dueño del token o null si no es válido. */
    public Long validarToken(String token) {
        return token == null ? null : tokens.get(token);
    }

    public void logout(String token) {
        tokens.remove(token);
    }
}
