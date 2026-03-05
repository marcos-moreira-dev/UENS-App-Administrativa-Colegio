package com.marcosmoreiradev.uensbackend.modules.auditoria.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "auditoria_evento")
public class AuditoriaEventoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id", nullable = false)
    private Long id;

    @Column(name = "modulo", nullable = false, length = 80)
    private String modulo;

    @Column(name = "accion", nullable = false, length = 120)
    private String accion;

    @Column(name = "entidad", length = 120)
    private String entidad;

    @Column(name = "entidad_id", length = 120)
    private String entidadId;

    @Column(name = "resultado", nullable = false, length = 20)
    private String resultado;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "request_id", length = 64)
    private String requestId;

    @Column(name = "ip_origen", length = 64)
    private String ipOrigen;

    @Column(name = "actor_usuario_id")
    private Long actorUsuarioId;

    @Column(name = "actor_login", length = 80)
    private String actorLogin;

    @Column(name = "actor_rol", length = 30)
    private String actorRol;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    protected AuditoriaEventoJpaEntity() {
    }

    private AuditoriaEventoJpaEntity(
            String modulo,
            String accion,
            String entidad,
            String entidadId,
            String resultado,
            String detalle,
            String requestId,
            String ipOrigen,
            Long actorUsuarioId,
            String actorLogin,
            String actorRol
    ) {
        this.modulo = modulo;
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.resultado = resultado;
        this.detalle = detalle;
        this.requestId = requestId;
        this.ipOrigen = ipOrigen;
        this.actorUsuarioId = actorUsuarioId;
        this.actorLogin = actorLogin;
        this.actorRol = actorRol;
    }

    public static AuditoriaEventoJpaEntity crear(
            String modulo,
            String accion,
            String entidad,
            String entidadId,
            String resultado,
            String detalle,
            String requestId,
            String ipOrigen,
            Long actorUsuarioId,
            String actorLogin,
            String actorRol
    ) {
        return new AuditoriaEventoJpaEntity(
                modulo,
                accion,
                entidad,
                entidadId,
                resultado,
                detalle,
                requestId,
                ipOrigen,
                actorUsuarioId,
                actorLogin,
                actorRol
        );
    }

    @PrePersist
    public void prePersist() {
        this.fechaEvento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getModulo() {
        return modulo;
    }

    public String getAccion() {
        return accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public String getEntidadId() {
        return entidadId;
    }

    public String getResultado() {
        return resultado;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public Long getActorUsuarioId() {
        return actorUsuarioId;
    }

    public String getActorLogin() {
        return actorLogin;
    }

    public String getActorRol() {
        return actorRol;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditoriaEventoJpaEntity that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}

