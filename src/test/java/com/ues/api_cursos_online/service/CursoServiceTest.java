
package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.CursoRequestDTO;
import com.ues.api_cursos_online.dto.CursoResponseDTO;
import com.ues.api_cursos_online.entity.Categoria;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.mapper.CursoMapper;
import com.ues.api_cursos_online.repository.CategoriaRepository;
import com.ues.api_cursos_online.repository.CursoRepository;
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
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CursoMapper cursoMapper;

    @InjectMocks
    private CursoService cursoService;

    private Categoria categoria;
    private Usuario profesor;
    private CursoRequestDTO dto;

    @BeforeEach
    void prepararDatos() {
        categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setEstado(true);

        Rol rol = new Rol();
        rol.setNombreRol("PROFESOR");

        profesor = new Usuario();
        profesor.setIdUsuario(2L);
        profesor.setEstado(true);
        profesor.setRol(rol);

        dto = new CursoRequestDTO();
        dto.setNombreCurso(" Curso de prueba ");
        dto.setDescripcion("Descripción de prueba");
        dto.setEstado(true);
        dto.setIdCategoria(1L);
        dto.setIdProfesor(2L);
    }

    @Test
    void crearCursoCuandoDatosSonValidos() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(2L))
                .thenReturn(Optional.of(profesor));

        when(cursoRepository.save(any(Curso.class)))
                .thenAnswer(invocation -> {
                    Curso curso = invocation.getArgument(0);
                    curso.setIdCurso(10L);
                    return curso;
                });

        CursoResponseDTO respuesta = new CursoResponseDTO();
        when(cursoMapper.toResponse(any(Curso.class)))
                .thenReturn(respuesta);

        CursoResponseDTO resultado = cursoService.crear(dto);

        assertNotNull(resultado);
        verify(cursoRepository).save(any(Curso.class));
        verify(cursoMapper).toResponse(any(Curso.class));
    }

    @Test
    void obtenerCursoCuandoNoExisteLanzaExcepcion() {
        when(cursoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> cursoService.obtenerPorId(999L)
        );
    }

    @Test
    void crearCursoCuandoUsuarioNoEsProfesorLanzaExcepcion() {
        Rol rolEstudiante = new Rol();
        rolEstudiante.setNombreRol("ESTUDIANTE");
        profesor.setRol(rolEstudiante);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(2L))
                .thenReturn(Optional.of(profesor));

        assertThrows(
                BusinessRuleException.class,
                () -> cursoService.crear(dto)
        );

        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void crearCursoCuandoCategoriaEstaDesactivadaLanzaExcepcion() {
        categoria.setEstado(false);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        assertThrows(
                BusinessRuleException.class,
                () -> cursoService.crear(dto)
        );

        verify(usuarioRepository, never()).findById(any());
        verify(cursoRepository, never()).save(any(Curso.class));
    }
}
