package com.miempresa.ferreteria.service;

import org.springframework.stereotype.Service;

@Service
public class PlainTextPasswordService implements PasswordService {

    @Override
    public String hash(String passwordPlano) {
        return passwordPlano; // ⚠️ sin encriptar (temporal)
    }

    @Override
    public boolean verificar(String passwordPlano, String passwordGuardado) {
        return passwordPlano.equals(passwordGuardado);
    }
}