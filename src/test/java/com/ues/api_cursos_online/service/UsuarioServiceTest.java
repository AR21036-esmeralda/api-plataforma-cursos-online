package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.UsuarioRequestDTO;
import com.ues.api_cursos_online.dto.UsuarioResponseDTO;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.RolRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Rol rol;
    private UsuarioRequestDTO dto;

    @BeforeEach
    void prepararDatos() {
        rol = new Rol();
        rol.setIdRol(2L);
        rol.setNombreRol("PROFESOR");

        dto = new UsuarioRequestDTO();
        dto.setNombreUsuario("Usuario");
        dto.setApellidoUsuario("Prueba");
        dto.setEmail("usuario.prueba@example.com");
        dto.setPassword("ClaveTemporal123");
        dto.setEstado(true);
        dto.setIdRol(2L);
    }

    @Test
    void crearUsuarioCuandoDatosSonValidos() {
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("ClaveTemporal123")).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    usuario.setIdUsuario(10L);
                    return usuario;
                });

        UsuarioResponseDTO resultado = usuarioService.crear(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getIdUsuario());
        assertEquals("Usuario", resultado.getNombreUsuario());
        assertEquals("usuario.prueba@example.com", resultado.getEmail());
        assertEquals("PROFESOR", resultado.getRol());
        assertTrue(resultado.getEstado());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("$2a$10$hash", captor.getValue().getPassword());
        verify(passwordEncoder).encode("ClaveTemporal123");
    }

    @Test
    void crearUsuarioConCorreoDuplicadoLanzaExcepcion() {
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> usuarioService.crear(dto)
        );

        assertEquals("Ya existe un usuario con el correo " + dto.getEmail(), excepcion.getMessage());
        verify(rolRepository, never()).findById(any());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void crearUsuarioRespetaEstadoInactivoDelDto() {
        dto.setEstado(false);
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO resultado = usuarioService.crear(dto);

        assertFalse(resultado.getEstado());
    }

    @Test
    void crearUsuarioCuandoRolNoExisteLanzaExcepcion() {
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(rolRepository.findById(999L)).thenReturn(Optional.empty());

        dto.setIdRol(999L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.crear(dto)
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void obtenerPorIdCuandoExiste() {
        Usuario existente = new Usuario();
        existente.setIdUsuario(5L);
        existente.setNombreUsuario("Usuario");
        existente.setApellidoUsuario("Prueba");
        existente.setEmail("usuario.prueba@example.com");
        existente.setEstado(true);
        existente.setRol(rol);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));

        UsuarioResponseDTO resultado = usuarioService.obtenerPorId(5L);

        assertEquals(5L, resultado.getIdUsuario());
        assertEquals("PROFESOR", resultado.getRol());
        verify(usuarioRepository).findById(5L);
    }

    @Test
    void obtenerPorIdCuandoNoExisteLanzaExcepcion() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.obtenerPorId(999L)
        );
    }

    @Test
    void actualizarUsuarioCuandoExiste() {
        Usuario existente = new Usuario();
        existente.setIdUsuario(5L);
        existente.setNombreUsuario("Nombre anterior");
        existente.setApellidoUsuario("Apellido anterior");
        existente.setEmail("anterior@example.com");
        existente.setPassword("ClaveAnterior");
        existente.setEstado(true);
        existente.setRol(rol);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("ClaveTemporal123")).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO resultado = usuarioService.actualizar(5L, dto);

        assertNotNull(resultado);
        assertEquals("Usuario", resultado.getNombreUsuario());
        assertEquals("Prueba", resultado.getApellidoUsuario());
        assertEquals("usuario.prueba@example.com", resultado.getEmail());
        assertEquals("PROFESOR", resultado.getRol());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("$2a$10$hash", captor.getValue().getPassword());
    }

    @Test
    void actualizarUsuarioConPasswordEnBlancoNoLaModifica() {
        Usuario existente = new Usuario();
        existente.setIdUsuario(5L);
        existente.setNombreUsuario("Nombre anterior");
        existente.setApellidoUsuario("Apellido anterior");
        existente.setEmail("anterior@example.com");
        existente.setPassword("ClaveAnterior");
        existente.setEstado(true);
        existente.setRol(rol);

        dto.setPassword("   ");
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.actualizar(5L, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("ClaveAnterior", captor.getValue().getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void actualizarUsuarioConCorreoDeOtroUsuarioLanzaExcepcion() {
        Usuario existente = new Usuario();
        existente.setIdUsuario(5L);
        existente.setRol(rol);

        Usuario otro = new Usuario();
        otro.setIdUsuario(6L);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(otro));

        assertThrows(
                BusinessRuleException.class,
                () -> usuarioService.actualizar(5L, dto)
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuarioCuandoNoExisteLanzaExcepcion() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.actualizar(999L, dto)
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void desactivarUsuarioCambiaElEstado() {
        Usuario existente = new Usuario();
        existente.setIdUsuario(5L);
        existente.setEstado(true);
        existente.setRol(rol);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO resultado = usuarioService.desactivar(5L);

        assertFalse(resultado.getEstado());
        verify(usuarioRepository).save(existente);
    }

    @Test
    void reactivarUsuarioCambiaElEstado() {
        Usuario existente = new Usuario();
        existente.setIdUsuario(5L);
        existente.setEstado(false);
        existente.setRol(rol);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO resultado = usuarioService.reactivar(5L);

        assertTrue(resultado.getEstado());
        verify(usuarioRepository).save(existente);
    }

    @Test
    void eliminarUsuarioCuandoExiste() {
        Usuario existente = new Usuario();
        existente.setIdUsuario(5L);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));

        usuarioService.eliminar(5L);

        verify(usuarioRepository).deleteById(5L);
    }
}
