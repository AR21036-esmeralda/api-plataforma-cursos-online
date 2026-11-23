package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.EvaluacionResponseDTO;
import com.ues.api_cursos_online.dto.IntentoEvaluacionRequestDTO;
import com.ues.api_cursos_online.dto.IntentoEvaluacionResponseDTO;
import com.ues.api_cursos_online.entity.Evaluacion;
import com.ues.api_cursos_online.entity.IntentoEvaluacion;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.EvaluacionRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.IntentoEvaluacionRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Implementación del caso de uso CU10 - Realizar evaluaciones. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntentoEvaluacionService {

    private static final String ROL_ESTUDIANTE = "ESTUDIANTE";

    private final IntentoEvaluacionRepository intentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionService evaluacionService;

    @Transactional
    public IntentoEvaluacionResponseDTO registrar(Long idEvaluacion,
                                                   IntentoEvaluacionRequestDTO dto) {
        Evaluacion evaluacion = evaluacionRepository.findById(idEvaluacion)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación", idEvaluacion));

        Usuario estudiante = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getIdUsuario()));

        if (!ROL_ESTUDIANTE.equals(estudiante.getRol().getNombreRol())) {
            throw new BusinessRuleException("El usuario no tiene rol ESTUDIANTE");
        }
        if (!Boolean.TRUE.equals(estudiante.getEstado())) {
            throw new BusinessRuleException("El estudiante está desactivado");
        }

        boolean inscrito = inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(
                        dto.getIdUsuario(), evaluacion.getCurso().getIdCurso());

        EvaluacionResponseDTO evaluacionValidada = evaluacionService.validarDisponible(
                idEvaluacion, dto.getIdUsuario(), inscrito);

        if (intentoRepository.existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(
                dto.getIdUsuario(), idEvaluacion)) {
            throw new BusinessRuleException("El estudiante ya realizó esta evaluación");
        }

        IntentoEvaluacion intento = IntentoEvaluacion.builder()
                .usuario(estudiante)
                .evaluacion(evaluacion)
                .fechaRealizacion(LocalDateTime.now())
                .build();

        IntentoEvaluacion guardado = intentoRepository.save(intento);

        return IntentoEvaluacionResponseDTO.builder()
                .idIntento(guardado.getIdIntento())
                .idUsuario(estudiante.getIdUsuario())
                .nombreEstudiante(estudiante.getNombreUsuario() + " " + estudiante.getApellidoUsuario())
                .idEvaluacion(evaluacionValidada.getIdEvaluacion())
                .tituloEvaluacion(evaluacionValidada.getTitulo())
                .fechaRealizacion(guardado.getFechaRealizacion())
                .mensaje("Evaluación registrada correctamente")
                .build();
    }

    public List<IntentoEvaluacionResponseDTO> listarPorEstudiante(Long idUsuario) {
        return intentoRepository.findByUsuario_IdUsuario(idUsuario).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<IntentoEvaluacionResponseDTO> listarPorEvaluacion(Long idEvaluacion) {
        return intentoRepository.findByEvaluacion_IdEvaluacion(idEvaluacion).stream()
                .map(this::toResponse)
                .toList();
    }

    private IntentoEvaluacionResponseDTO toResponse(IntentoEvaluacion intento) {
        return IntentoEvaluacionResponseDTO.builder()
                .idIntento(intento.getIdIntento())
                .idUsuario(intento.getUsuario().getIdUsuario())
                .nombreEstudiante(intento.getUsuario().getNombreUsuario() + " "
                        + intento.getUsuario().getApellidoUsuario())
                .idEvaluacion(intento.getEvaluacion().getIdEvaluacion())
                .tituloEvaluacion(intento.getEvaluacion().getTitulo())
                .fechaRealizacion(intento.getFechaRealizacion())
                .build();
    }
}
