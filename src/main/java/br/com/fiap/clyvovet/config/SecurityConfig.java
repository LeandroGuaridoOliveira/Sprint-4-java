package br.com.fiap.clyvovet.config;

import br.com.fiap.clyvovet.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioDetailsService userDetailsService;

    public SecurityConfig(UsuarioDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos e utilitários
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                
                // Rotas da API REST (para compatibilidade total com o aplicativo Mobile)
                .requestMatchers("/api/**").permitAll()
                
                // Documentação OpenAPI e Console H2
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/h2-console/**").permitAll()
                
                // Autenticação e páginas públicas
                .requestMatchers("/login", "/403", "/erro").permitAll()
                
                // Atendimento clínico e evolução de prontuário médico (Exclusivo para Veterinários)
                .requestMatchers("/atendimentos/**", "/fluxo/consulta/**").hasRole("VET")
                
                // Triagem clínica e solicitação de consultas (Tutores e Veterinários)
                .requestMatchers("/agendamentos/novo", "/fluxo/agendamento/**").hasAnyRole("TUTOR", "VET")
                
                // Dashboard e áreas internas autenticadas
                .requestMatchers("/", "/dashboard", "/pets/**", "/agendamentos/**", "/clinicas/**").authenticated()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/403")
            );

        return http.build();
    }
}
