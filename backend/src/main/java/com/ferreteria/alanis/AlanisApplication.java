package com.ferreteria.alanis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.ferreteria.alanis",
    "com.miempresa.ferreteria"
})
public class AlanisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlanisApplication.class, args);
    }

}
