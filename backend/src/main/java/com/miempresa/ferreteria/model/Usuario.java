package com.miempresa.ferreteria.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

// FIX: @JsonIgnoreProperties evita que Jackson falle al serializar proxies Hibernate
// (los que tienen "hibernateLazyInitializer" como pseudo-propiedad).
// Necesario porque Ticket tiene @ManyToOne a Usuario con FetchType.LAZY.
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "USUARIO")
public class Usuario {

    public Usuario() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario", length = 100, nullable = false, unique = true)
    private String nombreUsuario;

    // FIX: WRITE_ONLY en lugar de @JsonIgnore.
    //
    // El problema con @JsonIgnore es que bloquea el campo en AMBAS direcciones:
    //   - Serializacion   (Java -> JSON): el hash no sale en respuestas.  OK
    //   - Deserializacion (JSON -> Java): el hash tampoco ENTRA en @RequestBody. ROMPE LOGIN
    //
    // Con WRITE_ONLY Jackson solo ignora la serializacion (salida), pero si lee
    // el campo cuando llega en el body del POST /usuarios/login, por lo que
    // UsuarioController.login() recibe contrasenaHash correctamente.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "contrasena_hash", length = 255, nullable = false)
    private String contrasenaHash;

    @Column(name = "rol")
    private String rol;

    // --- Getters y Setters ---

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}