package org.example.transferencia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransferenciaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "mysql");
        SpringApplication.run(TransferenciaApplication.class, args);
    }

    @Override
    public void run(String... args) {}
}
