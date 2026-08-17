package com.cscreativ.billboard.user.config;

import com.cscreativ.billboard.user.infrastructure.security.JwtAuthenticationFilter;
import com.cscreativ.billboard.user.infrastructure.security.JwtTokenProvider;
import com.cscreativ.billboard.user.infrastructure.security.oauth.MapClientRegistrationRepository;
import com.cscreativ.billboard.user.infrastructure.security.oauth.OAuth2AuthenticationFailureHandler;
import com.cscreativ.billboard.user.infrastructure.security.oauth.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
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

    /**
     * Construite programmatiquement plutôt que via les propriétés standard
     * spring.security.oauth2.client.registration.* : celles-ci font planter le démarrage dès qu'un
     * client-id/secret est vide, ce qui casserait l'app pour quiconque n'a pas encore configuré de
     * credentials Google/Facebook. Ici, chaque provider n'est enregistré que si ses deux variables
     * d'environnement sont renseignées ; la liste peut donc rester vide sans empêcher le démarrage
     * (voir MapClientRegistrationRepository, qui accepte une liste vide contrairement à
     * InMemoryClientRegistrationRepository).
     * <p>
     * Endpoints déclarés à la main (plutôt que via {@code CommonOAuth2Provider}, supprimé en
     * Spring Security 7) : ce sont les endpoints OAuth2 publics et stables de chaque provider.
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            @Value("${GOOGLE_CLIENT_ID:}") String googleClientId,
            @Value("${GOOGLE_CLIENT_SECRET:}") String googleClientSecret,
            @Value("${FACEBOOK_CLIENT_ID:}") String facebookClientId,
            @Value("${FACEBOOK_CLIENT_SECRET:}") String facebookClientSecret) {
        List<ClientRegistration> registrations = new ArrayList<>();

        if (!googleClientId.isBlank() && !googleClientSecret.isBlank()) {
            registrations.add(ClientRegistration.withRegistrationId("google")
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("email", "profile")
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                    .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .userNameAttributeName("sub")
                    .clientName("Google")
                    .build());
        }
        if (!facebookClientId.isBlank() && !facebookClientSecret.isBlank()) {
            registrations.add(ClientRegistration.withRegistrationId("facebook")
                    .clientId(facebookClientId)
                    .clientSecret(facebookClientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("email", "public_profile")
                    .authorizationUri("https://www.facebook.com/v19.0/dialog/oauth")
                    .tokenUri("https://graph.facebook.com/v19.0/oauth/access_token")
                    // "fields" explicite : sans lui, Facebook peut omettre des attributs dont on a
                    // besoin pour créer/rattacher le compte (voir OAuth2AuthenticationSuccessHandler).
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,first_name,last_name")
                    .userNameAttributeName("id")
                    .clientName("Facebook")
                    .build());
        }

        return new MapClientRegistrationRepository(registrations);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
            OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) throws Exception {
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
                        // Flux OAuth2 (Google/Facebook) : initiation + retour du provider, jamais avec un JWT.
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        // Catalogue public de panneaux (page d'accueil + /billboards, accessibles sans
                        // connexion) : consultation seule, la création/modification reste protégée par
                        // les vérifications de propriétaire déjà faites dans BillboardController.
                        .requestMatchers(HttpMethod.GET, "/api/v1/billboards/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cities/**").permitAll()
                        // Avis publiés (note moyenne + liste) affichés sur la fiche panneau publique.
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/target/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository)
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
