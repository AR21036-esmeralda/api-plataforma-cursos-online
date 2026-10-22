package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeccionRepository extends JpaRepository<Leccion, Long> {

    List<Leccion> findByCurso_IdCursoOrderByOrdenAsc(Long idCurso);

    boolean existsByCurso_IdCursoAndOrden(Long idCurso, Integer orden);

    boolean existsByCurso_IdCursoAndOrdenAndIdLeccionNot(
            Long idCurso, Integer orden, Long idLeccion);
}
