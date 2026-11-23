package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.AuthRequestDTO;
import com.ues.api_cursos_online.dto.AuthResponseDTO;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private AuthRequestDTO request;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setNombreRol("ESTUDIANTE");

        usuario = new Usuario();
        usuario.setIdUsuario(4L);
        usuario.setNombreUsuario("Lucía");
        usuario.setApellidoUsuario("Fernández");
        usuario.setEmail("lucia@test.com");
        usuario.setPassword("$2a$10$hash");
        usuario.setEstado(true);
        usuario.setRol(rol);

        request = new AuthRequestDTO("lucia@test.com", "Clave123");
    }

    @Test
    void login_CredencialesValidas_DevuelveTokenYDatosDelUsuario() {
        when(usuarioRepository.findByEmail("lucia@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Clave123", "$2a$10$hash")).thenReturn(true);

        AuthResponseDTO respuesta = authService.login(request);

        assertNotNull(respuesta);
        assertNotNull(respuesta.getToken());
        assertEquals(4L, respuesta.getIdUsuario());
        assertEquals("Lucía Fernández", respuesta.getNombreUsuario());
        assertEquals("lucia@test.com", respuesta.getEmail());
        assertEquals("ESTUDIANTE", respuesta.getRol());
        assertTrue(respuesta.getEstado());
        assertEquals("Inicio de sesión correcto", respuesta.getMensaje());
        verify(usuarioRepository).findByEmail("lucia@test.com");
        verify(passwordEncoder).matches("Clave123", "$2a$10$hash");
    }

    @Test
    void login_UsuarioInexistente_LanzaCredencialesInvalidas() {
        when(usuarioRepository.findByEmail("lucia@test.com")).thenReturn(Optional.empty());

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> authService.login(request)
        );

        assertEquals("Credenciales inválidas", excepcion.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void login_PasswordInvalido_LanzaCredencialesInvalidas() {
        when(usuarioRepository.findByEmail("lucia@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Clave123", "$2a$10$hash")).thenReturn(false);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> authService.login(request)
        );

        assertEquals("Credenciales inválidas", excepcion.getMessage());
    }

    @Test
    void login_UsuarioDesactivado_LanzaReglaDeNegocio() {
        usuario.setEstado(false);
        when(usuarioRepository.findByEmail("lucia@test.com")).thenReturn(Optional.of(usuario));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> authService.login(request)
        );

        assertEquals("El usuario está desactivado", excepcion.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void validarToken_DespuesDelLogin_DevuelveElIdDelUsuario() {
        when(usuarioRepository.findByEmail("lucia@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Clave123", "$2a$10$hash")).thenReturn(true);

        AuthResponseDTO respuesta = authService.login(request);

        assertEquals(4L, authService.validarToken(respuesta.getToken()));
    }

    @Test
    void validarToken_TokenDesconocidoONulo_DevuelveNull() {
        assertNull(authService.validarToken(null));
        assertNull(authService.validarToken("token-inexistente"));
    }

    @Test
    void logout_InvalidaElTokenEmitido() {
        when(usuarioRepository.findByEmail("lucia@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Clave123", "$2a$10$hash")).thenReturn(true);

        AuthResponseDTO respuesta = authService.login(request);
        String token = respuesta.getToken();
        assertEquals(4L, authService.validarToken(token));

        authService.logout(token);

        assertNull(authService.validarToken(token));
    }

    @Test
    void logout_ConTokenDesconocido_NoLanzaExcepcion() {
        assertNull(authService.validarToken("token-inexistente"));
        assertDoesNotThrow(() -> authService.logout("token-inexistente"));
    }
}
