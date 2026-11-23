package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.InscripcionRequestDTO;
import com.ues.api_cursos_online.dto.InscripcionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Inscripcion;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InscripcionService {

    private static final String ROL_ESTUDIANTE = "ESTUDIANTE";

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    public List<InscripcionResponseDTO> listar() {
        return inscripcionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public InscripcionResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidad(id));
    }

    @Transactional
    public InscripcionResponseDTO crear(InscripcionRequestDTO dto) {
        Usuario usuario = obtenerEstudianteActivo(dto.getIdUsuario());
        Curso curso = obtenerCursoActivo(dto.getIdCurso());

        /*
         * Si el estudiante se dio de baja antes, se reactiva la inscripción
         * existente. Así se conserva el historial y se respeta el UNIQUE
         * (idUsuario, idCurso) de la base de datos.
         */
        var inscripcionAnterior = inscripcionRepository
                .findByUsuario_IdUsuarioAndCurso_IdCurso(dto.getIdUsuario(), dto.getIdCurso());

        if (inscripcionAnterior.isPresent()) {
            Inscripcion anterior = inscripcionAnterior.get();
            if (Boolean.TRUE.equals(anterior.getEstado())) {
                throw new BusinessRuleException("El usuario ya está inscrito en este curso");
            }
            anterior.setEstado(true);
            anterior.setFechaInscripcion(LocalDateTime.now());
            return toDTO(inscripcionRepository.save(anterior));
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .usuario(usuario)
                .curso(curso)
                .estado(true)
                .fechaInscripcion(LocalDateTime.now())
                .build();

        return toDTO(inscripcionRepository.save(inscripcion));
    }

    @Transactional
    public InscripcionResponseDTO actualizar(Long id, InscripcionRequestDTO dto) {
        Inscripcion inscripcion = buscarEntidad(id);

        Usuario usuario = obtenerEstudianteActivo(dto.getIdUsuario());
        Curso curso = obtenerCursoActivo(dto.getIdCurso());

        boolean cambioUsuario = !inscripcion.getUsuario().getIdUsuario().equals(dto.getIdUsuario());
        boolean cambioCurso = !inscripcion.getCurso().getIdCurso().equals(dto.getIdCurso());

        if (cambioUsuario || cambioCurso) {
            boolean existe = inscripcionRepository
                    .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(
                            dto.getIdUsuario(), dto.getIdCurso());
            if (existe) {
                throw new BusinessRuleException("El usuario ya está inscrito en este curso");
            }
        }

        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(curso);
        return toDTO(inscripcionRepository.save(inscripcion));
    }

    /** CU13 - Registrar la baja de un curso (baja lógica). */
    @Transactional
    public InscripcionResponseDTO darDeBaja(Long id) {
        Inscripcion inscripcion = buscarEntidad(id);
        if (!Boolean.TRUE.equals(inscripcion.getEstado())) {
            throw new BusinessRuleException("La inscripción ya estaba dada de baja");
        }
        inscripcion.setEstado(false);
        return toDTO(inscripcionRepository.save(inscripcion));
    }

    @Transactional
    public InscripcionResponseDTO reactivar(Long id) {
        Inscripcion inscripcion = buscarEntidad(id);
        inscripcion.setEstado(true);
        return toDTO(inscripcionRepository.save(inscripcion));
    }

    /** Eliminación física disponible para el administrador. */
    @Transactional
    public void eliminar(Long id) {
        inscripcionRepository.delete(buscarEntidad(id));
    }

    public List<InscripcionResponseDTO> listarPorEstudiante(Long idUsuario) {
        return listarPorEstudiante(idUsuario, false);
    }

    public List<InscripcionResponseDTO> listarPorEstudiante(Long idUsuario, boolean incluirInactivas) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario", idUsuario);
        }
        List<Inscripcion> inscripciones = incluirInactivas
                ? inscripcionRepository.findByUsuario_IdUsuario(idUsuario)
                : inscripcionRepository.findByUsuario_IdUsuarioAndEstadoTrue(idUsuario);
        return inscripciones.stream().map(this::toDTO).toList();
    }

    public List<InscripcionResponseDTO> listarPorCurso(Long idCurso) {
        return listarPorCurso(idCurso, false);
    }

    public List<InscripcionResponseDTO> listarPorCurso(Long idCurso, boolean incluirInactivas) {
        if (!cursoRepository.existsById(idCurso)) {
            throw new ResourceNotFoundException("Curso", idCurso);
        }
        List<Inscripcion> inscripciones = incluirInactivas
                ? inscripcionRepository.findByCurso_IdCurso(idCurso)
                : inscripcionRepository.findByCurso_IdCursoAndEstadoTrue(idCurso);
        return inscripciones.stream().map(this::toDTO).toList();
    }

    /** Verifica que un estudiante activo esté inscrito en un curso. */
    public boolean estaInscritoActivo(Long idUsuario, Long idCurso) {
        return inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(idUsuario, idCurso);
    }

    private Usuario obtenerEstudianteActivo(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", idUsuario));
        if (!ROL_ESTUDIANTE.equals(usuario.getRol().getNombreRol())) {
            throw new BusinessRuleException("El usuario con id " + idUsuario + " no tiene rol ESTUDIANTE");
        }
        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new BusinessRuleException("El estudiante con id " + idUsuario + " está desactivado");
        }
        return usuario;
    }

    private Curso obtenerCursoActivo(Long idCurso) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", idCurso));
        if (!Boolean.TRUE.equals(curso.getEstado())) {
            throw new BusinessRuleException("El curso con id " + idCurso + " está desactivado");
        }
        return curso;
    }

    private Inscripcion buscarEntidad(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripcion", id));
    }

    private InscripcionResponseDTO toDTO(Inscripcion inscripcion) {
        InscripcionResponseDTO dto = new InscripcionResponseDTO();
        dto.setIdInscripcion(inscripcion.getIdInscripcion());
        dto.setIdUsuario(inscripcion.getUsuario().getIdUsuario());
        dto.setNombreUsuario(inscripcion.getUsuario().getNombreUsuario());
        dto.setApellidoUsuario(inscripcion.getUsuario().getApellidoUsuario());
        dto.setIdCurso(inscripcion.getCurso().getIdCurso());
        dto.setNombreCurso(inscripcion.getCurso().getNombreCurso());
        dto.setFechaInscripcion(inscripcion.getFechaInscripcion());
        dto.setEstado(inscripcion.getEstado());
        return dto;
    }
}
