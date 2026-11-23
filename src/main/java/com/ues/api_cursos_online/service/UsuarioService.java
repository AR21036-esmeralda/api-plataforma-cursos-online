package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.UsuarioRequestDTO;
import com.ues.api_cursos_online.dto.UsuarioResponseDTO;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.RolRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        return toDTO(buscarUsuario(id));
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("Ya existe un usuario con el correo " + dto.getEmail());
        }

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", dto.getIdRol()));

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setApellidoUsuario(dto.getApellidoUsuario());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : Boolean.TRUE);
        usuario.setRol(rol);

        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarUsuario(id);

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", dto.getIdRol()));

        usuarioRepository.findByEmail(dto.getEmail())
                .filter(otro -> !otro.getIdUsuario().equals(id))
                .ifPresent(otro -> {
                    throw new BusinessRuleException("Ya existe un usuario con el correo " + dto.getEmail());
                });

        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setApellidoUsuario(dto.getApellidoUsuario());
        usuario.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        usuario.setRol(rol);
        if (dto.getEstado() != null) {
            usuario.setEstado(dto.getEstado());
        }

        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO desactivar(Long id) {
        return cambiarEstado(id, false);
    }

    @Transactional
    public UsuarioResponseDTO reactivar(Long id) {
        return cambiarEstado(id, true);
    }

    private UsuarioResponseDTO cambiarEstado(Long id, boolean estado) {
        Usuario usuario = buscarUsuario(id);
        usuario.setEstado(estado);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        buscarUsuario(id);
        usuarioRepository.deleteById(id);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setApellidoUsuario(usuario.getApellidoUsuario());
        dto.setEmail(usuario.getEmail());
        dto.setEstado(usuario.getEstado());
        dto.setRol(usuario.getRol().getNombreRol());
        return dto;
    }
}
