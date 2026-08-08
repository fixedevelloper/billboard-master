package com.cscreativ.billboard.user.config;

import com.cscreativ.billboard.user.infrastructure.security.JwtAuthenticationFilter;
import com.cscreativ.billboard.user.infrastructure.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:3000}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        // Liste explicite d'origines (jamais "*") : requis pour pouvoir envoyer le cookie de
        // session cross-origin (frontend :3000 / backend :8080) via allowCredentials.
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        // Le JWT voyage désormais dans un cookie HttpOnly (voir JwtTokenProvider) : le navigateur
        // ne l'envoie en cross-origin que si la requête est faite avec credentials, et CORS ne
        // laisse passer les credentials qu'avec une origine explicite (incompatible avec "*").
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Healthcheck Docker/orchestrateur : doit rester accessible sans JWT, sinon le
                        // conteneur est déclaré "unhealthy" et les services qui en dépendent ne démarrent pas.
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Étapes de création de profil pendant l'inscription : appelées juste après
                        // POST /auth/register, avant toute connexion (donc sans JWT). Doivent rester
                        // publiques comme /auth/** ; les autres méthodes (GET/PUT/DELETE) restent protégées.
                        .requestMatchers(HttpMethod.POST, "/api/v1/advertisers", "/api/v1/owners", "/api/v1/media-buyers")
                        .permitAll()
                        // Retour navigateur + webhook Flutterwave : appelés par Flutterwave, jamais avec un JWT.
                        // Le webhook s'authentifie lui-même via le header verif-hash (voir PaymentController).
                        .requestMatchers("/api/v1/payments/flutterwave/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
