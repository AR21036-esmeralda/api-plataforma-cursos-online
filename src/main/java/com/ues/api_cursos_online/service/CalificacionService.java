package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.CalificacionRequestDTO;
import com.ues.api_cursos_online.dto.CalificacionResponseDTO;
import com.ues.api_cursos_online.entity.Calificacion;
import com.ues.api_cursos_online.entity.Evaluacion;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CalificacionRepository;
import com.ues.api_cursos_online.repository.EvaluacionRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Lógica de negocio del CRUD de Calificación. */
@Service
@RequiredArgsConstructor
public class CalificacionService {

    private static final String ROL_ESTUDIANTE = "ESTUDIANTE";
    private static final String ROL_PROFESOR = "PROFESOR";

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final InscripcionRepository inscripcionRepository;

    /** Lista las calificaciones; los filtros son opcionales (nulo = sin filtrar). */
    @Transactional(readOnly = true)
    public List<CalificacionResponseDTO> listar(Long idUsuario, Long idEvaluacion) {
        if (idUsuario != null) {
            buscarUsuario(idUsuario);
        }
        if (idEvaluacion != null) {
            buscarEvaluacion(idEvaluacion);
        }
        return calificacionRepository.buscar(idUsuario, idEvaluacion).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CalificacionResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarCalificacion(id));
    }

    @Transactional
    public CalificacionResponseDTO crear(CalificacionRequestDTO dto) {
        Evaluacion evaluacion = buscarEvaluacion(dto.getIdEvaluacion());
        Usuario estudiante = obtenerEstudianteActivo(dto.getIdUsuario());
        validarInscripcion(estudiante.getIdUsuario(), evaluacion);
        validarProfesor(dto.getIdProfesor(), evaluacion);
        validarNoDuplicada(estudiante.getIdUsuario(), evaluacion.getIdEvaluacion(), null);

        Calificacion calificacion = Calificacion.builder()
                .usuario(estudiante)
                .evaluacion(evaluacion)
                .nota(dto.getNota())
                .fechaRealizacion(LocalDateTime.now())
                .build();
        try {
            return toResponse(calificacionRepository.save(calificacion));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("No se pudo crear la calificación. Verifique que el estudiante no tenga ya una nota en esta evaluación.");
        }
    }

    @Transactional
    public CalificacionResponseDTO actualizar(Long id, CalificacionRequestDTO dto) {
        Calificacion calificacion = buscarCalificacion(id);
        Evaluacion evaluacion = buscarEvaluacion(dto.getIdEvaluacion());
        Usuario estudiante = obtenerEstudianteActivo(dto.getIdUsuario());
        validarInscripcion(estudiante.getIdUsuario(), evaluacion);
        validarProfesor(dto.getIdProfesor(), evaluacion);
        validarNoDuplicada(estudiante.getIdUsuario(), evaluacion.getIdEvaluacion(), id);

        calificacion.setUsuario(estudiante);
        calificacion.setEvaluacion(evaluacion);
        calificacion.setNota(dto.getNota());
        calificacion.setFechaRealizacion(LocalDateTime.now());
        try {
            return toResponse(calificacionRepository.save(calificacion));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("No se pudo actualizar la calificación. Verifique que el estudiante no tenga ya una nota en esta evaluación.");
        }
    }

    @Transactional
    public void eliminar(Long id) {
        calificacionRepository.delete(buscarCalificacion(id));
    }

    // ---------- métodos auxiliares ----------

    private Calificacion buscarCalificacion(Long id) {
        return calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación", id));
    }

    private Evaluacion buscarEvaluacion(Long id) {
        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación", id));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    /** Solo un usuario activo con rol ESTUDIANTE puede recibir una calificación. */
    private Usuario obtenerEstudianteActivo(Long idUsuario) {
        Usuario usuario = buscarUsuario(idUsuario);
        if (!ROL_ESTUDIANTE.equals(usuario.getRol().getNombreRol())) {
            throw new BusinessRuleException("El usuario con id " + idUsuario + " no tiene rol ESTUDIANTE");
        }
        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new BusinessRuleException("El estudiante con id " + idUsuario + " está desactivado");
        }
        return usuario;
    }

    /** CU18: el estudiante debe estar inscrito activamente en el curso de la evaluación. */
    private void validarInscripcion(Long idUsuario, Evaluacion evaluacion) {
        Long idCurso = evaluacion.getCurso().getIdCurso();
        boolean inscrito = inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(idUsuario, idCurso);
        if (!inscrito) {
            throw new BusinessRuleException(
                    "El estudiante no está inscrito en el curso de la evaluación");
        }
    }

    /** CU18: si se indica el profesor, debe estar activo e impartir el curso. */
    private void validarProfesor(Long idProfesor, Evaluacion evaluacion) {
        if (idProfesor == null) {
            return;
        }
        Usuario profesor = buscarUsuario(idProfesor);
        if (!ROL_PROFESOR.equals(profesor.getRol().getNombreRol())) {
            throw new BusinessRuleException("El usuario con id " + idProfesor + " no tiene rol PROFESOR");
        }
        if (!Boolean.TRUE.equals(profesor.getEstado())) {
            throw new BusinessRuleException("El profesor con id " + idProfesor + " está desactivado");
        }
        Long profesorDelCurso = evaluacion.getCurso().getProfesor().getIdUsuario();
        if (!profesorDelCurso.equals(idProfesor)) {
            throw new BusinessRuleException("El profesor no imparte el curso de la evaluación");
        }
    }

    /** Un estudiante solo puede tener una calificación por evaluación. */
    private void validarNoDuplicada(Long idUsuario, Long idEvaluacion, Long idCalificacionActual) {
        boolean duplicada = idCalificacionActual == null
                ? calificacionRepository.existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(idUsuario, idEvaluacion)
                : calificacionRepository.existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacionAndIdCalificacionNot(
                        idUsuario, idEvaluacion, idCalificacionActual);
        if (duplicada) {
            throw new BusinessRuleException("El estudiante ya tiene una calificación en esta evaluación");
        }
    }

    private CalificacionResponseDTO toResponse(Calificacion calificacion) {
        Usuario estudiante = calificacion.getUsuario();
        return CalificacionResponseDTO.builder()
                .idCalificacion(calificacion.getIdCalificacion())
                .idUsuario(estudiante.getIdUsuario())
                .nombreEstudiante(estudiante.getNombreUsuario() + " " + estudiante.getApellidoUsuario())
                .idEvaluacion(calificacion.getEvaluacion().getIdEvaluacion())
                .tituloEvaluacion(calificacion.getEvaluacion().getTitulo())
                .nota(calificacion.getNota())
                .fechaRealizacion(calificacion.getFechaRealizacion())
                .build();
    }
}
