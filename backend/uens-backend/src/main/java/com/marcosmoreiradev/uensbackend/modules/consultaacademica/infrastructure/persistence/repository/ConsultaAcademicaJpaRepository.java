package com.marcosmoreiradev.uensbackend.modules.consultaacademica.infrastructure.persistence.repository;

import com.marcosmoreiradev.uensbackend.modules.clase.infrastructure.persistence.entity.ClaseJpaEntity;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model.DocentePorSeccionRow;
import com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model.SeccionPorDocenteRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Query repository especializado para lecturas academicas agregadas que no
 * encajan en un CRUD puntual.
 */
public interface ConsultaAcademicaJpaRepository extends Repository<ClaseJpaEntity, Long> {

    @Query("""
            select new com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model.DocentePorSeccionRow(
                d.id,
                d.nombres,
                d.apellidos,
                d.correoElectronico,
                d.telefono,
                d.estado,
                count(c.id)
            )
            from ClaseJpaEntity c
            join c.docente d
            join c.seccion s
            where s.id = :seccionId
              and c.estado = 'ACTIVO'
            group by d.id, d.nombres, d.apellidos, d.correoElectronico, d.telefono, d.estado
            order by d.apellidos asc, d.nombres asc
            """)
    List<DocentePorSeccionRow> findDocentesPorSeccionId(Long seccionId);

    @Query("""
            select new com.marcosmoreiradev.uensbackend.modules.consultaacademica.application.model.SeccionPorDocenteRow(
                s.id,
                s.grado,
                s.paralelo,
                s.anioLectivo,
                s.estado,
                count(c.id)
            )
            from ClaseJpaEntity c
            join c.seccion s
            join c.docente d
            where d.id = :docenteId
              and c.estado = 'ACTIVO'
            group by s.id, s.grado, s.paralelo, s.anioLectivo, s.estado
            order by s.anioLectivo desc, s.grado asc, s.paralelo asc
            """)
    List<SeccionPorDocenteRow> findSeccionesPorDocenteId(Long docenteId);
}
