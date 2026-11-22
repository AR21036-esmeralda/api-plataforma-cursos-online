package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.PreguntaRequestDTO;
import com.ues.api_cursos_online.dto.PreguntaResponseDTO;
import com.ues.api_cursos_online.entity.Leccion;
import com.ues.api_cursos_online.entity.Pregunta;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.LeccionRepository;
import com.ues.api_cursos_online.repository.PreguntaRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Lógica de negocio del CRUD de Pregunta. */
@Service
@RequiredArgsConstructor
public class PreguntaService {

    private static final String ROL_ESTUDIANTE = "ESTUDIANTE";

    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeccionRepository leccionRepository;

    /** Lista las preguntas; los filtros por lección y por estudiante son opcionales. */
    @Transactional(readOnly = true)
    public List<PreguntaResponseDTO> listar(Long idLeccion, Long idUsuario) {
        if (idLeccion != null) {
            buscarLeccion(idLeccion);
        }
        if (idUsuario != null) {
            buscarUsuario(idUsuario);
        }
        return preguntaRepository.buscar(idLeccion, idUsuario).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PreguntaResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarPregunta(id));
    }

    @Transactional
    public PreguntaResponseDTO crear(PreguntaRequestDTO dto) {
        Leccion leccion = buscarLeccion(dto.getIdLeccion());
        Usuario estudiante = obtenerEstudianteActivo(dto.getIdUsuario());

        Pregunta pregunta = Pregunta.builder()
                .usuario(estudiante)
                .leccion(leccion)
                .contenido(dto.getContenido().trim())
                .fecha(LocalDateTime.now())
                .build();
        return toResponse(preguntaRepository.save(pregunta));
    }

    @Transactional
    public PreguntaResponseDTO actualizar(Long id, PreguntaRequestDTO dto) {
        Pregunta pregunta = buscarPregunta(id);
        Leccion leccion = buscarLeccion(dto.getIdLeccion());
        Usuario estudiante = obtenerEstudianteActivo(dto.getIdUsuario());

        pregunta.setUsuario(estudiante);
        pregunta.setLeccion(leccion);
        pregunta.setContenido(dto.getContenido().trim());
        return toResponse(preguntaRepository.save(pregunta));
    }

    @Transactional
    public void eliminar(Long id) {
        // Las respuestas de la pregunta se eliminan en cascada (ON DELETE CASCADE)
        preguntaRepository.delete(buscarPregunta(id));
    }

    // ---------- métodos auxiliares ----------

    private Pregunta buscarPregunta(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta", id));
    }

    private Leccion buscarLeccion(Long id) {
        return leccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lección", id));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    /** Solo un usuario activo con rol ESTUDIANTE puede hacer preguntas. */
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

    private PreguntaResponseDTO toResponse(Pregunta pregunta) {
        Usuario estudiante = pregunta.getUsuario();
        return PreguntaResponseDTO.builder()
                .idPregunta(pregunta.getIdPregunta())
                .idUsuario(estudiante.getIdUsuario())
                .nombreEstudiante(estudiante.getNombreUsuario() + " " + estudiante.getApellidoUsuario())
                .idLeccion(pregunta.getLeccion().getIdLeccion())
                .tituloLeccion(pregunta.getLeccion().getTitulo())
                .contenido(pregunta.getContenido())
                .fecha(pregunta.getFecha())
                .build();
    }
}
