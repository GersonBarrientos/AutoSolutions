package com.autosolutions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF habilitado con cookie (útil para formularios Thymeleaf)
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            // Autorizaciones
            .authorizeHttpRequests(auth -> auth
                // ✅ Recursos estáticos en ubicaciones comunes (static/, public/, META-INF/resources/, resources/)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // WebJars y favicon
                .requestMatchers("/webjars/**", "/favicon.ico").permitAll()
                // Páginas públicas
                .requestMatchers("/login", "/error").permitAll()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Login clásico con página personalizada
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .failureUrl("/login?error")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Cabeceras mínimas
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
