package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.EvaluacionResponseDTO;
import com.ues.api_cursos_online.dto.IntentoEvaluacionRequestDTO;
import com.ues.api_cursos_online.dto.IntentoEvaluacionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Evaluacion;
import com.ues.api_cursos_online.entity.IntentoEvaluacion;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.EvaluacionRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.IntentoEvaluacionRepository;
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
class IntentoEvaluacionServiceTest {

    @Mock
    private IntentoEvaluacionRepository intentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @Mock
    private EvaluacionService evaluacionService;

    @InjectMocks
    private IntentoEvaluacionService intentoEvaluacionService;

    private Usuario estudiante;
    private Evaluacion evaluacion;
    private EvaluacionResponseDTO evaluacionValidada;
    private IntentoEvaluacionRequestDTO request;
    private IntentoEvaluacion intento;

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

        Curso curso = Curso.builder().idCurso(1L).nombreCurso("Desarrollo Web")
                .estado(true).build();
        evaluacion = Evaluacion.builder()
                .idEvaluacion(10L).titulo("Evaluación Final")
                .fechaLimite(LocalDateTime.now().plusDays(2))
                .curso(curso).build();
        evaluacionValidada = EvaluacionResponseDTO.builder()
                .idEvaluacion(10L).titulo("Evaluación Final").idCurso(1L).build();

        request = new IntentoEvaluacionRequestDTO(4L);
        intento = IntentoEvaluacion.builder()
                .idIntento(50L).usuario(estudiante).evaluacion(evaluacion)
                .fechaRealizacion(LocalDateTime.now()).build();
    }

    private void datosValidos() {
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(true);
        when(evaluacionService.validarDisponible(10L, 4L, true))
                .thenReturn(evaluacionValidada);
    }

    @Test
    void registrar_ConDatosValidos_GuardaIntentoYDevuelveRespuesta() {
        datosValidos();
        when(intentoRepository
                .existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(4L, 10L))
                .thenReturn(false);
        when(intentoRepository.save(any(IntentoEvaluacion.class)))
                .thenAnswer(inv -> {
                    IntentoEvaluacion guardado = inv.getArgument(0);
                    guardado.setIdIntento(50L);
                    return guardado;
                });

        IntentoEvaluacionResponseDTO respuesta =
                intentoEvaluacionService.registrar(10L, request);

        assertNotNull(respuesta);
        assertEquals(50L, respuesta.getIdIntento());
        assertEquals(4L, respuesta.getIdUsuario());
        assertEquals("Lucía Fernández", respuesta.getNombreEstudiante());
        assertEquals(10L, respuesta.getIdEvaluacion());
        assertEquals("Evaluación Final", respuesta.getTituloEvaluacion());
        assertNotNull(respuesta.getFechaRealizacion());
        assertEquals("Evaluación registrada correctamente", respuesta.getMensaje());
        verify(evaluacionService).validarDisponible(10L, 4L, true);
        verify(intentoRepository).save(any(IntentoEvaluacion.class));
    }

    @Test
    void registrar_conEvaluacionInexistente_lanzaNotFound() {
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> intentoEvaluacionService.registrar(10L, request));
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void registrar_conUsuarioInexistente_lanzaNotFound() {
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> intentoEvaluacionService.registrar(10L, request));
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void registrar_conUsuarioQueNoEsEstudiante_lanzaReglaDeNegocio() {
        Rol rolProfesor = new Rol();
        rolProfesor.setNombreRol("PROFESOR");
        estudiante.setRol(rolProfesor);

        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> intentoEvaluacionService.registrar(10L, request)
        );

        assertEquals("El usuario no tiene rol ESTUDIANTE", excepcion.getMessage());
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void registrar_conEstudianteDesactivado_lanzaReglaDeNegocio() {
        estudiante.setEstado(false);

        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> intentoEvaluacionService.registrar(10L, request)
        );

        assertEquals("El estudiante está desactivado", excepcion.getMessage());
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void registrar_sinInscripcionActiva_lanzaReglaDeNegocio() {
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(false);
        when(evaluacionService.validarDisponible(10L, 4L, false))
                .thenThrow(new BusinessRuleException(
                        "El estudiante no está inscrito en el curso de la evaluación"));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> intentoEvaluacionService.registrar(10L, request)
        );

        assertEquals("El estudiante no está inscrito en el curso de la evaluación",
                excepcion.getMessage());
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void registrar_conFechaLimiteVencida_lanzaReglaDeNegocio() {
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(true);
        when(evaluacionService.validarDisponible(10L, 4L, true))
                .thenThrow(new BusinessRuleException(
                        "La evaluación ya no está disponible: venció la fecha límite"));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> intentoEvaluacionService.registrar(10L, request)
        );

        assertEquals("La evaluación ya no está disponible: venció la fecha límite",
                excepcion.getMessage());
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void registrar_conIntentoDuplicado_lanzaReglaDeNegocio() {
        datosValidos();
        when(intentoRepository
                .existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(4L, 10L))
                .thenReturn(true);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> intentoEvaluacionService.registrar(10L, request)
        );

        assertEquals("El estudiante ya realizó esta evaluación", excepcion.getMessage());
        verify(intentoRepository, never()).save(any());
    }

    @Test
    void listarPorEstudiante_DebeRetornarLosIntentos() {
        when(intentoRepository.findByUsuario_IdUsuario(4L)).thenReturn(List.of(intento));

        List<IntentoEvaluacionResponseDTO> resultado =
                intentoEvaluacionService.listarPorEstudiante(4L);

        assertEquals(1, resultado.size());
        assertEquals(50L, resultado.get(0).getIdIntento());
        assertEquals(4L, resultado.get(0).getIdUsuario());
        assertEquals("Evaluación Final", resultado.get(0).getTituloEvaluacion());
        verify(intentoRepository).findByUsuario_IdUsuario(4L);
    }

    @Test
    void listarPorEvaluacion_DebeRetornarLosIntentos() {
        when(intentoRepository.findByEvaluacion_IdEvaluacion(10L)).thenReturn(List.of(intento));

        List<IntentoEvaluacionResponseDTO> resultado =
                intentoEvaluacionService.listarPorEvaluacion(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getIdEvaluacion());
        verify(intentoRepository).findByEvaluacion_IdEvaluacion(10L);
    }
}
