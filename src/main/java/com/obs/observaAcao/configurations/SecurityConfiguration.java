package com.obs.observaAcao.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
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

    @Autowired
    private Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        boolean devProfile = environment.acceptsProfiles(Profiles.of("dev"));

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // Rotas públicas
                    auth.requestMatchers("/h2-console/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/auth/recuperar-senha").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/usuarios/cadastro").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/solicitacoes/anonima").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/categorias").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/contatos").permitAll();

                    // Arquivos estáticos do front (quando servido pelo próprio backend)
                    auth.requestMatchers(HttpMethod.GET,
                            "/", "/*.html", "/css/**", "/js/**", "/favicon.ico").permitAll();

                    // ===== DEVELOPMENT ONLY =====
                    // O atalho de login só é liberado quando o profile "dev" está ativo.
                    // Em produção esta regra não é registrada e a rota não existe.
                    if (devProfile) {
                        auth.requestMatchers(HttpMethod.POST, "/auth/dev-login").permitAll();
                    }
                    // ============================

                    // Apenas GESTOR
                    auth.requestMatchers(HttpMethod.PATCH, "/solicitacoes/*/status").hasRole("GESTOR");
                    auth.requestMatchers(HttpMethod.PATCH, "/usuarios/*/tipo").hasRole("GESTOR");
                    auth.requestMatchers(HttpMethod.GET, "/usuarios/gestores").hasRole("GESTOR");
                    auth.requestMatchers(HttpMethod.PUT, "/contatos").hasRole("GESTOR");

                    // Demais rotas exigem autenticação
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
