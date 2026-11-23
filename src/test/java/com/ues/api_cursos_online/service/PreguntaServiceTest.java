package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.PreguntaRequestDTO;
import com.ues.api_cursos_online.dto.PreguntaResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Leccion;
import com.ues.api_cursos_online.entity.Pregunta;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.LeccionRepository;
import com.ues.api_cursos_online.repository.PreguntaRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreguntaServiceTest {

    @Mock
    private PreguntaRepository preguntaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LeccionRepository leccionRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private PreguntaService preguntaService;

    private Usuario estudiante;
    private Curso curso;
    private Leccion leccion;
    private Pregunta pregunta;
    private PreguntaRequestDTO request;

    @BeforeEach
    void setUp() {
        Rol rolEstudiante = new Rol();
        rolEstudiante.setNombreRol("ESTUDIANTE");

        estudiante = new Usuario();
        estudiante.setIdUsuario(4L);
        estudiante.setNombreUsuario("Lucía");
        estudiante.setApellidoUsuario("Fernández");
        estudiante.setEstado(true);
        estudiante.setRol(rolEstudiante);

        curso = Curso.builder().idCurso(1L).nombreCurso("Desarrollo Web").build();
        leccion = Leccion.builder().idLeccion(1L).titulo("Introducción a HTML y CSS")
                .curso(curso).build();

        pregunta = Pregunta.builder()
                .idPregunta(30L).usuario(estudiante).leccion(leccion)
                .contenido("¿Qué es un elemento block?").fecha(LocalDateTime.now()).build();

        request = PreguntaRequestDTO.builder()
                .idUsuario(4L).idLeccion(1L).contenido("¿Qué es un elemento block?").build();
    }

    private void inscripcionActiva() {
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(true);
    }

    @Test
    void crear_conDatosValidos_guardaConFechaYCurso() {
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();
        when(preguntaRepository.save(any(Pregunta.class))).thenAnswer(inv -> inv.getArgument(0));

        PreguntaResponseDTO respuesta = preguntaService.crear(request);

        assertEquals("¿Qué es un elemento block?", respuesta.getContenido());
        assertEquals("Lucía Fernández", respuesta.getNombreEstudiante());
        assertEquals("Introducción a HTML y CSS", respuesta.getTituloLeccion());
        assertEquals(1L, respuesta.getIdCurso());
        assertEquals("Desarrollo Web", respuesta.getNombreCurso());
        assertNotNull(respuesta.getFecha());
        verify(inscripcionRepository)
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L);
    }

    @Test
    void crear_conLeccionInexistente_lanzaNotFound() {
        when(leccionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> preguntaService.crear(request));
        verify(preguntaRepository, never()).save(any());
    }

    @Test
    void crear_conUsuarioInexistente_lanzaNotFound() {
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> preguntaService.crear(request));
    }

    @Test
    void crear_conUsuarioQueNoEsEstudiante_lanzaReglaDeNegocio() {
        Rol rolProfesor = new Rol();
        rolProfesor.setNombreRol("PROFESOR");
        estudiante.setRol(rolProfesor);
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));

        assertThrows(BusinessRuleException.class, () -> preguntaService.crear(request));
        verify(preguntaRepository, never()).save(any());
    }

    @Test
    void crear_conEstudianteInactivo_lanzaReglaDeNegocio() {
        estudiante.setEstado(false);
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));

        assertThrows(BusinessRuleException.class, () -> preguntaService.crear(request));
    }

    @Test
    void crear_sinInscripcionActiva_lanzaReglaDeNegocio() {
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(false);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> preguntaService.crear(request)
        );

        assertEquals("El estudiante no está inscrito en el curso de la lección",
                excepcion.getMessage());
        verify(preguntaRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_inexistente_lanzaNotFound() {
        when(preguntaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> preguntaService.obtenerPorId(99L));
    }

    @Test
    void listar_sinFiltros_devuelveTodas() {
        when(preguntaRepository.buscar(null, null)).thenReturn(List.of(pregunta));

        List<PreguntaResponseDTO> lista = preguntaService.listar(null, null);

        assertEquals(1, lista.size());
        assertEquals(1L, lista.get(0).getIdCurso());
        assertEquals("Desarrollo Web", lista.get(0).getNombreCurso());
        verify(leccionRepository, never()).findById(any());
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void listar_conLeccionInexistente_lanzaNotFound() {
        when(leccionRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> preguntaService.listar(5L, null));
        verify(preguntaRepository, never()).buscar(any(), any());
    }

    @Test
    void actualizar_cambiaElContenido() {
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();
        when(preguntaRepository.save(any(Pregunta.class))).thenAnswer(inv -> inv.getArgument(0));
        request.setContenido("Pregunta editada");

        PreguntaResponseDTO respuesta = preguntaService.actualizar(30L, request);

        assertEquals("Pregunta editada", respuesta.getContenido());
        assertEquals("Desarrollo Web", respuesta.getNombreCurso());
    }

    @Test
    void actualizar_sinInscripcionActiva_lanzaReglaDeNegocio() {
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> preguntaService.actualizar(30L, request));
        verify(preguntaRepository, never()).save(any());
    }

    @Test
    void eliminar_existente_laBorra() {
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));

        preguntaService.eliminar(30L);

        verify(preguntaRepository).delete(pregunta);
    }

    @Test
    void eliminar_inexistente_lanzaNotFound() {
        when(preguntaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> preguntaService.eliminar(99L));
        verify(preguntaRepository, never()).delete(any());
    }
}
