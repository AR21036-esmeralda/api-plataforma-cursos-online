package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.CategoriaRequestDTO;
import com.ues.api_cursos_online.dto.CategoriaResponseDTO;
import com.ues.api_cursos_online.entity.Categoria;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CategoriaRepository;
import com.ues.api_cursos_online.repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequestDTO request;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombreCategoria("Tecnología");
        categoria.setDescripcion("Cursos de tecnología");
        categoria.setEstado(true);

        request = new CategoriaRequestDTO(" Tecnología ", "Cursos de tecnología", null);
    }

    @Test
    void listar_DebeRetornarCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<CategoriaResponseDTO> resultado = categoriaService.listar();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdCategoria());
        assertEquals("Tecnología", resultado.get(0).getNombreCategoria());
        assertTrue(resultado.get(0).getEstado());
        verify(categoriaRepository).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DebeRetornarCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponseDTO resultado = categoriaService.obtenerPorId(1L);

        assertEquals(1L, resultado.getIdCategoria());
        assertEquals("Cursos de tecnología", resultado.getDescripcion());
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DebeLanzarNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoriaService.obtenerPorId(99L));
    }

    @Test
    void crear_SinEstado_DebeCrearCategoriaActivaYNombreSinEspacios() {
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(inv -> {
                    Categoria guardada = inv.getArgument(0);
                    guardada.setIdCategoria(10L);
                    return guardada;
                });

        CategoriaResponseDTO resultado = categoriaService.crear(request);

        assertEquals(10L, resultado.getIdCategoria());
        assertEquals("Tecnología", resultado.getNombreCategoria());
        assertTrue(resultado.getEstado());

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaRepository).save(captor.capture());
        assertEquals("Tecnología", captor.getValue().getNombreCategoria());
    }

    @Test
    void crear_ConEstadoInactivo_DebeRespetaElDto() {
        request.setEstado(false);
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CategoriaResponseDTO resultado = categoriaService.crear(request);

        assertFalse(resultado.getEstado());
    }

    @Test
    void actualizar_CuandoExiste_DebeModificarCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        request.setEstado(false);
        request.setNombreCategoria(" Nueva tecnología ");

        CategoriaResponseDTO resultado = categoriaService.actualizar(1L, request);

        assertEquals("Nueva tecnología", resultado.getNombreCategoria());
        assertFalse(resultado.getEstado());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void actualizar_CuandoNoExiste_DebeLanzarNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoriaService.actualizar(99L, request));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void desactivar_DebeCambiarElEstado() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CategoriaResponseDTO resultado = categoriaService.desactivar(1L);

        assertFalse(resultado.getEstado());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void reactivar_DebeCambiarElEstado() {
        categoria.setEstado(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CategoriaResponseDTO resultado = categoriaService.reactivar(1L);

        assertTrue(resultado.getEstado());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void eliminar_SinCursosAsociados_DebeBorrarCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(cursoRepository.existsByCategoria_IdCategoria(1L)).thenReturn(false);

        categoriaService.eliminar(1L);

        verify(categoriaRepository).delete(categoria);
    }

    @Test
    void eliminar_ConCursosAsociados_DebeLanzarReglaDeNegocio() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(cursoRepository.existsByCategoria_IdCategoria(1L)).thenReturn(true);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> categoriaService.eliminar(1L)
        );

        assertEquals("No se puede eliminar la categoría porque tiene cursos asociados",
                excepcion.getMessage());
        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }

    @Test
    void eliminar_CuandoNoExiste_DebeLanzarNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoriaService.eliminar(99L));
        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }
}
