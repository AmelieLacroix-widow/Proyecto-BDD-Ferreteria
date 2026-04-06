package com.ferreteria.alanis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.ferreteria.alanis",
    "com.miempresa.ferreteria"
})

@EnableJpaRepositories(basePackages = "com.miempresa.ferreteria.repository")

public class AlanisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlanisApplication.class, args);
    }

}
