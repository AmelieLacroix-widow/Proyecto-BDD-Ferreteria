package com.ferreteria.alanis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.ferreteria.alanis",
    "com.miempresa.ferreteria"
})

@EnableJpaRepositories(basePackages = "com.miempresa.ferreteria.repository")
@EntityScan(basePackages = "com.miempresa.ferreteria.model")

public class AlanisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlanisApplication.class, args);
    }

}
