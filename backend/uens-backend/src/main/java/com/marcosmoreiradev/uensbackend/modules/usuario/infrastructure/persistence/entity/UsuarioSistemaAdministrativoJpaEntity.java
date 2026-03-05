package com.marcosmoreiradev.uensbackend.modules.usuario.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Representa la cuenta administrativa persistida en la tabla
 * {@code usuario_sistema_administrativo} definida por el SQL oficial.
 */
@Entity
@Table(name = "usuario_sistema_administrativo")
public class UsuarioSistemaAdministrativoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id")
    private Long id;

    @Column(name = "nombre_login", nullable = false, length = 80)
    private String nombreLogin;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    protected UsuarioSistemaAdministrativoJpaEntity() {
    }

    private UsuarioSistemaAdministrativoJpaEntity(String nombreLogin, String passwordHash, String rol, String estado) {
        this.nombreLogin = nombreLogin;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.estado = estado;
    }

    public static UsuarioSistemaAdministrativoJpaEntity crear(
            String nombreLogin,
            String passwordHash,
            String rol,
            String estado
    ) {
        return new UsuarioSistemaAdministrativoJpaEntity(nombreLogin, passwordHash, rol, estado);
    }

    public Long getId() {
        return id;
    }

    public String getNombreLogin() {
        return nombreLogin;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }

    public void actualizarIdentidad(String nombreLogin, String rol) {
        this.nombreLogin = nombreLogin;
        this.rol = rol;
    }

    public void actualizarCredencial(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void activar() {
        this.estado = "ACTIVO";
    }

    public void inactivar() {
        this.estado = "INACTIVO";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UsuarioSistemaAdministrativoJpaEntity that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
