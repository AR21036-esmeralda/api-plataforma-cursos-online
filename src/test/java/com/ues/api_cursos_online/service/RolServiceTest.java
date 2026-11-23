package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.RolRequestDTO;
import com.ues.api_cursos_online.dto.RolResponseDTO;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private Rol rol;
    private RolRequestDTO request;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol("ESTUDIANTE");

        request = new RolRequestDTO();
        request.setNombreRol(" estudiante ");
    }

    @Test
    void crear_conNombreValido_normalizaElNombreYGurda() {
        when(rolRepository.existsByNombreRol("ESTUDIANTE")).thenReturn(false);
        when(rolRepository.save(any(Rol.class)))
                .thenAnswer(inv -> {
                    Rol guardado = inv.getArgument(0);
                    guardado.setIdRol(1L);
                    return guardado;
                });

        RolResponseDTO respuesta = rolService.crear(request);

        assertEquals(1L, respuesta.getIdRol());
        assertEquals("ESTUDIANTE", respuesta.getNombreRol());
        verify(rolRepository).save(any(Rol.class));
    }

    @Test
    void crear_conNombreNoPermitido_lanzaReglaDeNegocio() {
        request.setNombreRol("INVITADO");

        assertThrows(BusinessRuleException.class, () -> rolService.crear(request));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void crear_conNombreVacio_lanzaReglaDeNegocio() {
        request.setNombreRol("  ");

        assertThrows(BusinessRuleException.class, () -> rolService.crear(request));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void crear_conRolDuplicado_lanzaReglaDeNegocio() {
        when(rolRepository.existsByNombreRol("ESTUDIANTE")).thenReturn(true);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> rolService.crear(request)
        );

        assertEquals("El rol ESTUDIANTE ya existe", excepcion.getMessage());
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void obtenerPorId_cuandoExiste_devuelveRol() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        RolResponseDTO respuesta = rolService.obtenerPorId(1L);

        assertEquals("ESTUDIANTE", respuesta.getNombreRol());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_lanzaNotFound() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rolService.obtenerPorId(99L));
    }

    @Test
    void actualizar_conNombreDeOtroRol_lanzaReglaDeNegocio() {
        Rol otro = new Rol();
        otro.setIdRol(2L);
        otro.setNombreRol("PROFESOR");

        request.setNombreRol("PROFESOR");
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(rolRepository.findByNombreRol("PROFESOR")).thenReturn(Optional.of(otro));

        assertThrows(BusinessRuleException.class, () -> rolService.actualizar(1L, request));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void listar_devuelveTodosLosRoles() {
        when(rolRepository.findAll()).thenReturn(List.of(rol));

        List<RolResponseDTO> resultado = rolService.listar();

        assertEquals(1, resultado.size());
        assertEquals("ESTUDIANTE", resultado.get(0).getNombreRol());
    }

    @Test
    void eliminar_cuandoExiste_borraElRol() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        rolService.eliminar(1L);

        verify(rolRepository).deleteById(1L);
    }
}
