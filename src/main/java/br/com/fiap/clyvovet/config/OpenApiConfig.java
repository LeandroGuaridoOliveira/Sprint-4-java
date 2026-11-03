package br.com.fiap.clyvovet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clyvoVetOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clyvo VET API")
                        .description("API RESTful para jornada contínua de saúde pet — Challenge FIAP 2026")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Clyvo VET")
                                .email("contato@clyvovet.com.br")));
    }
}
