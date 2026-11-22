package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.RespuestaRequestDTO;
import com.ues.api_cursos_online.dto.RespuestaResponseDTO;
import com.ues.api_cursos_online.entity.Pregunta;
import com.ues.api_cursos_online.entity.Respuesta;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.PreguntaRepository;
import com.ues.api_cursos_online.repository.RespuestaRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Lógica de negocio del CRUD de Respuesta. */
@Service
@RequiredArgsConstructor
public class RespuestaService {

    private static final String ROL_PROFESOR = "PROFESOR";

    private final RespuestaRepository respuestaRepository;
    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;

    /** Lista las respuestas; los filtros por pregunta y por profesor son opcionales. */
    @Transactional(readOnly = true)
    public List<RespuestaResponseDTO> listar(Long idPregunta, Long idUsuario) {
        if (idPregunta != null) {
            buscarPregunta(idPregunta);
        }
        if (idUsuario != null) {
            buscarUsuario(idUsuario);
        }
        return respuestaRepository.buscar(idPregunta, idUsuario).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RespuestaResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarRespuesta(id));
    }

    @Transactional
    public RespuestaResponseDTO crear(RespuestaRequestDTO dto) {
        Pregunta pregunta = buscarPregunta(dto.getIdPregunta());
        Usuario profesor = obtenerProfesorActivo(dto.getIdUsuario());

        Respuesta respuesta = Respuesta.builder()
                .pregunta(pregunta)
                .usuario(profesor)
                .contenido(dto.getContenido().trim())
                .fecha(LocalDateTime.now())
                .build();
        return toResponse(respuestaRepository.save(respuesta));
    }

    @Transactional
    public RespuestaResponseDTO actualizar(Long id, RespuestaRequestDTO dto) {
        Respuesta respuesta = buscarRespuesta(id);
        Pregunta pregunta = buscarPregunta(dto.getIdPregunta());
        Usuario profesor = obtenerProfesorActivo(dto.getIdUsuario());

        respuesta.setPregunta(pregunta);
        respuesta.setUsuario(profesor);
        respuesta.setContenido(dto.getContenido().trim());
        return toResponse(respuestaRepository.save(respuesta));
    }

    @Transactional
    public void eliminar(Long id) {
        respuestaRepository.delete(buscarRespuesta(id));
    }

    // ---------- métodos auxiliares ----------

    private Respuesta buscarRespuesta(Long id) {
        return respuestaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Respuesta", id));
    }

    private Pregunta buscarPregunta(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta", id));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    /** Solo un usuario activo con rol PROFESOR puede responder preguntas. */
    private Usuario obtenerProfesorActivo(Long idUsuario) {
        Usuario usuario = buscarUsuario(idUsuario);
        if (!ROL_PROFESOR.equals(usuario.getRol().getNombreRol())) {
            throw new BusinessRuleException("El usuario con id " + idUsuario + " no tiene rol PROFESOR");
        }
        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new BusinessRuleException("El profesor con id " + idUsuario + " está desactivado");
        }
        return usuario;
    }

    private RespuestaResponseDTO toResponse(Respuesta respuesta) {
        Usuario profesor = respuesta.getUsuario();
        return RespuestaResponseDTO.builder()
                .idRespuesta(respuesta.getIdRespuesta())
                .idPregunta(respuesta.getPregunta().getIdPregunta())
                .idUsuario(profesor.getIdUsuario())
                .nombreProfesor(profesor.getNombreUsuario() + " " + profesor.getApellidoUsuario())
                .contenido(respuesta.getContenido())
                .fecha(respuesta.getFecha())
                .build();
    }
}
