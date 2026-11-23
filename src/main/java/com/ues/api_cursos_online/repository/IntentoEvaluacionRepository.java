package com.ues.api_cursos_online.repository;

import com.ues.api_cursos_online.entity.IntentoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntentoEvaluacionRepository extends JpaRepository<IntentoEvaluacion, Long> {

    boolean existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(Long idUsuario, Long idEvaluacion);

    List<IntentoEvaluacion> findByUsuario_IdUsuario(Long idUsuario);

    List<IntentoEvaluacion> findByEvaluacion_IdEvaluacion(Long idEvaluacion);
}
