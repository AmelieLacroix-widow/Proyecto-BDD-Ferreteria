package com.miempresa.ferreteria.service;

public interface PasswordService {

    String hash(String passwordPlano);

    boolean verificar(String passwordPlano, String passwordGuardado);
}