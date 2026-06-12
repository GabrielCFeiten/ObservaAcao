package com.obs.observaAcao.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/h2-console/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/auth/recuperar-senha").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/usuarios/cadastro").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/solicitacoes/anonima").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/categorias").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/contatos").permitAll();
                    auth.requestMatchers(HttpMethod.GET,
                            "/", "/*.html", "/css/**", "/js/**", "/favicon.ico").permitAll();
                    auth.requestMatchers(HttpMethod.PATCH, "/solicitacoes/*/status").hasRole("GESTOR");
                    auth.requestMatchers(HttpMethod.PATCH, "/usuarios/*/tipo").hasRole("GESTOR");
                    auth.requestMatchers(HttpMethod.GET, "/usuarios/gestores").hasRole("GESTOR");
                    auth.requestMatchers(HttpMethod.PUT, "/contatos").hasRole("GESTOR");
                    auth.anyRequest().authenticated();
                })
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
