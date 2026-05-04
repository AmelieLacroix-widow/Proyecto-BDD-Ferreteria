package com.mycompany.ferreteria_alanis;

/**
 * Clase estática que guarda los datos del usuario autenticado.
 * Se puebla una sola vez en Login.java y es legible desde cualquier ventana.
 *
 * ─── PASO REQUERIDO EN Login.java ───────────────────────────────────────────
 * Antes de llamar a new MenuPrincipal(rol), agrega estas dos líneas:
 *
 *     SesionActual.setNombreUsuario(user);   // "user" ya existe en Login.java
 *     SesionActual.setRol(rol);
 *
 * ────────────────────────────────────────────────────────────────────────────
 */
public class SesionActual {

    private static String  nombreUsuario = "Usuario";
    private static String  rol           = "";
    private static int     idUsuario     = 0;

    private SesionActual() {}

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    public static void setNombreUsuario(String nombre) {
        nombreUsuario = (nombre != null && !nombre.isBlank()) ? nombre : "Usuario";
    }

    public static String getRol() {
        return rol;
    }

    public static void setRol(String r) {
        rol = (r != null) ? r : "";
    }

    public static int getIdUsuario() {
        return idUsuario;
    }

    public static void setIdUsuario(int id) {
        idUsuario = id;
    }
}