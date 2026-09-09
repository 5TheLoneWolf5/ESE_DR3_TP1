package org.example.conta;

import org.example.conta.application.ContaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrudT1Application implements CommandLineRunner {

    private final ContaService contaService;

    public CrudT1Application(ContaService contaService) {
        this.contaService = contaService;
    }

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "mysql");
        SpringApplication.run(CrudT1Application.class, args);
    }

    @Override
    public void run(String... args) {}
}
