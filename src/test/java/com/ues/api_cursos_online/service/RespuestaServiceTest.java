package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.RespuestaRequestDTO;
import com.ues.api_cursos_online.dto.RespuestaResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Leccion;
import com.ues.api_cursos_online.entity.Pregunta;
import com.ues.api_cursos_online.entity.Respuesta;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.PreguntaRepository;
import com.ues.api_cursos_online.repository.RespuestaRepository;
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
class RespuestaServiceTest {

    @Mock
    private RespuestaRepository respuestaRepository;

    @Mock
    private PreguntaRepository preguntaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RespuestaService respuestaService;

    private Usuario profesor;
    private Pregunta pregunta;
    private Respuesta respuesta;
    private RespuestaRequestDTO request;

    @BeforeEach
    void setUp() {
        Rol rolProfesor = new Rol();
        rolProfesor.setNombreRol("PROFESOR");

        profesor = new Usuario();
        profesor.setIdUsuario(2L);
        profesor.setNombreUsuario("Ana");
        profesor.setApellidoUsuario("Martínez");
        profesor.setEstado(true);
        profesor.setRol(rolProfesor);

        Curso curso = Curso.builder().idCurso(1L).nombreCurso("Desarrollo Web")
                .profesor(profesor).build();
        Leccion leccion = Leccion.builder().idLeccion(1L).titulo("Introducción")
                .curso(curso).build();

        pregunta = Pregunta.builder().idPregunta(30L).contenido("¿Qué es un elemento block?")
                .leccion(leccion).build();

        respuesta = Respuesta.builder()
                .idRespuesta(40L).pregunta(pregunta).usuario(profesor)
                .contenido("Ocupa todo el ancho disponible.").fecha(LocalDateTime.now()).build();

        request = RespuestaRequestDTO.builder()
                .idPregunta(30L).idUsuario(2L).contenido("Ocupa todo el ancho disponible.").build();
    }

    @Test
    void crear_conDatosValidos_guardaConFecha() {
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));
        when(respuestaRepository.existsByPregunta_IdPregunta(30L)).thenReturn(false);
        when(respuestaRepository.save(any(Respuesta.class))).thenAnswer(inv -> inv.getArgument(0));

        RespuestaResponseDTO dto = respuestaService.crear(request);

        assertEquals("Ocupa todo el ancho disponible.", dto.getContenido());
        assertEquals("Ana Martínez", dto.getNombreProfesor());
        assertEquals(30L, dto.getIdPregunta());
        assertNotNull(dto.getFecha());
        verify(respuestaRepository).existsByPregunta_IdPregunta(30L);
    }

    @Test
    void crear_conPreguntaInexistente_lanzaNotFound() {
        when(preguntaRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> respuestaService.crear(request));
        verify(respuestaRepository, never()).save(any());
    }

    @Test
    void crear_conUsuarioInexistente_lanzaNotFound() {
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> respuestaService.crear(request));
    }

    @Test
    void crear_conUsuarioQueNoEsProfesor_lanzaReglaDeNegocio() {
        Rol rolEstudiante = new Rol();
        rolEstudiante.setNombreRol("ESTUDIANTE");
        profesor.setRol(rolEstudiante);
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));

        assertThrows(BusinessRuleException.class, () -> respuestaService.crear(request));
        verify(respuestaRepository, never()).save(any());
    }

    @Test
    void crear_conProfesorInactivo_lanzaReglaDeNegocio() {
        profesor.setEstado(false);
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));

        assertThrows(BusinessRuleException.class, () -> respuestaService.crear(request));
    }

    @Test
    void crear_conProfesorQueNoImparteElCurso_lanzaReglaDeNegocio() {
        Usuario otroProfesor = new Usuario();
        otroProfesor.setIdUsuario(9L);
        otroProfesor.setEstado(true);
        otroProfesor.setRol(profesor.getRol());

        request.setIdUsuario(9L);
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(otroProfesor));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> respuestaService.crear(request)
        );

        assertEquals("El profesor no imparte el curso al que pertenece la pregunta",
                excepcion.getMessage());
        verify(respuestaRepository, never()).save(any());
    }

    @Test
    void crear_conPreguntaYaRespondida_lanzaReglaDeNegocio() {
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));
        when(respuestaRepository.existsByPregunta_IdPregunta(30L)).thenReturn(true);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> respuestaService.crear(request)
        );

        assertEquals("La pregunta ya tiene una respuesta", excepcion.getMessage());
        verify(respuestaRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_inexistente_lanzaNotFound() {
        when(respuestaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> respuestaService.obtenerPorId(99L));
    }

    @Test
    void listar_sinFiltros_devuelveTodas() {
        when(respuestaRepository.buscar(null, null)).thenReturn(List.of(respuesta));

        List<RespuestaResponseDTO> lista = respuestaService.listar(null, null);

        assertEquals(1, lista.size());
        verify(preguntaRepository, never()).findById(any());
    }

    @Test
    void listar_conPreguntaInexistente_lanzaNotFound() {
        when(preguntaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> respuestaService.listar(5L, null));
        verify(respuestaRepository, never()).buscar(any(), any());
    }

    @Test
    void actualizar_cambiaElContenido() {
        when(respuestaRepository.findById(40L)).thenReturn(Optional.of(respuesta));
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));
        when(respuestaRepository
                .existsByPregunta_IdPreguntaAndIdRespuestaNot(30L, 40L)).thenReturn(false);
        when(respuestaRepository.save(any(Respuesta.class))).thenAnswer(inv -> inv.getArgument(0));
        request.setContenido("Respuesta editada");

        RespuestaResponseDTO dto = respuestaService.actualizar(40L, request);

        assertEquals("Respuesta editada", dto.getContenido());
    }

    @Test
    void actualizar_conOtraRespuestaEnLaPregunta_lanzaReglaDeNegocio() {
        when(respuestaRepository.findById(40L)).thenReturn(Optional.of(respuesta));
        when(preguntaRepository.findById(30L)).thenReturn(Optional.of(pregunta));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));
        when(respuestaRepository
                .existsByPregunta_IdPreguntaAndIdRespuestaNot(30L, 40L)).thenReturn(true);

        assertThrows(BusinessRuleException.class,
                () -> respuestaService.actualizar(40L, request));
        verify(respuestaRepository, never()).save(any());
    }

    @Test
    void eliminar_existente_laBorra() {
        when(respuestaRepository.findById(40L)).thenReturn(Optional.of(respuesta));

        respuestaService.eliminar(40L);

        verify(respuestaRepository).delete(respuesta);
    }

    @Test
    void eliminar_inexistente_lanzaNotFound() {
        when(respuestaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> respuestaService.eliminar(99L));
        verify(respuestaRepository, never()).delete(any());
    }
}
