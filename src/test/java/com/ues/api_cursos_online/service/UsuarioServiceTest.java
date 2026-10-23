
package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.UsuarioRequestDTO;
import com.ues.api_cursos_online.dto.UsuarioResponseDTO;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.RolRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

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
        when(rolRepository.findById(2L))
                .thenReturn(Optional.of(rol));

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

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void crearUsuarioCuandoRolNoExisteLanzaExcepcion() {
        when(rolRepository.findById(999L))
                .thenReturn(Optional.empty());

        dto.setIdRol(999L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.crear(dto)
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
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

        when(usuarioRepository.findById(5L))
                .thenReturn(Optional.of(existente));
        when(rolRepository.findById(2L))
                .thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO resultado =
                usuarioService.actualizar(5L, dto);

        assertNotNull(resultado);
        assertEquals("Usuario", resultado.getNombreUsuario());
        assertEquals("Prueba", resultado.getApellidoUsuario());
        assertEquals("usuario.prueba@example.com", resultado.getEmail());
        assertEquals("PROFESOR", resultado.getRol());

        verify(usuarioRepository).save(existente);
    }

    @Test
    void actualizarUsuarioCuandoNoExisteLanzaExcepcion() {
        when(usuarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.actualizar(999L, dto)
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void eliminarUsuarioCuandoExiste() {
        Usuario existente = new Usuario();
        existente.setIdUsuario(5L);

        when(usuarioRepository.findById(5L))
                .thenReturn(Optional.of(existente));

        usuarioService.eliminar(5L);

        verify(usuarioRepository).deleteById(5L);
    }
}
