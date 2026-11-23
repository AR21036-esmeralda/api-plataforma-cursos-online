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
import org.mockito.ArgumentCaptor;
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
        categoria.setNombreCategoria("Tecnología");
        categoria.setEstado(true);

        Rol rol = new Rol();
        rol.setNombreRol("PROFESOR");

        profesor = new Usuario();
        profesor.setIdUsuario(2L);
        profesor.setNombreUsuario("Ana");
        profesor.setApellidoUsuario("Martínez");
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
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));
        when(cursoRepository.save(any(Curso.class)))
                .thenAnswer(invocation -> {
                    Curso curso = invocation.getArgument(0);
                    curso.setIdCurso(10L);
                    return curso;
                });

        CursoResponseDTO respuesta = new CursoResponseDTO();
        when(cursoMapper.toResponse(any(Curso.class))).thenReturn(respuesta);

        CursoResponseDTO resultado = cursoService.crear(dto);

        assertNotNull(resultado);
        verify(cursoRepository).save(any(Curso.class));
        verify(cursoMapper).toResponse(any(Curso.class));
    }

    @Test
    void obtenerCursoCuandoNoExisteLanzaExcepcion() {
        when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

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

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));

        assertThrows(
                BusinessRuleException.class,
                () -> cursoService.crear(dto)
        );

        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void crearCursoCuandoProfesorEstaDesactivadoLanzaExcepcion() {
        profesor.setEstado(false);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));

        assertThrows(
                BusinessRuleException.class,
                () -> cursoService.crear(dto)
        );

        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void crearCursoCuandoCategoriaEstaDesactivadaLanzaExcepcion() {
        categoria.setEstado(false);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThrows(
                BusinessRuleException.class,
                () -> cursoService.crear(dto)
        );

        verify(usuarioRepository, never()).findById(any());
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void asignarProfesorCambiaElProfesorDelCurso() {
        Curso curso = Curso.builder()
                .idCurso(10L)
                .nombreCurso("Curso de prueba")
                .estado(true)
                .categoria(categoria)
                .profesor(profesor)
                .build();

        Usuario nuevoProfesor = new Usuario();
        nuevoProfesor.setIdUsuario(3L);
        nuevoProfesor.setEstado(true);
        nuevoProfesor.setRol(profesor.getRol());

        when(cursoRepository.findById(10L)).thenReturn(Optional.of(curso));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(nuevoProfesor));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cursoMapper.toResponse(any(Curso.class))).thenReturn(new CursoResponseDTO());

        cursoService.asignarProfesor(10L, 3L);

        ArgumentCaptor<Curso> captor = ArgumentCaptor.forClass(Curso.class);
        verify(cursoRepository).save(captor.capture());
        assertEquals(3L, captor.getValue().getProfesor().getIdUsuario());
    }

    @Test
    void desactivarCursoCambiaElEstado() {
        Curso curso = Curso.builder()
                .idCurso(10L)
                .estado(true)
                .categoria(categoria)
                .profesor(profesor)
                .build();

        when(cursoRepository.findById(10L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cursoMapper.toResponse(any(Curso.class))).thenReturn(new CursoResponseDTO());

        cursoService.desactivar(10L);

        ArgumentCaptor<Curso> captor = ArgumentCaptor.forClass(Curso.class);
        verify(cursoRepository).save(captor.capture());
        assertFalse(captor.getValue().getEstado());
    }

    @Test
    void activarCursoCambiaElEstado() {
        Curso curso = Curso.builder()
                .idCurso(10L)
                .estado(false)
                .categoria(categoria)
                .profesor(profesor)
                .build();

        when(cursoRepository.findById(10L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cursoMapper.toResponse(any(Curso.class))).thenReturn(new CursoResponseDTO());

        cursoService.activar(10L);

        ArgumentCaptor<Curso> captor = ArgumentCaptor.forClass(Curso.class);
        verify(cursoRepository).save(captor.capture());
        assertTrue(captor.getValue().getEstado());
    }
}
