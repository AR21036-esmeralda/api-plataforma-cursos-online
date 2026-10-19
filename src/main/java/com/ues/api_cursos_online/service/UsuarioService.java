package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.UsuarioRequestDTO;
import com.ues.api_cursos_online.dto.UsuarioResponseDTO;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.RolRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", dto.getIdRol()));

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setApellidoUsuario(dto.getApellidoUsuario());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setEstado(true);
        usuario.setRol(rol);

        usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", dto.getIdRol()));

        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setApellidoUsuario(dto.getApellidoUsuario());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setRol(rol);

        usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        usuarioRepository.deleteById(id);
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
