package br.com.fiap.clyvovet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * As regras de agenda dependem do instante atual. Expor o relógio como bean permite
 * fixá-lo nos testes e manter as validações temporais determinísticas.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
