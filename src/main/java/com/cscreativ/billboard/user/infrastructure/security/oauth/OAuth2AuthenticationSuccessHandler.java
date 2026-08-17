package com.cscreativ.billboard.user.infrastructure.security.oauth;

import com.cscreativ.billboard.user.application.AuthenticationService;
import com.cscreativ.billboard.user.domain.exception.UserDisabledException;
import com.cscreativ.billboard.user.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Point d'arrivée commun du flux OAuth2 (Google, Facebook) une fois l'utilisateur authentifié côté
 * provider. Convertit le profil OAuth2 en JWT applicatif via {@link AuthenticationService}, exactement
 * comme {@code AuthController#login} le fait pour le login email/mot de passe, puis pose le même
 * cookie HttpOnly avant de rediriger vers le frontend.
 */
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider tokenProvider;
    private final String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(AuthenticationService authenticationService,
                                               JwtTokenProvider tokenProvider,
                                               @Value("${app.frontend-url}") String frontendUrl) {
        this.authenticationService = authenticationService;
        this.tokenProvider = tokenProvider;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuthProfile profile = extractProfile(registrationId, oauthToken.getPrincipal());

        if (profile.email() == null || profile.email().isBlank()) {
            // Facebook peut ne pas renvoyer d'email (compte sans email vérifié, ou permission refusée) :
            // impossible de rattacher/créer un compte sans identifiant stable côté métier.
            response.sendRedirect(frontendUrl + "/login?error=oauth_no_email");
            return;
        }

        try {
            String token = authenticationService.loginOrRegisterViaOAuth(
                    registrationId, profile.providerId(), profile.email(), profile.firstName(), profile.lastName());
            response.addHeader(HttpHeaders.SET_COOKIE, tokenProvider.buildAuthCookie(token).toString());
            response.sendRedirect(frontendUrl + "/space");
        } catch (UserDisabledException e) {
            response.sendRedirect(frontendUrl + "/login?error=account_disabled");
        }
    }

    private OAuthProfile extractProfile(String registrationId, OAuth2User oauthUser) {
        Map<String, Object> attrs = oauthUser.getAttributes();
        String fullName = asString(attrs.get("name"));
        String[] fallbackName = splitName(fullName);

        if ("google".equals(registrationId)) {
            return new OAuthProfile(
                    asString(attrs.get("sub")),
                    asString(attrs.get("email")),
                    orFallback(asString(attrs.get("given_name")), fallbackName[0]),
                    orFallback(asString(attrs.get("family_name")), fallbackName[1])
            );
        }
        // facebook (et tout autre provider ajouté plus tard avec le même schéma d'attributs)
        return new OAuthProfile(
                asString(attrs.get("id")),
                asString(attrs.get("email")),
                orFallback(asString(attrs.get("first_name")), fallbackName[0]),
                orFallback(asString(attrs.get("last_name")), fallbackName[1])
        );
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static String orFallback(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    /** Découpe le "name" complet en [prénom, nom] : dernier repli si le provider ne détaille pas les champs. */
    private static String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"Utilisateur", "OAuth"};
        }
        String trimmed = fullName.trim();
        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex < 0) {
            return new String[]{trimmed, "OAuth"};
        }
        return new String[]{trimmed.substring(0, spaceIndex), trimmed.substring(spaceIndex + 1)};
    }

    private record OAuthProfile(String providerId, String email, String firstName, String lastName) {}
}
