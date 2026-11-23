package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.InscripcionRequestDTO;
import com.ues.api_cursos_online.dto.InscripcionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Inscripcion;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    /**
     * Lista todas las inscripciones.
     */
    public List<InscripcionResponseDTO> listar() {

        return inscripcionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Busca una inscripción por ID.
     */
    public InscripcionResponseDTO buscarPorId(Long id) {

        Inscripcion inscripcion = buscarEntidad(id);

        return toDTO(inscripcion);
    }

    /**
     * Crea una nueva inscripción.
     */
    @Transactional
    public InscripcionResponseDTO crear(
            InscripcionRequestDTO dto
    ) {

        Usuario usuario = usuarioRepository
                .findById(dto.getIdUsuario())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario",
                                dto.getIdUsuario()
                        )
                );

        Curso curso = cursoRepository
                .findById(dto.getIdCurso())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Curso",
                                dto.getIdCurso()
                        )
                );

        /*
         * Evita que el mismo usuario se inscriba
         * dos veces en el mismo curso.
         */
        boolean yaInscrito =
                inscripcionRepository
                        .existsByUsuario_IdUsuarioAndCurso_IdCurso(
                                dto.getIdUsuario(),
                                dto.getIdCurso()
                        );

        if (yaInscrito) {

            throw new IllegalArgumentException(
                    "El usuario ya está inscrito en este curso"
            );
        }

        Inscripcion inscripcion =
                Inscripcion.builder()
                        .usuario(usuario)
                        .curso(curso)
                        .build();

        Inscripcion guardada =
                inscripcionRepository.save(inscripcion);

        return toDTO(guardada);
    }

    /**
     * Actualiza una inscripción existente.
     */
    @Transactional
    public InscripcionResponseDTO actualizar(
            Long id,
            InscripcionRequestDTO dto
    ) {

        Inscripcion inscripcion =
                buscarEntidad(id);

        Usuario usuario = usuarioRepository
                .findById(dto.getIdUsuario())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario",
                                dto.getIdUsuario()
                        )
                );

        Curso curso = cursoRepository
                .findById(dto.getIdCurso())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Curso",
                                dto.getIdCurso()
                        )
                );

        /*
         * Comprobamos si realmente se está intentando
         * cambiar el usuario o el curso.
         */
        boolean cambioUsuario =
                !inscripcion
                        .getUsuario()
                        .getIdUsuario()
                        .equals(dto.getIdUsuario());

        boolean cambioCurso =
                !inscripcion
                        .getCurso()
                        .getIdCurso()
                        .equals(dto.getIdCurso());

        /*
         * Si cambia alguna de las relaciones,
         * verificamos que la combinación no exista.
         */
        if (cambioUsuario || cambioCurso) {

            boolean existe =
                    inscripcionRepository
                            .existsByUsuario_IdUsuarioAndCurso_IdCurso(
                                    dto.getIdUsuario(),
                                    dto.getIdCurso()
                            );

            if (existe) {

                throw new IllegalArgumentException(
                        "El usuario ya está inscrito en este curso"
                );
            }
        }

        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(curso);

        Inscripcion actualizada =
                inscripcionRepository.save(inscripcion);

        return toDTO(actualizada);
    }

    /**
     * Elimina una inscripción por ID.
     */
    @Transactional
    public void eliminar(Long id) {

        Inscripcion inscripcion =
                buscarEntidad(id);

        inscripcionRepository.delete(inscripcion);
    }

    /**
     * Obtiene los cursos de un estudiante
     * a través de sus inscripciones.
     */
    public List<InscripcionResponseDTO> listarPorEstudiante(
            Long idUsuario
    ) {

        if (!usuarioRepository.existsById(idUsuario)) {

            throw new ResourceNotFoundException(
                    "Usuario",
                    idUsuario
            );
        }

        return inscripcionRepository
                .findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Obtiene los estudiantes inscritos
     * en un curso.
     */
    public List<InscripcionResponseDTO> listarPorCurso(
            Long idCurso
    ) {

        if (!cursoRepository.existsById(idCurso)) {

            throw new ResourceNotFoundException(
                    "Curso",
                    idCurso
            );
        }

        return inscripcionRepository
                .findByCurso_IdCurso(idCurso)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Busca internamente una inscripción por ID.
     */
    private Inscripcion buscarEntidad(Long id) {

        return inscripcionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inscripcion",
                                id
                        )
                );
    }

    /**
     * Convierte una entidad Inscripcion
     * en InscripcionResponseDTO.
     */
    private InscripcionResponseDTO toDTO(
            Inscripcion inscripcion
    ) {

        InscripcionResponseDTO dto =
                new InscripcionResponseDTO();

        dto.setIdInscripcion(
                inscripcion.getIdInscripcion()
        );

        // Usuario
        dto.setIdUsuario(
                inscripcion
                        .getUsuario()
                        .getIdUsuario()
        );

        dto.setNombreUsuario(
                inscripcion
                        .getUsuario()
                        .getNombreUsuario()
        );

        dto.setApellidoUsuario(
                inscripcion
                        .getUsuario()
                        .getApellidoUsuario()
        );

        // Curso
        dto.setIdCurso(
                inscripcion
                        .getCurso()
                        .getIdCurso()
        );

        dto.setNombreCurso(
                inscripcion
                        .getCurso()
                        .getNombreCurso()
        );

        // Datos de inscripción
        dto.setFechaInscripcion(
                inscripcion.getFechaInscripcion()
        );

        dto.setEstado(
                inscripcion.getEstado()
        );

        return dto;
    }
}