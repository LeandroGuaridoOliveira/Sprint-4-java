package br.com.fiap.clyvovet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ClyvovetApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClyvovetApiApplication.class, args);
    }
}
